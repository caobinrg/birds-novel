package com.leqiwl.novel.web.controller.api;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.leqiwl.novel.common.base.ApiBaseController;
import com.leqiwl.novel.common.base.ApiResult;
import com.leqiwl.novel.common.util.EntityToDtoUtil;
import com.leqiwl.novel.domain.dto.CookieReadHisDto;
import com.leqiwl.novel.domain.dto.NovelInfoOutDto;
import com.leqiwl.novel.domain.dto.UserInfoOutDto;
import com.leqiwl.novel.domain.dto.UserRegisteredAndLoginDto;
import com.leqiwl.novel.domain.entify.Novel;
import com.leqiwl.novel.domain.entify.User;
import com.leqiwl.novel.service.NovelService;
import com.leqiwl.novel.service.TopicAndQueuePushService;
import com.leqiwl.novel.service.UserService;
import com.leqiwl.novel.util.CookieNovelHisUtil;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.leqiwl.novel.common.enums.ApiErrorCodeEnum.User_Not_Login;

/**
 * @author 飞鸟不过江
 * @description:
 * @date 2022/1/9 0009 17:44
 */
@RestController
@RequestMapping("/api/user")
public class UserApiController extends ApiBaseController {


    @Resource
    private UserService userService;

    @Resource
    private NovelService novelService;

    @Resource
    private TopicAndQueuePushService topicPushService;


    @PostMapping("/getUserInfo")
    public ApiResult<UserInfoOutDto> getUserInfo(HttpServletRequest request, HttpServletResponse response){
        String userId = userService.getUserId(request, response);
        if(StrUtil.isBlank(userId)){
            fail("用户未登陆");
        }
        User user = userService.getByUserId(response,userId);
        if(null == user){
            fail("用户错误");
        }
        UserInfoOutDto userInfoOutDto = new UserInfoOutDto();
        BeanUtil.copyProperties(user,userInfoOutDto);
        return ok(userInfoOutDto);
    }


    @PostMapping("/register")
    public ApiResult<?> register(@RequestBody @Validated UserRegisteredAndLoginDto userRegisteredDto,
                                HttpServletRequest request,HttpServletResponse response)
            throws IllegalAccessException, InstantiationException {

        User user = userService.register(userRegisteredDto.getUserName(),
                userRegisteredDto.getUserPassword(), request, response);
        if(null != user){
            UserInfoOutDto userInfoOutDto = new UserInfoOutDto();
            BeanUtil.copyProperties(user,userInfoOutDto);
            return ok(userInfoOutDto);
        }else{
            return fail("注册失败");
        }
    }

    @PostMapping("/login")
    public ApiResult<?> login(@RequestBody @Validated UserRegisteredAndLoginDto userLoginDto,
                              HttpServletRequest request,HttpServletResponse response){
        User user = userService.login(userLoginDto.getUserName(), userLoginDto.getUserPassword(), request, response);
        if(null != user){
            UserInfoOutDto userInfoOutDto = new UserInfoOutDto();
            BeanUtil.copyProperties(user,userInfoOutDto);
            return ok(userInfoOutDto);
        }else{
            return fail("登录失败");
        }
    }

    @PostMapping("/loginOut")
    public ApiResult<?> loginOut(HttpServletResponse response){
        boolean loginOut = userService.loginOut(response);
        if(loginOut){
            return ok("注销成功");
        }else{
            return ok("注销失败");
        }
    }

    /**
     * 书籍收藏（加入书架）
     * @return
     */
    @PostMapping("/bookStar")
    public ApiResult<?> bookStar(@RequestBody @Validated CookieReadHisDto cookieReadHisDto,
                                 HttpServletRequest request, HttpServletResponse response)
            throws IllegalAccessException, InstantiationException {
        String userId = userService.getUserId(request, response);
        if(StrUtil.isBlank(userId)){
            //用户未登录需要重新登录
            return fail(User_Not_Login);
        }
        String novelId = cookieReadHisDto.getNovelId();
        String chapterId = cookieReadHisDto.getChapterId();
        boolean save = userService.addNovelStar(response,userId, novelId);
        userService.setReadHis(request,response,novelId,chapterId);
        topicPushService.sendStar(novelId);
        if(save){
            return ok("收藏成功");
        }
        return fail("收藏失败");
    }


    /**
     * 取消收藏
     * @return
     */
    @PostMapping("/unBookStar")
    public ApiResult<?> unBookStar(@RequestBody @Validated CookieReadHisDto cookieReadHisDto,
                                 HttpServletRequest request, HttpServletResponse response){
        String userId = userService.getUserId(request, response);
        if(StrUtil.isBlank(userId)){
            //用户未登录需要重新登录
            return fail(User_Not_Login);
        }
        String novelId = cookieReadHisDto.getNovelId();
        boolean save = userService.unNovelStar(response,userId, novelId);
        if(save){
            return ok("取消收藏成功");
        }
        return fail("取消收藏失败");
    }

    @PostMapping("/getBookStar")
    public ApiResult<?> getBookStar(HttpServletRequest request, HttpServletResponse response)
            throws InstantiationException, IllegalAccessException {
        List<NovelInfoOutDto> novelInfoOutStarDtos = new ArrayList<>();
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
        return ok(novelInfoOutStarDtos);
    }

    @PostMapping("/getBookHis")
    public ApiResult<?> getBookHis(HttpServletRequest request, HttpServletResponse response)
            throws InstantiationException, IllegalAccessException {
        List<NovelInfoOutDto> novelInfoOutDtos = new ArrayList<>();
        List<CookieReadHisDto> novelHisList = CookieNovelHisUtil.getNovelHisList(request);
        if(CollectionUtil.isNotEmpty(novelHisList)){
            List<String> novelIds = novelHisList.stream().map(item -> item.getNovelId()).collect(Collectors.toList());
            List<Novel> novelByNovelIds = novelService.getNovelByNovelIds(novelIds);
            novelInfoOutDtos = EntityToDtoUtil.parseDataListWithUrl(novelByNovelIds, NovelInfoOutDto.class);
        }
        return ok(novelInfoOutDtos);
    }

}
