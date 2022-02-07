package com.leqiwl.novel.admin.config;

import com.leqiwl.novel.common.base.ApiResult;
import com.leqiwl.novel.config.GlobalExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author: 飞鸟不过江
 * @Date: 2020/7/24 13:42
 * @Description:
 */
@RestControllerAdvice
@Slf4j
public class AdminExceptionHandler extends GlobalExceptionHandler {


    @ExceptionHandler(IncorrectCredentialsException.class)
    @ResponseBody
    public ApiResult<?> handleIncorrectCredentialsException(IncorrectCredentialsException e) {
        ApiResult<Object> result = ApiResult.fail(e.getMessage());
        log.info("IncorrectCredentialsException：{}",result);
        return result;
    }

}
