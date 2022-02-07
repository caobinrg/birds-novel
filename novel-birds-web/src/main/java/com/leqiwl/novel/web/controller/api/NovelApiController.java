package com.leqiwl.novel.web.controller.api;

import com.leqiwl.novel.common.base.ApiBaseController;
import com.leqiwl.novel.common.base.ApiResult;
import com.leqiwl.novel.common.util.EntityToDtoUtil;
import com.leqiwl.novel.domain.dto.*;
import com.leqiwl.novel.domain.entify.Chapter;
import com.leqiwl.novel.domain.entify.Novel;
import com.leqiwl.novel.domain.entify.NovelConver;
import com.leqiwl.novel.domain.entify.NovelSet;
import com.leqiwl.novel.enums.NovelSetTypeEnum;
import com.leqiwl.novel.service.*;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author: 飞鸟不过江
 * @Date: 2021/12/30 13:54
 * @Description:
 */
@RestController
@RequestMapping("/api/novel")
public class NovelApiController extends ApiBaseController {

    @Resource
    private NovelService novelService;

    @Resource
    private NovelSetService novelSetService;

    @Resource
    private ChapterService chapterService;

    @Resource
    private ContentService contentService;

    @Resource
    private UserService userService;

    @Resource
    private NovelConverService novelConverService;

    @Resource
    private TopicAndQueuePushService topicPushService;

    @PostMapping("/shortage")
    @ResponseBody
    public ApiResult<?> getShortageData() throws InstantiationException, IllegalAccessException {
        List<NovelSet> shortagesNovelSet = novelSetService.getByRandom(NovelSetTypeEnum.SH.getName());
        List<NovelSetOutDto> shortages = EntityToDtoUtil.parseDataListWithUrl(shortagesNovelSet, NovelSetOutDto.class);
        return ApiResult.ok(shortages);
    }


    @PostMapping("/getNovelByNovelType")
    public ApiResult<NovelInfoByTypeOutDto> getNovelByType(@RequestBody @Validated NovelInfoByTypeInDto novelInfoByTypeInDto)
            throws InstantiationException, IllegalAccessException {
        NovelInfoByTypeOutDto novelInfoByTypeOutDto = new NovelInfoByTypeOutDto();
        Page<Novel> novelPage = novelService.getNovelByType(novelInfoByTypeInDto);
        List<Novel> novels = novelPage.getContent();
        long totalElements = novelPage.getTotalElements();
        novelInfoByTypeOutDto.setPageNo(novelInfoByTypeInDto.getPageNo());
        novelInfoByTypeOutDto.setPageSize(novelInfoByTypeInDto.getPageSize());
        novelInfoByTypeOutDto.setData(EntityToDtoUtil.parseDataListWithUrl(novels,NovelInfoOutDto.class));
        novelInfoByTypeOutDto.setTotal(totalElements);
        return ok(novelInfoByTypeOutDto);
    }

    @PostMapping("/searchNovelByNovelName")
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
        return ok(novelInfoByNameOutDto);
    }


    @PostMapping("/getChapter")
    public ApiResult<?> getChapter(@RequestBody @Validated NovelInfoInDto novelInfoInDto)
            throws InstantiationException, IllegalAccessException {
        String novelId = novelInfoInDto.getNovelId();
        List<Chapter> chapterList = chapterService.findByNovelId(novelId);
        List<ChapterOutDto> chapterOutDtoList =
                EntityToDtoUtil.parseDataListWithUrl(chapterList, ChapterOutDto.class);
        return ok(chapterOutDtoList);
    }

    @PostMapping("/contentInfo")
    public ApiResult<?> getContentInfo(@RequestBody @Validated ContentInDto contentInDto,
                                       HttpServletRequest request, HttpServletResponse response)
            throws IllegalAccessException, InstantiationException {
        String novelId = contentInDto.getNovelId();
        String chapterId = contentInDto.getChapterId();
        ContentOutDto viewContentInfo = contentService.getViewContentInfo(novelId, chapterId);
        viewContentInfo.setNovelId(contentInDto.getNovelId());
        userService.setReadHis(request, response, novelId, chapterId);
        topicPushService.sendRead(novelId);
        return ok(viewContentInfo);
    }

    @PostMapping("/converInfo")
    public ApiResult<?> getRankInfo(@RequestBody @Validated NovelConverInDto converInDto)
            throws InstantiationException, IllegalAccessException {
        int rankType = converInDto.getRankType();
        List<NovelConver> novelConvers = novelConverService.getByRankType(rankType);
        List<NovelConverOutDto> novelConverOutDtos =
                EntityToDtoUtil.parseDataListWithUrl(novelConvers, NovelConverOutDto.class);
        return ok(novelConverOutDtos);
    }
}