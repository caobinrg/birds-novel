package com.leqiwl.novel.common.util;

import cn.hutool.core.bean.BeanUtil;
import com.leqiwl.novel.common.util.face.IDtoParseUrl;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 飞鸟不过江
 * @description:
 * @date 2022/1/5 0005 23:30
 */
public class EntityToDtoUtil {

    public static  <T,F> List<T> parseDataList(List<F> sourceDataList, Class<T> clazz)
            throws IllegalAccessException, InstantiationException {
        ArrayList<T> ts = new ArrayList<>();
        for (F sourceData : sourceDataList) {
            T t = clazz.newInstance();
            BeanUtil.copyProperties(sourceData,t);
            ts.add(t);
        }
        return ts;
    }

    public static  <T extends IDtoParseUrl,F> List<T> parseDataListWithUrl(List<F> sourceDataList, Class<T> clazz)
            throws IllegalAccessException, InstantiationException {
        ArrayList<T> ts = new ArrayList<>();
        for (F sourceData : sourceDataList) {
            T t = clazz.newInstance();
            BeanUtil.copyProperties(sourceData,t);
            t.parseUrl();
            ts.add(t);
        }
        return ts;
    }


    public static <T extends IDtoParseUrl,F> T parseDataWithUrl(F sourceData , Class<T> clazz)
            throws IllegalAccessException, InstantiationException {
        T t = clazz.newInstance();
        if(null != sourceData){
            BeanUtil.copyProperties(sourceData,t);
            t.parseUrl();
        }
        return t;
    }

    public static <T extends IDtoParseUrl,F> T parseDataWithUrl(F sourceData , T t){
        if(null != sourceData){
            BeanUtil.copyProperties(sourceData,t);
            t.parseUrl();
        }
        return t;
    }

}
