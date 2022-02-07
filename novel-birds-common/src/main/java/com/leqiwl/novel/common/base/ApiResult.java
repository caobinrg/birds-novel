package com.leqiwl.novel.common.base;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.leqiwl.novel.common.enums.ApiErrorCodeEnum;
import lombok.Data;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;


/**
 * @author 飞鸟不过江
 */
@Data
public class ApiResult<T> {

    /**
     * 是否成功
     */
    @JsonProperty(value = "success")
    @JSONField(name = "success")
    private boolean success;

    /**
     * 接口返回信息
     */
    @JsonProperty(value = "msg")
    @JSONField(name = "msg")
    private String msg;

    /**
     * 接口返回错误码
     */
    @JsonProperty(value = "errorCode")
    @JSONField(name = "errorCode")
    private Integer errCode;

    /**
     * 接口返回错误码
     */
    @JsonProperty(value = "data")
    @JSONField(name = "data")
    private T data;


    public ApiResult() {
    }

    public static <E> ApiResult<E> ok() {
        ApiResult<E> result = new ApiResult<>();
        result.setMsg(ApiErrorCodeEnum.Success.getStatusName());
        result.setSuccess(true);
        result.setData(null);
        result.setErrCode(ApiErrorCodeEnum.Success.getStatus());
        return result;
    }

    public static <E> ApiResult<E> ok(E data) {
        ApiResult<E> result = new ApiResult();
        result.setMsg(ApiErrorCodeEnum.Success.getStatusName());
        result.setSuccess(true);
        result.setErrCode(ApiErrorCodeEnum.Success.getStatus());
        result.setData(data);
        return result;
    }

    public static <E> ApiResult<E> ok(String Msg) {
        ApiResult<E> result = new ApiResult();
        result.setMsg(Msg);
        result.setSuccess(true);
        result.setErrCode(ApiErrorCodeEnum.Success.getStatus());
        result.setData(null);
        return result;
    }

    public static <E> ApiResult<E> fail(String message) {
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

    public static <E> ApiResult<E> fail(Integer code,String message) {
        if(message==null||message.isEmpty()){
            message = ApiErrorCodeEnum.Fail.getStatusName();
        }
        ApiResult<E> result = new ApiResult();
        result.setMsg(message);
        result.setSuccess(false);
        result.setData(null);
        result.setErrCode(code);
        return result;
    }

    public static <E> ApiResult<E> fail(String message, Integer errorCode) {
        ApiResult<E> result = new ApiResult<>();
        result.setMsg(message);
        result.setSuccess(false);
        result.setData(null);
        result.setErrCode(errorCode);
        return result;
    }

    public static <E> ApiResult<E> fail(ApiErrorCodeEnum errorCodeEnum) {
        ApiResult<E> result = new ApiResult();
        result.setMsg(errorCodeEnum.getStatusName());
        result.setSuccess(false);
        result.setData(null);
        result.setErrCode(errorCodeEnum.getStatus());
        return result;
    }

    public static <E> ApiResult<E> fail(ApiErrorCodeEnum errorCodeEnum, String msg) {
        ApiResult<E> result = new ApiResult();
        result.setMsg(errorCodeEnum.getStatusName() + msg);
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
    public static <E> ApiResult<E> fail(BindingResult errors){
        StringBuffer sb = new StringBuffer();
        for(FieldError fieldError :errors.getFieldErrors()){
            sb.append(fieldError.getDefaultMessage()+",");
        }
        String message = StrUtil.sub(sb.toString(), 0, -1);
        return fail(ApiErrorCodeEnum.DefaultParamNotNull.getStatus(),message);
    }
}

