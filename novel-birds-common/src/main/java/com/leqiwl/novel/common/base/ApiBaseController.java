package com.leqiwl.novel.common.base;


import cn.hutool.core.util.StrUtil;
import com.leqiwl.novel.common.enums.ApiErrorCodeEnum;
import com.leqiwl.novel.common.util.NetworkIpUtil;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.http.HttpServletRequest;

/**
 * Api 接口基类
 * 用于返回统一Code、Message
 * @author 飞鸟不过江
 */
@NoArgsConstructor
@Slf4j
public class ApiBaseController {

    protected <E> ApiResult<E> ok(E data) {
        ApiResult<E> result = new ApiResult();
        result.setMsg(ApiErrorCodeEnum.Success.getStatusName());
        result.setSuccess(true);
        result.setErrCode(ApiErrorCodeEnum.Success.getStatus());
        result.setData(data);
        return result;
    }

    protected <E> ApiResult<E> ok(String Msg) {
        ApiResult<E> result = new ApiResult();
        result.setMsg(Msg);
        result.setSuccess(true);
        result.setErrCode(ApiErrorCodeEnum.Success.getStatus());
        result.setData(null);
        return result;
    }

    protected <E> ApiResult<E> fail(String message) {
        if(StrUtil.isBlank(message)){
            message = ApiErrorCodeEnum.Fail.getStatusName();
        }
        ApiResult<E> result = new ApiResult();
        result.setMsg(message);
        result.setSuccess(false);
        result.setData(null);
        result.setErrCode(ApiErrorCodeEnum.Fail.getStatus());
        return result;
    }


    protected <E> ApiResult<E> fail(ApiErrorCodeEnum errorCodeEnum) {
        ApiResult<E> result = new ApiResult();
        result.setMsg(errorCodeEnum.getStatusName());
        result.setSuccess(false);
        result.setData(null);
        result.setErrCode(errorCodeEnum.getStatus());
        return result;
    }

    protected <E> ApiResult<E> fail(ApiErrorCodeEnum errorCodeEnum,String message) {
        ApiResult<E> result = new ApiResult();
        result.setMsg(message);
        result.setSuccess(false);
        result.setData(null);
        result.setErrCode(errorCodeEnum.getStatus());
        return result;
    }

    /**
     * 适用于参数验证直接返回
     * @param errors
     * @param <E>
     * @return
     */
    protected <E> ApiResult<E> fail(BindingResult errors){
        for (ObjectError allErrors : errors.getAllErrors()) {
            FieldError fieldError = (FieldError) allErrors;
            return fail(ApiErrorCodeEnum.getParam(fieldError.getDefaultMessage()));
        }
        return fail(ApiErrorCodeEnum.DefaultParamNotNull);
    }

    public ApiResult bindingResultError(BindingResult errors){
        StringBuffer errorMessage = new StringBuffer();
        if(errors.hasErrors()){
            errors.getAllErrors().forEach(error -> {
                        FieldError fieldError = (FieldError) error;
                        errorMessage.append(fieldError.getDefaultMessage()).append(" ");
                    }
            );
            return fail(errorMessage.toString());
        } else {
            return ok(null);
        }
    }

    public String getHeader(String headerName){
        ServletRequestAttributes servletRequestAttributes =
                (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        return request.getHeader(headerName);
    }

    public String getClientIp(){
        ServletRequestAttributes servletRequestAttributes =
                (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        return NetworkIpUtil.getIpAddress(request);
    }
}
