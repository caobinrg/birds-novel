package com.leqiwl.novel.admin.config;


import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * 通用配置(拦截器)
 * @author 飞鸟不过江
 */
@Configuration
public class NovelWebMvcConfigurer implements WebMvcConfigurer {

    @Value("${novelImagePath}")
    private String novelImagePath;

    @Value("${location.image}")
    private String baseImagePath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/"+baseImagePath+"**")
                .addResourceLocations("file:"+novelImagePath + baseImagePath);
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        ObjectMapper objectMapper = builder.build();
        objectMapper.configure(MapperFeature.PROPAGATE_TRANSIENT_MARKER, true); // 忽略 transient 修饰的属性
        converters.add(new MappingJackson2HttpMessageConverter(objectMapper));
    }
}

