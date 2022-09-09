package com.leqiwl.novel.web.interceptor;


import cn.hutool.core.util.StrUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.leqiwl.novel.common.enums.ClientTypeEnum;
import com.leqiwl.novel.web.config.NovelWebMvcConfigurer;
import com.leqiwl.novel.web.config.WebBaseUrlConfig;
import com.leqiwl.novel.web.config.WebSiteConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 通用配置(拦截器)
 */
@Slf4j
@Configuration
@AutoConfigureBefore({NovelWebMvcConfigurer.class})
public class ViewInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private WebSiteConfig webSiteConfig;

    @Autowired
    private WebBaseUrlConfig webBaseUrlConfig;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        String viewNamePrefix = ClientTypeEnum.PC.getName();
        if(null != modelAndView){
            String viewName = modelAndView.getViewName();
            if(judgeMobile(request)){
                viewNamePrefix = ClientTypeEnum.MOBILE.getName();
            }
            // 开发期参数
            viewNamePrefix = ClientTypeEnum.MOBILE.getName();
            viewName = viewNamePrefix + "/" + viewName;
            modelAndView.setViewName(viewName);
            Map<String, Object> model = modelAndView.getModel();
            model.put("webSiteName",webSiteConfig.getName());
            model.put("webSiteKeyWords",webSiteConfig.getKeywords());
            model.put("webSiteDescription",webSiteConfig.getDescription());
            model.put("webBaseUrlConfig",webBaseUrlConfig);
            model.put("webAds",judgeAds(request));
        }
        super.postHandle(request, response, handler, modelAndView);
    }


    private boolean judgeAds(HttpServletRequest request){
        String userAgentString = request.getHeader("User-Agent");
        String lowerCaseUa = userAgentString.toLowerCase();
        if(lowerCaseUa.contains("spider") ||
                lowerCaseUa.contains("bot") ||
                lowerCaseUa.contains("crawler")){
            return false;
        }
        return true;
    }

    private boolean judgeMobile(HttpServletRequest request){
        String userAgentString = request.getHeader("User-Agent");
        if(StrUtil.isBlank(userAgentString)){
            return false;
        }
        UserAgent userAgent = UserAgentUtil.parse(userAgentString);
        if(null == userAgent){
            return false;
        }
        return userAgent.isMobile();
    }
}

