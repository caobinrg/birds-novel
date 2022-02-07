package com.leqiwl.novel.config;

import com.leqiwl.novel.common.base.ApiResult;
import com.leqiwl.novel.common.enums.ApiErrorCodeEnum;
import com.leqiwl.novel.common.exception.ApiPresetException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.stream.Collectors;

/**
 * @author: 飞鸟不过江
 * @Date: 2020/7/24 13:42
 * @Description:
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {



    /**
     * 默认包装异常
     * <p>
     * 针对ApiErrorCodeEnum中定义的错误类型，自动包装为ApiResult
     *
     * @param e 业务通用异常
     * @return 对异常的包装
     */
    @ExceptionHandler(ApiPresetException.class)
    @ResponseBody
    public ApiResult<?> handleApiCommonException(ApiPresetException e) {
        ApiResult<Object> result = ApiResult.fail(e.getErrorEnum());
        log.info("业务异常：{}",result);
        return result;
    }


    /**
     * post参数校验异常
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResult<?> handleConstraintException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        ApiResult<Object> result = ApiResult.fail(bindingResult);
        log.info("参数校验异常：{}", result);
        return result;

    }

    /**
     * 拦截运行时异常
     * @param e
     */
    @ResponseBody
    @ExceptionHandler(value = RuntimeException.class)
    public ApiResult<?> runtimeExceptionHandle(RuntimeException e) {
        ApiResult<Object> result = ApiResult.fail(ApiErrorCodeEnum.ServerFail);
        log.error("捕捉到运行时异常：", e);
        return result;
    }



    /**
     * 不支持的请求
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    public ApiResult<?> supportHandle(HttpRequestMethodNotSupportedException e) {
        ApiResult<Object> result = ApiResult.fail(ApiErrorCodeEnum.ServerFail);
        log.info("不支持的请求类型：{}",e.getMessage());
        return result;
    }

    /**
     * Controller层相关异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler({
            NoHandlerFoundException.class,
            HttpMediaTypeNotSupportedException.class,
            MissingPathVariableException.class,
            MissingServletRequestParameterException.class,
            TypeMismatchException.class,
            HttpMessageNotReadableException.class,
            HttpMessageNotWritableException.class,
            HttpMediaTypeNotAcceptableException.class,
            ServletRequestBindingException.class,
            ConversionNotSupportedException.class,
            MissingServletRequestPartException.class,
    })
    public ApiResult<?> handleServletException(Exception e) {
        String exceptionName = e.getClass().getSimpleName();
        String errorMessage = "bad request ："+exceptionName + " message: " +  e.getMessage();
        log.warn(errorMessage);
        return ApiResult.fail(errorMessage);
    }

    /**
     * 捕捉系统级异常
     * @param th
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = Throwable.class)
    public ApiResult<?> throwableHandle(Throwable th) {
        ApiResult<Object> result = ApiResult.fail(ApiErrorCodeEnum.ServerFail);
        log.error("捕捉Throwable异常：", th);
        return result;
    }


    /**
     * 参数绑定异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = BindException.class)
    public ApiResult<?> handleBindException(BindException e) {
        return getBindingResult(e.getBindingResult());
    }

    private ApiResult<?> getBindingResult(BindingResult bindingResult) {
        String errorMessage = bindingResult.getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining(","));
        return ApiResult.fail(errorMessage);
    }
}
