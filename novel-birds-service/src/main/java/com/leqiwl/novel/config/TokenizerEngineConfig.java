package com.leqiwl.novel.config;

import cn.hutool.extra.tokenizer.TokenizerEngine;
import cn.hutool.extra.tokenizer.TokenizerUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 飞鸟不过江
 * @description:
 * @date 2022/1/1 0001 20:53
 */
@Configuration
public class TokenizerEngineConfig {

    @Bean
    public TokenizerEngine createEngine(){
        return TokenizerUtil.createEngine();
    }

}
