package com.leqiwl.novel.domain.entify;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * @author 飞鸟不过江
 * @description:
 * @date 2021/12/26 0026 19:55
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(collection = "chapter")
public class Chapter implements Serializable {

    @Id
    private String id;

    @Indexed(name ="chapter_novelId")
    private String novelId;

    private String chapterId;

    @Indexed(name = "chapter_chapterIndex")
    private int chapterIndex;

    @Indexed(name = "chapter_idMark")
    private String idMark;

    private String chapterName;

    private String novelName;

    private String chapterUrl;

    private String pageUrl;

    private String ruleId;

}
