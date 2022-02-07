package com.leqiwl.novel.common.enums;

/**
 * @author 飞鸟不过江
 */

public enum ApiErrorCodeEnum {

    Success("成功", 0),
    Fail("失败", -1),
    ServerFail("服务器异常", 500),
    ServerRetry("服务繁忙请稍后重试", 510),
    //参数校验异常
    ValidError("参数校验失败", 1000),
    DefaultParamNotNull("参数不完整", -60),


    User_Not_Login("用户未登录",10001),
    User_Not_Find("用户未找到",10002),
    Register_User_Exist("用户已存在",10003),
    User_Is_Login("用户已登陆",10004)
    ;



    private String statusName;
    private Integer status;

    private ApiErrorCodeEnum(String statusName, Integer status) {
        this.statusName = statusName;
        this.status = status;
    }

    public static ApiErrorCodeEnum getParam(String status) {
        ApiErrorCodeEnum apiErrorCodeEnum = get(status);
        return apiErrorCodeEnum == null ? ApiErrorCodeEnum.DefaultParamNotNull : apiErrorCodeEnum;
    }

    public static ApiErrorCodeEnum get(String status) {
        for (ApiErrorCodeEnum o : ApiErrorCodeEnum.values()) {
            if (o.getStatus().toString().equals(status)) {
                return o;
            }
        }
        return null;
    }

    public static String getStatusName(String status) {
        ApiErrorCodeEnum apiErrorCodeEnum = get(status);
        if (apiErrorCodeEnum != null) {
            return apiErrorCodeEnum.getStatusName();
        }
        return null;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public ApiErrorCodeEnum withStatusName(String statusName) {
        this.statusName = statusName;
        return this;
    }
}
