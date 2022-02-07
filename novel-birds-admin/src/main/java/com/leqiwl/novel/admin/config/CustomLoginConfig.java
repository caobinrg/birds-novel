package com.leqiwl.novel.admin.config;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


/**
 * @author 飞鸟不过江
 */
@Configuration
@ConfigurationProperties(prefix = "login")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CustomLoginConfig {


    private String userName;

    private String password;


}
