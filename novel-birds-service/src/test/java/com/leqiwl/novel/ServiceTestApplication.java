package com.leqiwl.novel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * @author 飞鸟不过江
 * @description:
 * @date 2021/12/26 0026 0:08
 */
@EnableMongoRepositories(basePackages = {"com.leqiwl.novel"})
@SpringBootApplication(exclude= {DataSourceAutoConfiguration.class})
public class ServiceTestApplication {
    public static void main(String args[]){
        SpringApplication.run(ServiceTestApplication.class,args);
    }
}
