package com.leqiwl.novel.web.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author 飞鸟不过江
 * @description:
 * @date 2022-08-14 11:41
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "mapping")
public class WebBaseUrlConfig {

    private String  home;

    private String page;

    private String classify;

    private String bookrack;

    private String user;




//    home: "/web/home"
//    page: "/web/page"
//    bookrack: "/web/bookrack"
//    user: "/web/user"






}
