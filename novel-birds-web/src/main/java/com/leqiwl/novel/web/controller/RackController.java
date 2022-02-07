package com.leqiwl.novel.web.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.leqiwl.novel.common.util.EntityToDtoUtil;
import com.leqiwl.novel.domain.dto.CookieReadHisDto;
import com.leqiwl.novel.domain.dto.NovelInfoOutDto;
import com.leqiwl.novel.domain.entify.Novel;
import com.leqiwl.novel.domain.entify.User;
import com.leqiwl.novel.service.NovelService;
import com.leqiwl.novel.service.UserService;
import com.leqiwl.novel.util.CookieNovelHisUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 飞鸟不过江
 * @description:
 * @date 2022/1/5 0005 23:19
 */
@Controller
@RequestMapping("${mapping.bookrack:/web/bookrack}")
public class RackController {

    @Resource
    private NovelService novelService;

    @Resource
    private UserService userService;

    @RequestMapping({"","/"})
    public String bookRack(HttpServletRequest request, HttpServletResponse response, Model model)
            throws InstantiationException, IllegalAccessException {
        List<NovelInfoOutDto> novelInfoOutDtos = new ArrayList<>();
        List<NovelInfoOutDto> novelInfoOutStarDtos = new ArrayList<>();
        List<CookieReadHisDto> novelHisList = CookieNovelHisUtil.getNovelHisList(request);
        if(CollectionUtil.isNotEmpty(novelHisList)){
            List<String> novelIds = novelHisList.stream().map(item -> item.getNovelId()).collect(Collectors.toList());
            List<Novel> novelByNovelIds = novelService.getNovelByNovelIds(novelIds);
            novelInfoOutDtos = EntityToDtoUtil.parseDataListWithUrl(novelByNovelIds, NovelInfoOutDto.class);
        }
        String userId = userService.getUserId(request, response);
        if(StrUtil.isNotBlank(userId)){
            User user = userService.getByUserId(response,userId);
            if(null != user){
                List<String> stars = user.getStars();
                if(CollectionUtil.isNotEmpty(stars)){
                    List<Novel> novelByNovelIds = novelService.getNovelByNovelIds(stars);
                    novelInfoOutStarDtos = EntityToDtoUtil.parseDataListWithUrl(novelByNovelIds, NovelInfoOutDto.class);
                }
            }
        }
        model.addAttribute("novelHis",novelInfoOutDtos);
        model.addAttribute("isLogin",false);
        model.addAttribute("rackNovels",novelInfoOutStarDtos);
        return "bookrack";
    }

}
