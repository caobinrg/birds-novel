package com.leqiwl.novel.job.pip.scheduler;

import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import com.leqiwl.novel.config.sysconst.RedisKeyConst;
import com.leqiwl.novel.config.sysconst.RequestConst;
import com.leqiwl.novel.domain.dto.CrawlerRequestDto;
import com.leqiwl.novel.domain.entify.crawler.CrawlerRule;
import com.leqiwl.novel.enums.CrawlerTypeEnum;
import com.leqiwl.novel.service.CrawlerRuleService;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RDeque;
import org.redisson.api.RSet;
import org.redisson.api.RSetCache;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.DuplicateRemovedScheduler;
import us.codecraft.webmagic.scheduler.MonitorableScheduler;
import us.codecraft.webmagic.scheduler.component.DuplicateRemover;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 自定义调度器，将下载器传递过来的请求保存到redis中，进行url去重，弹出请求
 * @author 飞鸟不过江
 */
@Slf4j
@Component
@NoArgsConstructor
public class SpiderRedisScheduler extends DuplicateRemovedScheduler implements MonitorableScheduler, DuplicateRemover {

    /**
     * 用于存放url的队列
     */
    private static final String QUEUE_PREFIX = "queue_";

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

    @Autowired
    private RedissonClient redissonClient;


    protected String getSetKey(Task task) {
        return StringUtils.join(RedisKeyConst.spiderKeySpace, SET_PREFIX, task.getUUID());
    }

    protected String getQueueKey(Task task,boolean isJump) {
        return StringUtils.join( RedisKeyConst.spiderKeySpace, (isJump?QUEUE_JUMP:QUEUE_PREFIX), task.getUUID());
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
        if(Boolean.FALSE.equals(has)) {
            // 将url加入到redis set中
            urlSet.add(request.getUrl(),60, TimeUnit.MINUTES);
            return false;
        } else {
            return true;
        }
    }

    @Override
    protected void pushWhenNoDuplicate(Request request, Task task) {
        CrawlerRequestDto requestInfo = request.getExtra(RequestConst.REQUEST_INFO);
        if(null == requestInfo){
            return;
        }
        boolean jump = requestInfo.isJump();
        // 将request推入redis队列中
        RDeque<Object> deque = redissonClient.getDeque(getQueueKey(task,jump));
        Integer type = requestInfo.getType();
        if(CrawlerTypeEnum.CONTENT.getType().equals(type)){
            deque.addLast(request);
            return;
        }
        deque.addFirst(request);
    }

    @Override
    public Request poll(Task task) {
        // 从队列中弹出一个url
        RDeque<Object> jumpDeque = redissonClient.getDeque(getQueueKey(task, true));
        Request request = pollWithStatus(jumpDeque);
        if(null != request){
            return request;
        }
        RDeque<Object> deque = redissonClient.getDeque(getQueueKey(task,false));
        return pollWithStatus(deque);
    }

    private Request pollWithStatus(RDeque<Object> deque){
        while (true){
            Request request = (Request)deque.pollFirst();
            if(request == null) {
                return null;
            }
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
        RDeque<Object> jumpDeque = redissonClient.getDeque(getQueueKey(task,true));
        RDeque<Object> deque = redissonClient.getDeque(getQueueKey(task,false));
        return jumpDeque.size() + deque.size();
    }

    public int getLeftRequestsCount(String domain){
        if(StrUtil.isBlank(domain)){
            return 0;
        }
        RDeque<Object> deque = redissonClient.getDeque(RedisKeyConst.spiderKeySpace + QUEUE_PREFIX + domain);
        return deque.size();
    }


    @Override
    public int getTotalRequestsCount(Task task) {
        RSetCache<Object> urlSet = redissonClient.getSetCache(getSetKey(task));
        return urlSet.size();
    }

    public int getTotalRequestsCount(String domain) {
        if(StrUtil.isBlank(domain)){
            return 0;
        }
        RSetCache<Object> urlSet = redissonClient.getSetCache(RedisKeyConst.spiderKeySpace + SET_PREFIX + domain);
        return urlSet.size();
    }


    public void delAllRequest(String domain){
        RDeque<Object> deque = redissonClient.getDeque(RedisKeyConst.spiderKeySpace + QUEUE_PREFIX + domain);
        deque.delete();
        RSetCache<Object> urlSet = redissonClient.getSetCache(RedisKeyConst.spiderKeySpace + SET_PREFIX + domain);
        urlSet.delete();
    }
}