package com.leqiwl.novel.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * @author 飞鸟不过江
 * @description:
 * @date 2021/12/26 0026 0:18
 */
@EnableMongoRepositories(basePackages = {"com.leqiwl.novel"})
@SpringBootApplication(exclude= {DataSourceAutoConfiguration.class})
@ComponentScan(basePackages = { "com.leqiwl.novel","cn.hutool.extra.spring" })
public class WebApplication {

    public static void main(String[] args){

        SpringApplication.run(WebApplication.class,args);
    }
}
