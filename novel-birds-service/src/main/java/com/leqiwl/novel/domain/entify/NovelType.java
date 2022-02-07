package com.leqiwl.novel.domain.entify;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author: 飞鸟不过江
 * @Date: 2021/12/31 11:16
 * @Description:
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Document(collection = "novelType")
@JsonIgnoreProperties(ignoreUnknown = true)
public class NovelType {

    @Id
    private String id;

    private String type;

    private String typeWords;

}
