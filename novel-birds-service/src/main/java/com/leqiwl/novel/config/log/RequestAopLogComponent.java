package com.leqiwl.novel.config.log;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author 飞鸟不过江
 */
@Slf4j
@ControllerAdvice
public class RequestAopLogComponent implements RequestBodyAdvice {

    @Override
    public boolean supports(@NonNull MethodParameter methodParameter,@NonNull Type targetType,
                            @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
       return true;
    }

    @Override
    public HttpInputMessage beforeBodyRead(@NonNull HttpInputMessage inputMessage,@NonNull MethodParameter parameter,@NonNull Type targetType,
                                           @NonNull Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        return inputMessage;
    }

    @Override
    public Object afterBodyRead(@NonNull Object body, @NonNull HttpInputMessage inputMessage, MethodParameter parameter,
                                @NonNull Type targetType,@NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        log.info("请求参数:{}", JSON.toJSONString(body));
        return body;
    }

    @Override
    public Object handleEmptyBody(Object body,@NonNull HttpInputMessage inputMessage, MethodParameter parameter,
                                  @NonNull Type targetType, @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        RequestMapping requestMapping = parameter.getMethodAnnotation(RequestMapping.class);
        if(null != requestMapping){
            log.info("请求地址====>{}", StringUtils.arrayToDelimitedString(requestMapping.value(), ","));
        }
        return body;
    }
}