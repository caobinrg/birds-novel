package com.leqiwl.novel.domain.entify;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class CookieReadHis {

    private String novelId;

    private String chapterId;

}