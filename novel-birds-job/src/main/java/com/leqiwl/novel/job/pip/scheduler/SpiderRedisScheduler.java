package com.leqiwl.novel.job.pip.scheduler;

import cn.hutool.core.util.StrUtil;
import com.leqiwl.novel.config.sysconst.RedisKeyConst;
import com.leqiwl.novel.config.sysconst.RequestConst;
import com.leqiwl.novel.domain.dto.CrawlerRequestDto;
import com.leqiwl.novel.domain.entify.crawler.CrawlerRule;
import com.leqiwl.novel.enums.CrawlerTypeEnum;
import com.leqiwl.novel.job.config.SpiderConfig;
import com.leqiwl.novel.service.CrawlerRuleService;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.*;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.DuplicateRemovedScheduler;
import us.codecraft.webmagic.scheduler.MonitorableScheduler;
import us.codecraft.webmagic.scheduler.component.DuplicateRemover;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * 自定义调度器，将下载器传递过来的请求保存到redis中，进行url去重，弹出请求
 * @author 飞鸟不过江
 */
@Slf4j
@Component
@NoArgsConstructor
public class SpiderRedisScheduler extends DuplicateRemovedScheduler implements MonitorableScheduler, DuplicateRemover {

    /**
     * 用于存放url的队列(info)
     */
    private static final String QUEUE_INFO_PREFIX = "queue_info_";

    /**
     * 用于存放url的队列(content)
     */
    private static final String QUEUE_CONTENT_PREFIX = "queue_content_";
    /**
     * 用于存放插队url的队列
     */
    private static final String QUEUE_JUMP = "jump_";
    /**
     * 用于对url去重
     */
    private static final String SET_PREFIX = "set_";

    @Resource
    private CrawlerRuleService crawlerRuleService;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private SpiderConfig spiderConfig;


    private RMap<String,String> queueMap;

    private RAtomicLong queueLeftTotal;

    @PostConstruct
    public void post(){
        queueLeftTotal = redissonClient.getAtomicLong("queueLeftTotal");
        queueLeftTotal.set(0L);
        queueMap = redissonClient.getMap(RedisKeyConst.spiderKeySpace+"queueMap");
        for (String key : queueMap.keySet()) {
            queueLeftTotal.addAndGet(redissonClient.getDeque(key).size());
        }
    }


    protected String getSetKey(Task task) {
        return StringUtils.join(RedisKeyConst.spiderKeySpace, SET_PREFIX, task.getUUID());
    }

    protected String getQueueKey(Task task,boolean isJump, Integer type) {
        if(isJump){
            return StringUtils.join( RedisKeyConst.spiderKeySpace, QUEUE_JUMP, task.getUUID());
        }
        if(!CrawlerTypeEnum.CONTENT.getType().equals(type)){
            return StringUtils.join( RedisKeyConst.spiderKeySpace, QUEUE_INFO_PREFIX, task.getUUID());
        }
        return StringUtils.join( RedisKeyConst.spiderKeySpace, QUEUE_CONTENT_PREFIX, task.getUUID());

    }

    @Override
    public void resetDuplicateCheck(Task task) {
        RSet<Object> urlSet = redissonClient.getSet(getSetKey(task));
        urlSet.delete();
    }

    @Override
    public void push(Request request, Task task) {
        if (this.shouldReserved(request) || this.noNeedToRemoveDuplicate(request) || !isDuplicate(request, task)) {
            this.pushWhenNoDuplicate(request, task);
        }
    }

    @Override
    public boolean isDuplicate(Request request, Task task) {
        RSetCache<Object> urlSet = redissonClient.getSetCache(getSetKey(task));
        boolean has = urlSet.contains(request.getUrl());
        if(!has){
            urlSet.add(request.getUrl());
            return false;
        }
        CrawlerRequestDto requestInfo = request.getExtra(RequestConst.REQUEST_INFO);
        if(null != requestInfo){
            boolean jump = requestInfo.isJump();
            //插队
            if(jump){
                return false;
            }
        }
        return true;
    }

    @Override
    protected void pushWhenNoDuplicate(Request request, Task task) {
        CrawlerRequestDto requestInfo = request.getExtra(RequestConst.REQUEST_INFO);
        if(null == requestInfo){
            return;
        }
        boolean jump = requestInfo.isJump();
        Integer type = requestInfo.getType();
        // 将request推入redis队列中
        String queueKey = getQueueKey(task, jump,type);
        RDeque<Object> deque = redissonClient.getDeque(queueKey);
        queueMap.computeIfAbsent(queueKey,key -> {
            queueLeftTotal.addAndGet(deque.size());
            return "1";
        });
        deque.addLast(request);
        queueLeftTotal.incrementAndGet();
    }

    @Override
    public Request poll(Task task) {
        // 从队列中弹出一个url
        RDeque<Object> jumpDeque = redissonClient.getDeque(getQueueKey(task, true,null));
        Request request = pollWithStatus(jumpDeque,true);
        if(null != request){
            return request;
        }
        long limit = spiderConfig.getQueueNum() * 1000;
        long total = queueLeftTotal.get();
        log.info("队列数据数量：{}",total);
        if(total < limit){
            RDeque<Object> infoDeque = redissonClient.getDeque(getQueueKey(task,false,null));
            request = pollWithStatus(infoDeque,false);
            if(null != request){
                return request;
            }
        }
        RDeque<Object> deque = redissonClient.getDeque(getQueueKey(task,false,CrawlerTypeEnum.CONTENT.getType()));
        return pollWithStatus(deque,false);
    }

    private Request pollWithStatus(RDeque<Object> deque,boolean isJump){
        while (true){
            Request request = (Request)deque.pollFirst();
            if(request == null) {
                return null;
            }
            queueLeftTotal.decrementAndGet();
            CrawlerRequestDto requestInfo = request.getExtra(RequestConst.REQUEST_INFO);
            if(null == requestInfo){
                return null;
            }
            CrawlerRule crawlerInfo = crawlerRuleService.getByRuleId(requestInfo.getRuleId());
            if(null == crawlerInfo){
                return null;
            }
            int openStatus = crawlerInfo.getOpenStatus();
            if(openStatus == 0){
                log.info("url:{},规则：{} 被关闭",request.getUrl(),crawlerInfo.getRuleName());
                continue;
            }
            return request;
        }
    }


    @Override
    public int getLeftRequestsCount(Task task) {
        return (int)queueLeftTotal.get();
    }

    public int getLeftRequestsCount(String domain){
        if(StrUtil.isBlank(domain)){
            return 0;
        }
        RDeque<Object> jumpDeque = redissonClient.getDeque(RedisKeyConst.spiderKeySpace + QUEUE_JUMP + domain);
        RDeque<Object> infoDeque = redissonClient.getDeque(RedisKeyConst.spiderKeySpace + QUEUE_INFO_PREFIX + domain);
        RDeque<Object> contentDeque = redissonClient.getDeque(RedisKeyConst.spiderKeySpace + QUEUE_CONTENT_PREFIX + domain);
        return jumpDeque.size() + infoDeque.size() + contentDeque.size();
    }


    @Override
    public int getTotalRequestsCount(Task task) {
        RSetCache<Object> urlSet = redissonClient.getSetCache(getSetKey(task));
        return urlSet.size();
    }

    public int getTotalRequestsCountByDomain(String domain) {
        if(StrUtil.isBlank(domain)){
            return 0;
        }
        RSetCache<Object> urlSet = redissonClient.getSetCache(RedisKeyConst.spiderKeySpace + SET_PREFIX + domain);
        return urlSet.size();
    }
}