package com.leqiwl.novel.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.leqiwl.novel.config.sysconst.PathConst;

/**
 * @author 飞鸟不过江
 * @description:
 * @date 2022/1/5 0005 1:36
 */
public class ImagePathCheckUtil {

    public static boolean check(String path){
        if(StrUtil.isNotBlank(path)){
            String basePath = PathConst.novelImagePath;
            if(StrUtil.isNotBlank(basePath) && basePath.endsWith("/")){
                basePath = StrUtil.subPre(basePath,basePath.length()-1);
            }
            String localImagePath = basePath + path;
            if(FileUtil.exist(localImagePath)){
                return true;
            }
        }
        return false;
    }
}
