package com.leqiwl.novel.job.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author: CaoBin
 * @Date: 2022/2/10 18:41
 * @Description:
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "spider")
public class SpiderConfig {

    private int threadNum = 0;

    private int retryTimes = 3000;

    private int sleepTime = 3000;

    private int timeOut = 60000;

    private long queueNum = 100;

}
