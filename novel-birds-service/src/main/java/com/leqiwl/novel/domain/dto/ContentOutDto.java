package com.leqiwl.novel.domain.dto;

import cn.hutool.core.util.StrUtil;
import com.leqiwl.novel.common.util.face.IDtoParseUrl;
import com.leqiwl.novel.config.WebMappingUrlConfig;
import lombok.Data;

/**
 * @author: 飞鸟不过江
 * @Date: 2021/12/30 14:02
 * @Description:
 */
@Data
public class ContentOutDto implements IDtoParseUrl {

    private String novelId;

    private String novelName;

    private String chapterId;

    private String name;

    private String contentText;

    private String lastChapterId;

    private String lastChapterUrl;

    private String nextChapterId;

    private String nextChapterUrl;

    private boolean hasDataFlag = false;

    @Override
    public void parseUrl() {
        if(StrUtil.isNotBlank(lastChapterId)){
            this.lastChapterUrl = WebMappingUrlConfig.instance().getPage()
                    + "/" + novelId
                    + "/" + lastChapterId
                    + ".html";
        }
        if(StrUtil.isNotBlank(nextChapterId)){
            this.nextChapterUrl = WebMappingUrlConfig.instance().getPage()
                    + "/" + novelId
                    + "/" + nextChapterId
                    + ".html";
        }
    }
}
