package com.leqiwl.novel.job.pip;

import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.util.CharsetUtil;
import com.leqiwl.novel.config.sysconst.RequestConst;
import com.leqiwl.novel.domain.dto.CrawlerRequestDto;
import com.leqiwl.novel.job.config.SpiderConfig;
import com.leqiwl.novel.job.pip.downloader.SpiderDownloader;
import com.leqiwl.novel.job.pip.listener.SpiderEventListener;
import com.leqiwl.novel.job.pip.pipeline.SpiderSavePipLine;
import com.leqiwl.novel.job.pip.processor.SpiderProcessor;
import com.leqiwl.novel.job.pip.scheduler.SpiderRedisScheduler;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import us.codecraft.webmagic.Request;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author: CaoBin
 * @Date: 2022/2/8 11:31
 * @Description:
 */
@Slf4j
@Configuration
public class SpiderStartContainerFactory {


    private static ExecutorService executorService;

    @Resource
    private SpiderConfig spiderConfig;

    @Resource
    private SpiderProcessor spiderProcessor;

    @Resource
    private SpiderEventListener spiderEventListener;

    @Resource
    private SpiderDownloader spiderDownloader;

    @Resource
    private SpiderRedisScheduler spiderRedisScheduler;

    @Resource
    private SpiderSavePipLine spiderSavePipLine;

    @Resource
    private RedissonClient redissonClient;

    private static final  Map<String, SpiderStartContainer> startContainerMap = Collections.synchronizedMap(new HashMap<>());

    public synchronized SpiderStartContainer getStartContainer(String domain) {
        SpiderStartContainer spiderStartContainer = startContainerMap.get(domain);
        if(spiderStartContainer != null){
            return spiderStartContainer;
        }
        spiderStartContainer =
                SpiderStartContainer.create(spiderProcessor, spiderEventListener, redissonClient);
        spiderStartContainer.setDownloader(spiderDownloader);
        spiderStartContainer.setScheduler(spiderRedisScheduler);
        spiderStartContainer.setPipelines(Collections.singletonList(spiderSavePipLine));
        spiderStartContainer.thread(executorService,
                spiderConfig.getThreadNum() <= 0 ? (Runtime.getRuntime().availableProcessors() * 2) : spiderConfig.getThreadNum());
        spiderStartContainer.setExitWhenComplete(false);
        startContainerMap.put(domain,spiderStartContainer);
        return spiderStartContainer;
    }

    public SpiderStartContainer getStartContainer(Request request) {
        CrawlerRequestDto requestInfo = request.getExtra(RequestConst.REQUEST_INFO);
        if(null == requestInfo){
            return null;
        }
        UrlBuilder urlBuilder = UrlBuilder.ofHttp(requestInfo.getUrl(), CharsetUtil.CHARSET_UTF_8);
        String domain = urlBuilder.getHost();
        return getStartContainer(domain);
    }

    @PostConstruct
    public void getExecutorService(){
        int threadNum = spiderConfig.getThreadNum();
        //线程数 cpu * 2
        if (threadNum <= 0) {
            threadNum = Runtime.getRuntime().availableProcessors() * 2;
        }
        log.info("================ spider thread num :{} ================", threadNum);
        SpiderStartContainerFactory.executorService =  Executors.newFixedThreadPool(threadNum);
    }
}