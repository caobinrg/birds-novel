package com.leqiwl.novel.config;

import cn.hutool.extra.spring.SpringUtil;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author: 飞鸟不过江
 * @Date: 2022/1/7 16:26
 * @Description:
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "mapping")
public class WebMappingUrlConfig {

    private String home;

    private String page;

    private String bookrack;

    private String user;

    private String error;

    public static WebMappingUrlConfig instance(){
        return SpringUtil.getBean("webMappingUrlConfig");
    }

}
