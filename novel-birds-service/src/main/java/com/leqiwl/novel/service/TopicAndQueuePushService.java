package com.leqiwl.novel.service;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.leqiwl.novel.config.sysconst.TopicAndQueueKeyConst;
import com.leqiwl.novel.domain.dto.NovelIdTopicDto;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingDeque;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author: 飞鸟不过江
 * @Date: 2022/1/10 10:30
 * @Description:
 */
@Slf4j
@Service
public class TopicAndQueuePushService {

    @Resource
    private RedissonClient redissonClient;

    @Async
    public void sendRead(String novelId){
        if(StrUtil.isBlank(novelId)){
            return;
        }
        RBlockingQueue<NovelIdTopicDto> blockingQueue =
                redissonClient.getBlockingQueue(TopicAndQueueKeyConst.READ_QUEUE);
        blockingQueue.putAsync(getNovelIdTopicDto(novelId));
    }

    @Async
    public void sendClick(String novelId){
        if(StrUtil.isBlank(novelId)){
            return;
        }
        RBlockingQueue<NovelIdTopicDto> blockingQueue =
                redissonClient.getBlockingQueue(TopicAndQueueKeyConst.CLICK_QUEUE);
        blockingQueue.putAsync(getNovelIdTopicDto(novelId));
    }

    @Async
    public void sendStar(String novelId){
        if(StrUtil.isBlank(novelId)){
            return;
        }
        RBlockingQueue<NovelIdTopicDto> blockingQueue =
                redissonClient.getBlockingQueue(TopicAndQueueKeyConst.STAR_QUEUE);
        blockingQueue.putAsync(getNovelIdTopicDto(novelId));

    }

    @Async
    public void sendSaveUrl(String url){
        if(StrUtil.isBlank(url)){
            return;
        }
        RBlockingQueue<String> blockingQueue =
                redissonClient.getBlockingQueue(TopicAndQueueKeyConst.ULR_SAVE_QUEUE);
        blockingQueue.putAsync(url);
    }

    private NovelIdTopicDto getNovelIdTopicDto(String novelId){
        NovelIdTopicDto novelIdTopicDto = new NovelIdTopicDto();
        novelIdTopicDto.setNovelId(novelId);
        novelIdTopicDto.setMessageId(IdUtil.simpleUUID());
        return novelIdTopicDto;
    }

}
