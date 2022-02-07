package com.leqiwl.novel.web.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author 飞鸟不过江
 * @description:
 * @date 2022/1/12 0012 0:57
 */
@Data
@Component
@ConfigurationProperties(prefix = "website")
public class WebSiteConfig {

    private String name;

    private String keywords;

    private String description;




}
