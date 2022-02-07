package com.leqiwl.novel.common.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;

/**
 * @author 飞鸟不过江
 * @description:
 * @date 2021/12/26 0026 23:57
 */
public class NovelIdMarkUtil {

    public static String getNovelIdMark(String novelName,String novelAuthor){
        novelName = StrUtil.isBlank(novelName)?"":novelName;
        novelAuthor = StrUtil.isBlank(novelAuthor)?"":novelAuthor;

        return SecureUtil.md5( novelName + novelAuthor);
    }

    public static String getChapterIdMark(String novelName,String chapterName){
        novelName = StrUtil.isBlank(novelName)?"":novelName;
        chapterName = StrUtil.isBlank(chapterName)?"":chapterName;
        return SecureUtil.md5( novelName + chapterName);
    }
}
