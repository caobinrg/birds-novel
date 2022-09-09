package com.leqiwl.novel.web.controller;

import com.leqiwl.novel.common.util.EntityToDtoUtil;
import com.leqiwl.novel.domain.dto.NovelConverOutDto;
import com.leqiwl.novel.domain.dto.NovelInfoByTypeOutDto;
import com.leqiwl.novel.domain.entify.NovelConver;
import com.leqiwl.novel.domain.entify.NovelType;
import com.leqiwl.novel.enums.RankTypeEnum;
import com.leqiwl.novel.service.NovelConverService;
import com.leqiwl.novel.service.NovelService;
import com.leqiwl.novel.service.NovelTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.List;


/**
 * @author: CaoBin
 * @Date: 2022-08-31 18:00
 * @Description:
 */
@Slf4j
@Controller
@RequestMapping("${mapping.classify:/web/classify}")
public class ClassifyController {

    @Resource
    private NovelService novelService;

    @Resource
    private NovelConverService novelConverService;

    @Resource
    private NovelTypeService novelTypeService;

    @GetMapping({"/",""})
    public String getHome(Model model) throws InstantiationException, IllegalAccessException {
        List<NovelType> novelTypes = novelTypeService.getNovelTypes();
        LinkedHashMap<String, NovelInfoByTypeOutDto> typesLinkedMap = new LinkedHashMap<>();
        for (NovelType novelType : novelTypes) {
            String type = novelType.getType();
            typesLinkedMap.put(type,novelService.getNovelByTypeWithPage(type));
        }
        model.addAttribute("novelTypes",typesLinkedMap);
        //点击
        model.addAttribute("clickRank",getRank(RankTypeEnum.Click));
        //阅读
        model.addAttribute("readRank",getRankWithSkip(RankTypeEnum.Read,20));
        //收藏
        model.addAttribute("starRank",getRank(RankTypeEnum.STAR));
        return "classify";
    }

    private List<NovelConverOutDto> getRank(RankTypeEnum rankTypeEnum)
            throws InstantiationException, IllegalAccessException {
        List<NovelConver> novelConvers = novelConverService.getByRankType(rankTypeEnum.getType());
        return EntityToDtoUtil.parseDataListWithUrl(novelConvers, NovelConverOutDto.class);
    }

    private List<NovelConverOutDto> getRankWithSkip(RankTypeEnum rankTypeEnum,int skip)
            throws InstantiationException, IllegalAccessException {
        List<NovelConver> novelConvers = novelConverService.getByRankTypeWithSkip(rankTypeEnum.getType(),skip);
        return EntityToDtoUtil.parseDataListWithUrl(novelConvers, NovelConverOutDto.class);
    }

}
