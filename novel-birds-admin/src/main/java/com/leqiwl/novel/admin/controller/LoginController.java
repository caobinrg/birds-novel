package com.leqiwl.novel.admin.controller;

import com.leqiwl.novel.admin.config.CustomLoginConfig;
import com.leqiwl.novel.common.base.ApiResult;
import com.leqiwl.novel.domain.dto.SysUserLoginInDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @author: 飞鸟不过江
 * @Date: 2022/1/28 17:03
 * @Description:
 */
@Slf4j
@Controller
@RequestMapping("/login")
public class LoginController {




    @GetMapping("")
    public String login(){
        return "login";
    }


    @PostMapping("/doLogin")
    @ResponseBody
    public ApiResult<?> doLogin(@RequestBody SysUserLoginInDto sysUserLoginInDto) {
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(
                sysUserLoginInDto.getUserName(),
                sysUserLoginInDto.getPassWord(),
                sysUserLoginInDto.isRememberMe());
        try {
            subject.login(token);
        } catch (AuthenticationException e) {
            token.clear();
            throw e;
        }
        return ApiResult.ok("success");
    }

    /**
     * 退出登录
     *
     * @return
     */
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult<?> logout() {
        SecurityUtils.getSubject().logout();
        return ApiResult.ok("success");
    }

}
