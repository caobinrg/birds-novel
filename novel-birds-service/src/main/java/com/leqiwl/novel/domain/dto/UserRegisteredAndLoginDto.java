package com.leqiwl.novel.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author 飞鸟不过江
 * @description:
 * @date 2022/1/9 0009 20:43
 */
@Data
public class UserRegisteredAndLoginDto {

    @NotBlank(message = "用户名不能为空")
    private String userName;

    @NotBlank(message = "密码不能为空")
    private String userPassword;

}
