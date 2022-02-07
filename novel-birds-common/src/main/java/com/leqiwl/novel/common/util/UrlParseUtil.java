package com.leqiwl.novel.common.util;

import cn.hutool.core.util.URLUtil;

/**
 * @author 飞鸟不过江
 * @description:
 * @date 2022/2/4 0004 20:58
 */
public class UrlParseUtil {

    public static String urlReduction(String baseUrl,String url){
        String start = "http";
        if(baseUrl.startsWith("https")){
            start = "https";
        }
        if(url.startsWith("http") || url.startsWith("//")){
            return url;
        }
        if(url.startsWith("/")){
            String host = URLUtil.url(baseUrl).getHost();
            url =  host + url;
        }else{
            url = baseUrl.endsWith("/") ? baseUrl : (baseUrl + "/") + url;
        }
        if(!url.startsWith(start) && !url.startsWith("//")){
            url = start + "://" + url;
        }
        return url;
    }
}
