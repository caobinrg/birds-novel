package com.leqiwl.novel.domain.entify;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * @author 飞鸟不过江
 * @description:
 * @date 2021/12/26 0026 19:58
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Document(collection = "content")
@CompoundIndexes({
        @CompoundIndex(name = "chapter_index",def = "{novelId:1,chapterId:1}")
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class Content implements Serializable {

    @Id
    private String id;

    private String novelId;

    private String novelName;

    private String chapterId;

    private String name;

    private String contentText;

    private String relUrl;

    @Builder.Default
    private int isActive = 1;

}
