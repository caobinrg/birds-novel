package com.leqiwl.novel.domain.dto;

import cn.hutool.core.util.StrUtil;
import com.leqiwl.novel.common.util.face.IDtoParseUrl;
import com.leqiwl.novel.config.WebMappingUrlConfig;
import lombok.Data;

/**
 * @author: 飞鸟不过江
 * @Date: 2021/12/30 14:01
 * @Description:
 */
@Data
public class ChapterOutDto implements IDtoParseUrl {

    private String novelId;

    private String chapterId;

    private String chapterUrl;

    private int chapterIndex;

    private String idMark;

    private String chapterName;


    @Override
    public void parseUrl() {
        if(StrUtil.isNotBlank(novelId) && StrUtil.isNotBlank(chapterId)){
            this.chapterUrl = WebMappingUrlConfig.instance().getPage()
                    + "/" + novelId
                    + "/" + chapterId;
        }
    }
}
