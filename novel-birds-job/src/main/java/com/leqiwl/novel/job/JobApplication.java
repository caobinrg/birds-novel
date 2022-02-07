package com.leqiwl.novel.job;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author: 飞鸟不过江
 * @Date: 2021/12/30 12:17
 * @Description:
 */
@EnableMongoRepositories(basePackages = {"com.leqiwl.novel"})
@SpringBootApplication(exclude= {DataSourceAutoConfiguration.class})
@ComponentScan(basePackages = { "com.leqiwl.novel" })
@EnableScheduling
public class JobApplication {
    public static void main(String[] args){
        SpringApplication.run(JobApplication.class,args);
    }
}
