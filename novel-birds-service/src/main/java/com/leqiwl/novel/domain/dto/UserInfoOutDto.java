package com.leqiwl.novel.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.leqiwl.novel.domain.entify.CookieReadHis;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

/**
 * @author 飞鸟不过江
 * @description:
 * @date 2022/1/8 0008 23:36
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Document(collection = "user")
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserInfoOutDto {


    private String userName;


    private String viewName;

    /**
     * 收藏书籍id列表
     */
    private List<String> stars;

    private List<CookieReadHis> his;

    private Date createTime;

}
