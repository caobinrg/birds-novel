package com.leqiwl.novel.admin.config;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;



@Slf4j
@Configuration
public class ShiroConfig{

    private static final String SECRET_KEY = "sst1234";

    private static final String COOKIE_PATH = "/";

    @Autowired
    private RedisProperties redisProperties;

    @Value("${location.image}")
    private String baseImagePath;

    @Autowired
    private CustomConfig customConfig;

    @Bean(name = "shiroFilter")
    public ShiroFilterFactoryBean shiroFilterFactoryBean(DefaultWebSecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new CustomShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        Map<String, Filter> filters = new HashMap<>();
        filters.put("userSession", new UserSessionFilter());
        shiroFilterFactoryBean.setFilters(filters);

        Map<String, String> filterMap = new LinkedHashMap<>();
        filterMap.put("/webjars/**", "anon");
        filterMap.put("/**/*.css", "anon");
        filterMap.put("/**/*.js", "anon");
        filterMap.put("/**/*.html", "anon");
        filterMap.put("/img/**", "anon");
        filterMap.put("/fonts/**", "anon");
        filterMap.put("/**/favicon.ico", "anon");
        filterMap.put("/**/*.jpg", "anon");
        filterMap.put("/**/*.png", "anon");
        filterMap.put("/index.htm", "anon");
        filterMap.put("/**/doLogin", "anon");
        filterMap.put("/", "user,userSession");
        //???????????????????????????authc,?????????????????????????????????user
        filterMap.put("/**", "user,userSession");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterMap);
        shiroFilterFactoryBean.setLoginUrl(customConfig.getLoginUrl());
        return shiroFilterFactoryBean;
    }


    /**
     * ????????????Realm
     */
    @Bean(name = "customShiroRealm")
    public CustomShiroRealm customShiroRealm() {
        CustomShiroRealm customShiroRealm = new CustomShiroRealm();
        return customShiroRealm;
    }


    @Bean
    public DefaultWebSecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(customShiroRealm());
        securityManager.setRememberMeManager(rememberMeManager());
        // ?????????session?????? ??????redis
        securityManager.setSessionManager(sessionManager());
        // ????????????????????? ??????redis
        securityManager.setCacheManager(redisCacheManager());
        return securityManager;
    }


    @Bean(name = "shiroDialect")
    public ShiroDialect shiroDialect() {
        return new ShiroDialect();
    }

    @Bean
    public RedisManager redisManager() {
        RedisManager redisManager = new RedisManager();
        redisManager.setHost(redisProperties.getHost()+":"+redisProperties.getPort());
        redisManager.setPassword(redisProperties.getPassword());
//        redisManager.setDatabase(redisProperties.getDatabase());
//        redisManager.setPort(redisProperties.getPort());
        return redisManager;
    }


    @Bean
    public RedisCacheManager redisCacheManager() {
        RedisCacheManager redisCacheManager = new RedisCacheManager();
        redisCacheManager.setRedisManager(redisManager());
        return redisCacheManager;
    }

    @Bean
    public RedisSessionDAO redisSessionDAO() {
        RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
        redisSessionDAO.setRedisManager(redisManager());
        return redisSessionDAO;
    }


    @Bean
    public DefaultWebSessionManager sessionManager() {
        CustomSessionManager sessionManager = new CustomSessionManager();
        sessionManager.setSessionDAO(redisSessionDAO());
        sessionManager.setSessionIdCookieEnabled(true);
        sessionManager.setSessionIdCookie(simpleCookie());
        return sessionManager;
    }

    /**
     * ??????sessionId???cookie
     *
     * @return
     */
    @Bean
    public SimpleCookie simpleCookie() {
        SimpleCookie simpleCookie = new SimpleCookie();
        simpleCookie.setName(CommonConstants.SHIRO_COOKIE);
        simpleCookie.setPath(COOKIE_PATH);
        return simpleCookie;
    }

    /**
     * ????????????cookie??????;
     *
     * @return
     */
    @Bean
    public SimpleCookie rememberMeCookie() {
        SimpleCookie simpleCookie = new SimpleCookie();
        simpleCookie.setName(CommonConstants.SHIRO_REMEMBER_ME);
        //<!-- ?????????cookie????????????30??? ,?????????;-->
        simpleCookie.setMaxAge(259200);
        simpleCookie.setPath(COOKIE_PATH);
        return simpleCookie;
    }

    /**
     * cookie????????????;
     * rememberMeManager()???????????????rememberMe??????????????????????????????rememberMe??????????????????securityManager???
     *
     * @return
     */
    @Bean
    public CookieRememberMeManager rememberMeManager() {
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        cookieRememberMeManager.setCookie(rememberMeCookie());
        byte[] bytesOfMessage = null;
        MessageDigest md = null;
        try {
            bytesOfMessage = SECRET_KEY.getBytes("UTF-8");
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
        }
        byte[] b = md.digest(bytesOfMessage);
        //rememberMe cookie??????????????? ?????????????????????????????? ??????AES?????? ????????????(128 256 512 ???)
        cookieRememberMeManager.setCipherKey(b);
        return cookieRememberMeManager;
    }

    /**
     * ??????shiro??????
     *
     * @return
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor() {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor
                = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager());
        return authorizationAttributeSourceAdvisor;
    }

}
