package com.leqiwl.novel.admin.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "custom")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CustomConfig {


    /**
     * 登录页地址
     */
    private String loginUrl;


}
