package com.leqiwl.novel.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * @author: 飞鸟不过江
 * @Date: 2022/1/4 19:57
 * @Description:
 */
@EnableMongoRepositories(basePackages = {"com.leqiwl.novel"})
@SpringBootApplication(exclude= {DataSourceAutoConfiguration.class})
@ComponentScan(basePackages = { "com.leqiwl.novel" })
public class AdminApplication {
    public static void main(String[] args){

        SpringApplication.run(AdminApplication.class,args);
    }
}
