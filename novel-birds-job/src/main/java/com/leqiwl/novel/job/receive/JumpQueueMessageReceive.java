package com.leqiwl.novel.job.receive;

import com.leqiwl.novel.job.pip.SpiderStartContainer;
import com.leqiwl.novel.config.sysconst.DelayQueueConst;
import com.leqiwl.novel.service.DelayQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Request;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author 飞鸟不过江
 * @description: 采集插队消息监听(延迟队列,列表)
 * @date 2022/1/2 0002 0:31
 */
@Slf4j
@Component
@Order(999)
public class JumpQueueMessageReceive implements ApplicationRunner {

    @Resource
    private DelayQueueService<Request> delayQueueService;

    @Resource
    private SpiderStartContainer spiderStartContainer;

    @Async("birdsExecutor")
    @Override
    public void run(ApplicationArguments args) {
        while (true){
            Request request = null;
            try {
                request = delayQueueService.pullData(DelayQueueConst.CRAWLER_LIST_QUEUE);
                log.info("接收list采集延时消息：{}",request);
                spiderStartContainer.spiderJumpQueue(request);
                spiderStartContainer.spiderStart();
            } catch (InterruptedException e) {
              log.info(e.getMessage(),e);
            }
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
               log.info(e.getMessage(),e);
            }

        }
    }
}
