package com.leqiwl.novel.admin.config;



public final class CommonConstants {

    /**超级管理员code*/
    public static final String SUPER_ADMIN = "SUPER_ADMIN";

    /**超级管理员code*/
    public static final String SUPER_ADMIN_CN = "超级管理员";

    /**普通用户code*/
    public static final String NORMAL_USER = "NORMAL_USER";

    /**session中用户信息key*/
    public static final String SESSION_USER_INFO = "loginUser";

    /**页面上保存配置的key*/
    public static final String GLOBAL_CONFIG = "globalConfig";

    /**cookie中保存sessionId的key*/
    public static final String SHIRO_COOKIE = "shiro.cookie";
    /**cookie中保存记住密码数据的key*/
    public static final String SHIRO_REMEMBER_ME = "shiro.rememberMe";

    /**url中sessionId的key*/
    public static final String URL_TOKEN = "token";

    /**用户的默认初始密码*/
    public static final String DEFAULT_PASSWORD = "12345678";

    /**用户的状态*/
    public static class UserStatus{
        /**正常*/
        public static final int AVAILABLE = 1;
        /**禁用*/
        public static final int DISABLED = 0;
    }

}
