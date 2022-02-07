package com.leqiwl.novel.admin.controller;

import com.leqiwl.novel.common.base.ApiResult;
import com.leqiwl.novel.common.util.EntityToDtoUtil;
import com.leqiwl.novel.domain.dto.*;
import com.leqiwl.novel.domain.entify.Novel;
import com.leqiwl.novel.domain.entify.NovelSet;
import com.leqiwl.novel.service.NovelService;
import com.leqiwl.novel.service.NovelSetService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: 飞鸟不过江
 * @Date: 2022/1/28 19:29
 * @Description:
 */
@Controller
@RequestMapping("/recommend")
public class RecommendController {


    @Resource
    private NovelSetService novelSetService;

    @Resource
    private NovelService novelService;


    @GetMapping("/view")
    public String recommendView(){
        return "view/book/recommend";
    }


    @PostMapping("/set")
    @ResponseBody
    public ApiResult<?> set(@RequestBody @Validated NovelSetInDto novelSetInDto){
        List<NovelSet> novelSetList =
                novelSetService.saveByType(novelSetInDto.getNovelId(), novelSetInDto.getSetType());
        return ApiResult.ok(novelSetList);
    }

    @PostMapping("/getByType")
    @ResponseBody
    public ApiResult<?> getByType(@RequestBody @Validated NovelInfoBySetTypeInDto novelInfoBySetTypeInDto)
            throws InstantiationException, IllegalAccessException {
        NovelInfoBySetTypeOutDto novelInfoBySetTypeOutDto = new NovelInfoBySetTypeOutDto();
        Page<NovelSet> page = novelSetService.getPageBySetType(novelInfoBySetTypeInDto);
        List<NovelSet> novelSets = page.getContent();
        long totalElements = page.getTotalElements();
        novelInfoBySetTypeOutDto.setTotal(totalElements);
        novelInfoBySetTypeOutDto.setData( EntityToDtoUtil.parseDataListWithUrl(novelSets, NovelInfoOutDto.class));
        return ApiResult.ok(novelInfoBySetTypeOutDto);
    }


    @PostMapping("/getNovelByName")
    @ResponseBody
    public ApiResult<NovelInfoByNameOutDto> getNovelByName(@RequestBody @Validated NovelInfoByNameInDto novelInfoByNameInDto)
            throws InstantiationException, IllegalAccessException {
        NovelInfoByNameOutDto novelInfoByNameOutDto = new NovelInfoByNameOutDto();
        Page<Novel> novelPage = novelService.getNovelByName(novelInfoByNameInDto);
        List<Novel> novels = novelPage.getContent();
        long totalElements = novelPage.getTotalElements();
        novelInfoByNameOutDto.setPageNo(novelInfoByNameInDto.getPageNo());
        novelInfoByNameOutDto.setPageSize(novelInfoByNameInDto.getPageSize());
        novelInfoByNameOutDto.setData(EntityToDtoUtil.parseDataListWithUrl(novels,NovelInfoOutDto.class));
        novelInfoByNameOutDto.setTotal(totalElements);
        return ApiResult.ok(novelInfoByNameOutDto);
    }



    @PostMapping("/remove")
    @ResponseBody
    public ApiResult<?> remove(@RequestBody @Validated NovelSetInDto novelSetInDto){
        novelSetService.removeSet(novelSetInDto);
        return ApiResult.ok("删除成功");
    }



}
