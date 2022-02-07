package com.leqiwl.novel.web.controller;

import com.leqiwl.novel.common.util.EntityToDtoUtil;
import com.leqiwl.novel.domain.dto.NovelInfoOutDto;
import com.leqiwl.novel.domain.dto.NovelSetOutDto;
import com.leqiwl.novel.domain.entify.Novel;
import com.leqiwl.novel.domain.entify.NovelSet;
import com.leqiwl.novel.enums.NovelSetTypeEnum;
import com.leqiwl.novel.service.NovelService;
import com.leqiwl.novel.service.NovelSetService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

/**
 * @author 飞鸟不过江
 * @description:
 * @date 2021/12/30 0030 23:31
 */
@Controller
@RequestMapping("${mapping.home:/web/home}")
public class HomeController {

    @Resource
    private NovelSetService novelSetService;

    @Resource
    private NovelService novelService;


    @GetMapping({"/",""})
    public String getHome(Model model) throws InstantiationException, IllegalAccessException {
        List<Novel> lastUpdateNovel = novelService.getLastUpdate();
        List<NovelInfoOutDto> lastUpdate = EntityToDtoUtil.parseDataListWithUrl(lastUpdateNovel,NovelInfoOutDto.class);
        HashMap<String, List<?>> novels = new HashMap<>();
        novels.put("update",lastUpdate);
        List<NovelSet> recommendsNovelSet = novelSetService.getBySetType(NovelSetTypeEnum.ZZ.getName());
        List<NovelSetOutDto> recommends = EntityToDtoUtil.parseDataListWithUrl(recommendsNovelSet, NovelSetOutDto.class);
        novels.put("recommend",recommends);
        List<NovelSet> shortagesNovelSet = novelSetService.getByRandom(NovelSetTypeEnum.SH.getName());
        List<NovelSetOutDto> shortages = EntityToDtoUtil.parseDataListWithUrl(shortagesNovelSet, NovelSetOutDto.class);
        novels.put("shortage",shortages);
        model.addAttribute("novels",novels);
        return "home";
    }
}
