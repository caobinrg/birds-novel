package com.leqiwl.novel.domain.dto;

import lombok.Data;

/**
 * @author 飞鸟不过江
 * @description:
 * @date 2022/2/6 0006 10:49
 */
@Data
public class SysUserLoginInDto {

    private String userName;

    private String passWord;

    private boolean rememberMe;

}
