package com.leqiwl.novel.domain.entify;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author: 飞鸟不过江
 * @Date: 2022/1/4 20:02
 * @Description:
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Document(collection = "novelSet")
@JsonIgnoreProperties(ignoreUnknown = true)
@CompoundIndexes({
        @CompoundIndex(name = "novelSet_index",def = "{novelId:1,setType:1}",unique = true)
})
public class NovelSet {


    @Id
    private String id;

    private String setType;

    private String novelId;

    /**
     * 标识
     */
    private String idMark;

    /**
     * 小说名称
     */
    private String name;
    /**
     * 小说作者
     */
    private String author;
    /**
     * 小说图片
     */
    private String image;

    /**
     * 图片本地位置
     */
    private String imagePath;
    /**
     * 小说字数(万字)
     */
    private String wordNum;
    /**
     * 小说状态
     */
    private String updateStatus;
    /**
     * 小说评分
     */
    private String score;
    /**
     * 评分人数
     */
    private String scorePersonNum;
    /**
     * 更新标识
     */
    private String lastUpdateMark;

    /**
     * 小说类型
     */
    private String novelType;

    private List<String> tags;

    /**
     * 小说标签，以逗号,分割
     */
    private String keywords;
    /**
     * 小说简介
     */
    private String intro;


    private Date createTime;

    public void setNovelType(String novelType) {
        this.novelType = novelType;
        if(StrUtil.isNotBlank(novelType)){
            this.tags = Collections.singletonList(novelType);
        }
    }
}
