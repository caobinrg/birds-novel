package com.leqiwl.novel.job.pip.scheduler;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
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

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

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
    private static final String MAP_PREFIX = "map_";

    private static final String QUEUE_NAME_SET = "queueNameSet";

    @Resource
    private CrawlerRuleService crawlerRuleService;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private SpiderConfig spiderConfig;

//    protected String getSetKey(Task task) {
//        String uuid = task.getUUID();
//        return getSetKey(uuid);
//    }
//
//    protected String getSetKey(String uuid) {
//        return StringUtils.join(RedisKeyConst.spiderKeySpace, SET_PREFIX, uuid);
//    }

    protected String getMapKey(Task task) {
        String uuid = task.getUUID();
        return getMapKey(uuid);
    }

    protected String getMapKey(String uuid) {
        return StringUtils.join(RedisKeyConst.spiderKeySpace, MAP_PREFIX, uuid);
    }


    protected String getQueueKey(Task task,boolean isJump, Integer type) {
        return getQueueKey(task.getUUID(),isJump,type);

    }

    private String getQueueKey(String uuid,boolean isJump, Integer type){
        if(isJump){
            return StringUtils.join( RedisKeyConst.spiderKeySpace, QUEUE_JUMP, uuid);
        }
        if(!CrawlerTypeEnum.CONTENT.getType().equals(type)){
            return StringUtils.join( RedisKeyConst.spiderKeySpace, QUEUE_INFO_PREFIX, uuid);
        }
        return StringUtils.join( RedisKeyConst.spiderKeySpace, QUEUE_CONTENT_PREFIX, uuid);
    }

    @Override
    public void resetDuplicateCheck(Task task) {
        String mapKey = getMapKey(task);
        RMapCache<String, DuplicateRequest> urlMap = redissonClient.getMapCache(mapKey);
        log.info("before del urlSet setKey:{},size:{}",mapKey,urlMap.size());
        urlMap.delete();
        log.info("after del urlSet setKey:{},size:{}",mapKey,urlMap.size());
    }

    public void resetDuplicateCheck(String domain) {
        String mapKey = getMapKey(domain);
        RMapCache<String, DuplicateRequest> urlMap = redissonClient.getMapCache(mapKey);
        log.info("before del urlSet setKey:{},size:{}",mapKey,urlMap.size());
        urlMap.delete();
        log.info("after del urlSet setKey:{},size:{}",mapKey,urlMap.size());
    }

    @Override
    public void push(Request request, Task task) {
        if (this.shouldReserved(request) || this.noNeedToRemoveDuplicate(request) || !isDuplicate(request, task)) {
            this.pushWhenNoDuplicate(request, task);
        }
    }

    @Override
    public boolean isDuplicate(Request request, Task task) {
        int cacheTime = 15;
        RMapCache<String, DuplicateRequest> urlMap = redissonClient.getMapCache(getMapKey(task));
        String url = request.getUrl();
        boolean has = urlMap.containsKey(url);
        Date now = new Date();
        if(!has){
            urlMap.put(url, new DuplicateRequest(url, now),cacheTime, TimeUnit.MINUTES);
            return false;
        }
        CrawlerRequestDto requestInfo = request.getExtra(RequestConst.REQUEST_INFO);
        if(null != requestInfo){
            boolean jump = requestInfo.isJump();
            //插队
            if(jump){
                return false;
            }
            DuplicateRequest duplicateRequest = urlMap.get(url);
            Date putTime = duplicateRequest.getPutTime();
            long between = DateUtil.between(putTime, now, DateUnit.MS);
            if(between >= cacheTime){
                urlMap.put(url, new DuplicateRequest(url, now),cacheTime, TimeUnit.MINUTES);
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
        requestInfo.setJump(false);
        RLock lock = null;
        try {
            lock = redissonClient.getLock(QUEUE_NAME_SET);
            lock.lock();
            RDeque<Object> deque = redissonClient.getDeque(queueKey);
            RSet<String> queueSet = getQueueSet();
            queueSet.add(queueKey);
            deque.addLast(request);
        }finally {
            if(null != lock){
                lock.unlock();
            }
        }
    }

    @Override
    public Request poll(Task task) {
        // 从队列中弹出一个url
        Request jumpRequest = poll(getQueueKey(task, true, null),task);
        if(null != jumpRequest){
            return jumpRequest;
        }
        long total = getDomainLeftRequestByTask(task);
        long limit = spiderConfig.getQueueNum() * 1000;
        if(total > limit){
            Request contentRequest = poll(getQueueKey(task,false,CrawlerTypeEnum.CONTENT.getType()),task);
            if(null != contentRequest){
                return contentRequest;
            }
        }
        Request infoRequest = poll(getQueueKey(task, false, null), task);
        if(null != infoRequest){
            return infoRequest;
        }
        return poll(getQueueKey(task,false,CrawlerTypeEnum.CONTENT.getType()),task);
    }

    private Request poll(String queueKey,Task task){
        RDeque<Object> deque = redissonClient.getDeque(queueKey);
        Request request = pollWithStatus(deque);
        long total = getDomainLeftRequestByTask(task);
        log.info("queueKey: {} ,弹出 request:{} ,当前队列数据数量：{} , 队列数据总数量：{}",
                queueKey,request == null ? "" : request.getUrl(),deque.size(),total);
        return request;
    }


    private Request pollWithStatus(RDeque<Object> deque){
        while (true){
            if(deque.isEmpty()){
                return null;
            }
            Request request = (Request)deque.pollFirst();
            if(null == request){
                continue;
            }
            CrawlerRequestDto requestInfo = request.getExtra(RequestConst.REQUEST_INFO);
            if(null == requestInfo){
                continue;
            }
            CrawlerRule crawlerInfo = crawlerRuleService.getByRuleId(requestInfo.getRuleId());
            if(null == crawlerInfo){
                continue;
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
        return (int)getLeftRequestCount();
    }

    public long getLeftRequestCount(){
        AtomicLong atomicLong = new AtomicLong();
        RSet<String> queueSet = getQueueSet();
        for (String key : queueSet) {
            atomicLong.addAndGet(redissonClient.getDeque(key).size());
        }
        return atomicLong.get();
    }

    public long getDomainLeftRequestByTask(Task task){
        String domain = task.getUUID();
        return getDomainLeftRequestByDomain(domain);
    }

    public long getDomainLeftRequestByDomain(String domain){
        RDeque<Object> jumpDeque = redissonClient.getDeque(getQueueKey(domain, true, null));
        RDeque<Object> infoDeque = redissonClient.getDeque(getQueueKey(domain, false, null));
        RDeque<Object> contentDeque = redissonClient.getDeque(getQueueKey(domain,false,CrawlerTypeEnum.CONTENT.getType()));
        return jumpDeque.size() + infoDeque.size() + contentDeque.size();
    }

    public RSet<String> getQueueSet(){
        return redissonClient.getSet(RedisKeyConst.spiderKeySpace+ QUEUE_NAME_SET);
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
        RMapCache<Object, Object> urlMap = redissonClient.getMapCache(getMapKey(task));
        return urlMap.size();
    }

    public int getTotalRequestsCountByDomain(String domain) {
        if(StrUtil.isBlank(domain)){
            return 0;
        }
        RSetCache<Object> urlSet = redissonClient.getSetCache(RedisKeyConst.spiderKeySpace + MAP_PREFIX + domain);
        return urlSet.size();
    }


    static class DuplicateRequest implements Serializable {

        public DuplicateRequest(String url, Date putTime) {
            this.url = url;
            this.putTime = putTime;
        }

        private String url;

        private Date putTime;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public Date getPutTime() {
            return putTime;
        }

        public void setPutTime(Date putTime) {
            this.putTime = putTime;
        }

    }

}
