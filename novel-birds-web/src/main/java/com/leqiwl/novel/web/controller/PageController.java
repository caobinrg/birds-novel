package com.leqiwl.novel.web.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.leqiwl.novel.common.util.EntityToDtoUtil;
import com.leqiwl.novel.config.WebMappingUrlConfig;
import com.leqiwl.novel.domain.dto.ContentOutDto;
import com.leqiwl.novel.domain.dto.CookieReadHisDto;
import com.leqiwl.novel.domain.dto.NovelInfoOutDto;
import com.leqiwl.novel.domain.entify.Chapter;
import com.leqiwl.novel.domain.entify.Novel;
import com.leqiwl.novel.service.*;
import com.leqiwl.novel.util.CookieNovelHisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 飞鸟不过江
 * @description:
 * @date 2022/1/5 0005 1:58
 */
@Slf4j
@Controller
@RequestMapping("${mapping.page:/web/page}")
public class PageController {

    @Value("${mapping.home:/web/home}")
    private String home;

    @Resource
    private NovelService novelService;

    @Resource
    private ChapterService chapterService;

    @Resource
    private ContentService contentService;

    @Resource
    private UserService userService;

    @Resource
    private TopicAndQueuePushService topicPushService;


    @RequestMapping({"","/"})
    public void pageIndex(HttpServletResponse response) throws IOException {
        response.sendRedirect(home);
    }

    @GetMapping({"/{novelId}","/{novelId}.html"})
    public String novelInfo(HttpServletRequest request,HttpServletResponse response,
                            @PathVariable String novelId, Model model)
            throws IOException, InstantiationException, IllegalAccessException {
        Novel novel = novelService.getByNovelId(novelId);
        if(null == novel || StrUtil.isBlank(novel.getNovelId())){
          // todo 默认错误页面
            response.sendRedirect(home);
        }
        NovelInfoOutDto novelInfoOutDto = EntityToDtoUtil.parseDataWithUrl(novel,NovelInfoOutDto.class);
        List<CookieReadHisDto> novelHisList = CookieNovelHisUtil.getNovelHisList(request);
        String chapterId = "";
        boolean firstChapterFlag = false;
        for (CookieReadHisDto cookieReadHisDto : novelHisList) {
            if(novelId.equals(cookieReadHisDto.getNovelId())){
                chapterId = cookieReadHisDto.getChapterId();
                break;
            }
        }
        if(StrUtil.isBlank(chapterId)){
            Chapter firstChapter = chapterService.getFirstChapter(novelId);
            chapterId = firstChapter.getChapterId() == null ? "" : firstChapter.getChapterId();
            firstChapterFlag = true;
        }
        List<String> stars = userService.getStars(request, response);
        boolean isStart = false;
        if(CollectionUtil.isNotEmpty(stars)){
            for (String star : stars) {
                if(star.equals(novelId)){
                    isStart = true;
                    break;
                }
            }
        }
        topicPushService.sendClick(novelId);
        model.addAttribute("isFirst",firstChapterFlag);
        model.addAttribute("chapterId",chapterId);
        model.addAttribute("chapterUrl", WebMappingUrlConfig.instance().getPage()+"/"+novelId+"/"+chapterId + ".html");
        model.addAttribute("novel",novelInfoOutDto);
        model.addAttribute("isStar",isStart);
        return "book";
    }

    @GetMapping({"/{novelId}/{chapterId}","/{novelId}/{chapterId}.html"})
    public String readInfo(HttpServletRequest request,
                           HttpServletResponse response,
                           @PathVariable String novelId,
                           @PathVariable String chapterId, Model model)
            throws IllegalAccessException, InstantiationException {
        ContentOutDto viewContentInfo = contentService.getViewContentInfo(novelId, chapterId);
        model.addAttribute("contentInfo",viewContentInfo);
        userService.setReadHis(request, response, novelId, chapterId);
        topicPushService.sendRead(novelId);
        return "read";
    }





}
