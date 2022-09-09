package com.leqiwl.novel.web.config;


import com.leqiwl.novel.web.interceptor.ViewInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

/**
 * 通用配置(拦截器)
 * @author 飞鸟不过江
 */
@Configuration
public class NovelWebMvcConfigurer implements WebMvcConfigurer {

    @Value("${thymeleaf.static}")
    private String staticResource;

    @Value("${novelImagePath}")
    private String novelImagePath;

    @Value("${location.image}")
    private String baseImagePath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
        registry.addResourceHandler("/static/**")
                .addResourceLocations(staticResource);
        registry.addResourceHandler("/favicon.ico")//favicon.ico
                .addResourceLocations(staticResource);
        if(!novelImagePath.endsWith("/")){
            novelImagePath = novelImagePath + "/";
        }
        registry.addResourceHandler("/"+baseImagePath+"**")
                .addResourceLocations("file:"+novelImagePath + baseImagePath);
    }

    @Bean
    ViewInterceptor localInterceptor() {
        return new ViewInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/error/**")
                .excludePathPatterns("/api/*")
//                .excludePathPatterns("/web/*")
                .excludePathPatterns("/favicon.ico")
                .excludePathPatterns("/**/*.css")
                .excludePathPatterns("/**/*.js")
                .excludePathPatterns("/"+baseImagePath+"**");
    }
}

