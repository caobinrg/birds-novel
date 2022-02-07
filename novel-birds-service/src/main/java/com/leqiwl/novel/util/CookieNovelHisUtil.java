package com.leqiwl.novel.util;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.json.JSONUtil;
import com.leqiwl.novel.config.sysconst.CookieKeyConst;
import com.leqiwl.novel.domain.dto.CookieReadHisDto;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 飞鸟不过江
 * @description:
 * @date 2022/1/7 0007 1:08
 */
public class CookieNovelHisUtil {

    public static List<CookieReadHisDto> getNovelHisList(HttpServletRequest request){
        List<CookieReadHisDto> cookieReadHisDtoList = new ArrayList<>();
        if(null == request){
            return cookieReadHisDtoList;
        }
        Cookie cookie = ServletUtil.getCookie(request, CookieKeyConst.COOKIE_HIS_KEY);
        if(null != cookie) {
            String value = cookie.getValue();
            if (StrUtil.isNotBlank(value)) {
                cookieReadHisDtoList =
                        JSONUtil.toList(Base64.decodeStr(value), CookieReadHisDto.class);
            }
        }
        return cookieReadHisDtoList;
    }
}
