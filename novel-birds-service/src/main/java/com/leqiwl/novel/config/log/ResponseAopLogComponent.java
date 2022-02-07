package com.leqiwl.novel.config.log;

import com.alibaba.fastjson.JSON;
import com.leqiwl.novel.common.base.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * @author 飞鸟不过江
 */
@Slf4j
@ControllerAdvice
public class ResponseAopLogComponent implements ResponseBodyAdvice {

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {

        if (body instanceof ApiResult) {
            log.info("请求返回:{}", JSON.toJSONString(body));
        }
        return body;
    }
}