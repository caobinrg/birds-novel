package com.leqiwl.novel.web.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.leqiwl.novel.domain.dto.UserInfoOutDto;
import com.leqiwl.novel.domain.entify.User;
import com.leqiwl.novel.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 飞鸟不过江
 * @description:
 * @date 2022/1/5 0005 23:19
 */
@Controller
@RequestMapping("${mapping.user:/web/user}")
public class UserController {

    @Resource
    private UserService userService;

    @RequestMapping({"/",""})
    public String bookRack(HttpServletRequest request, HttpServletResponse response, Model model){
        UserInfoOutDto userInfoOutDto = new UserInfoOutDto();
        String userId = userService.getUserId(request, response);
        boolean loginFlag = false;
        if(StrUtil.isNotBlank(userId)){
            User user = userService.getByUserId(response,userId);
            BeanUtil.copyProperties(user,userInfoOutDto);
            if(null != userInfoOutDto){
                loginFlag = true;
            }
        }
        model.addAttribute("isLogin",loginFlag);
        model.addAttribute("user",userInfoOutDto);
        return "user";
    }

}
