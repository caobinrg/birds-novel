package com.leqiwl.novel.admin.controller;

import com.leqiwl.novel.common.base.ApiResult;
import com.leqiwl.novel.common.util.EntityToDtoUtil;
import com.leqiwl.novel.domain.dto.NovelInfoByNameInDto;
import com.leqiwl.novel.domain.dto.NovelInfoByNameOutDto;
import com.leqiwl.novel.domain.dto.NovelInfoOutDto;
import com.leqiwl.novel.domain.entify.Novel;
import com.leqiwl.novel.service.NovelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @author 飞鸟不过江
 * @description:
 * @date 2022/2/1 0001 15:03
 */
@Controller
@RequestMapping("/book")
public class BookController {


    @Autowired
    private NovelService novelService;

    @GetMapping("queryView")
    public String query(){
        return "view/book/query";
    }

    @PostMapping("/queryByName")
    @ResponseBody
    public ApiResult<?> queryByName(@RequestBody NovelInfoByNameInDto novelInfoByNameInDto)
            throws InstantiationException, IllegalAccessException {
        Page<Novel> novelByName = novelService.getNovelByName(novelInfoByNameInDto);
        NovelInfoByNameOutDto novelInfoByNameOutDto = new NovelInfoByNameOutDto();
        novelInfoByNameOutDto.setTotal(novelByName.getTotalElements());
        novelInfoByNameOutDto.setData(
                EntityToDtoUtil.parseDataListWithUrl(novelByName.getContent(), NovelInfoOutDto.class)
        );
        return ApiResult.ok(novelInfoByNameOutDto);
    }


}
