package com.leqiwl.novel.job.pip;

import com.leqiwl.novel.job.pip.scheduler.SpiderRedisScheduler;
import com.leqiwl.novel.domain.dto.CrawlerRequestDto;
import com.leqiwl.novel.enums.CrawlerTypeEnum;
import com.leqiwl.novel.config.sysconst.RequestConst;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.monitor.SpiderMonitor;

import javax.management.JMException;

@Slf4j
@SpringBootTest(classes = CrawlerTestApplication.class)
@RunWith(SpringRunner.class)
public class BirdsTaskTest {

    @Autowired
    private SpiderStartContainer spiderStartContainer;


    @Test
    public void TestList() {
        Request request = new Request("https://www.xbiquge.la/");
        CrawlerRequestDto requestInfo = CrawlerRequestDto.builder()
                .url("https://www.xbiquge.la/")
                .ruleId("3dccc9aa30004cb999466fb78aef4e7a")
                .baseUrl("https://www.xbiquge.la/")
                .type(CrawlerTypeEnum.LIST.getType())
                .build();
        request.putExtra(RequestConst.REQUEST_INFO,requestInfo);
        spiderStartContainer.addRequest(request);
        spiderStartContainer.run();
    }
    @Test
    public void TestInfo() throws JMException {
        Request request = new Request("https://www.xbiquge.la/90/90721/");
        CrawlerRequestDto requestInfo = CrawlerRequestDto.builder()
                .url("https://www.xbiquge.la/90/90721/")
                .ruleId("3dccc9aa30004cb999466fb78aef4e7a")
                .baseUrl("https://www.xbiquge.la/")
                .type(CrawlerTypeEnum.DETAIL.getType())
                .build();
        request.putExtra(RequestConst.REQUEST_INFO,requestInfo);
        spiderStartContainer.addRequest(request);
//        Request request1 = new Request("https://www.xbiquge.la/90/90721/");
//        CrawlerRequestDto requestInfo1 = CrawlerRequestDto.builder()
//                .url("https://www.xbiquge.la/90/90721/")
//                .ruleId(1L)
//                .baseUrl("https://www.xbiquge.la/")
//                .type(CrawlerTypeEnum.DETAIL.getType())
//                .build();
//        request1.putExtra(RequestConst.REQUEST_INFO,requestInfo1);
//        spiderStartContainer.addRequest(request1);
        SpiderMonitor spiderMonitor = SpiderMonitor.instance();
//        spiderMonitor.register(spiderStartContainer);
        spiderStartContainer.run();
    }

    @Autowired
    private SpiderRedisScheduler spiderRedisScheduler;

    @Autowired
    private RedisTemplate redisTemplate;
//    @Test
//    public void removeAllRequest(){
//        spiderRedisScheduler.delAllRequest("www.xbiquge.la");
//    }
}