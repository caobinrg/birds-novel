package com.leqiwl.novel.service;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.jwt.JWT;
import com.leqiwl.novel.common.exception.ApiPresetException;
import com.leqiwl.novel.common.util.EntityToDtoUtil;
import com.leqiwl.novel.config.sysconst.CookieKeyConst;
import com.leqiwl.novel.domain.dto.CookieReadHisDto;
import com.leqiwl.novel.domain.entify.CookieReadHis;
import com.leqiwl.novel.domain.entify.User;
import com.leqiwl.novel.repository.UserRepository;
import com.leqiwl.novel.util.CookieNovelHisUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.leqiwl.novel.common.enums.ApiErrorCodeEnum.*;

/**
 * @author 飞鸟不过江
 * @description:
 * @date 2022/1/8 0008 23:37
 */
@Service
public class UserService {

    @Value("${jwt.tokenSecretKey}")
    private String tokenSecretKey;

    @Resource
    private UserRepository userRepository;


    public User getByUserId(HttpServletResponse response,String userId){
        Optional<User> userOptional = userRepository.findById(userId);
        User user = userOptional.orElse(null);
        if(null == user){
            //清除cookie
            loginOut(response);
        }
        return user;
    }

    public List<String> getStars( HttpServletRequest request,HttpServletResponse response){
        List<String> stars = new ArrayList<>();
        String userId = getUserId(request, response);
        if(StrUtil.isBlank(userId)){
            return stars;
        }
        User user = getByUserId(response,userId);
        if(null == user){
            return stars;
        }
        return user.getStars();
    }


    public User register(String userName,String password,
                            HttpServletRequest request,HttpServletResponse response)
            throws InstantiationException, IllegalAccessException {
        User user = userRepository.findByUserName(userName);
        if(null != user){
            throw new ApiPresetException(Register_User_Exist);
        }
        List<CookieReadHisDto> cookieReadHisDtos = CookieNovelHisUtil.getNovelHisList(request);
        Date now = new Date();
        user = User.builder()
                .userName(userName)
                .userPassword(SecureUtil.md5(password))
                .viewName(userName)
                .his(EntityToDtoUtil.parseDataList(cookieReadHisDtos, CookieReadHis.class))
                .createTime(now)
                .updateTime(now)
                .build();
        user = userRepository.save(user);
        if(pushToken(user,response)){
            return user;
        }
        return null;
    }

    public User login(String userName,String password,
                         HttpServletRequest request,HttpServletResponse response){
        String userId = getUserId(request, response);
        if(StrUtil.isNotBlank(userId)){
           throw new ApiPresetException(User_Is_Login);
        }
        User user = userRepository.
                findByUserNameAndUserPassword(userName,SecureUtil.md5(password));
        if(null == user){
            return null;
        }
        if(pushToken(user,response)){
            return user;
        }
        return null;
    }

    public boolean loginOut(HttpServletResponse response){
        Cookie cookie = new Cookie(CookieKeyConst.COOKIE_TOKEN, "");
        cookie.setPath("/");
        Cookie cookieThen = new Cookie(CookieKeyConst.COOKIE_TOKEN_LENGTHEN, "");
        cookieThen.setPath("/");
        response.addCookie(cookie);
        response.addCookie(cookieThen);
        return true;
    }


    public boolean pushToken(User user,HttpServletResponse response){
        if(null == user || StrUtil.isBlank(user.getId())){
            return false;
        }
        String token = JWT.create()
                .setExpiresAt(DateUtil.offsetWeek(new Date(),1))
                .setKey(tokenSecretKey.getBytes())
                .setPayload("userId",user.getId())
                .sign();
        Cookie cookie = new Cookie(CookieKeyConst.COOKIE_TOKEN, token);
        cookie.setPath("/");
        String signThen = JWT.create()
                .setKey(tokenSecretKey.getBytes())
                .setExpiresAt(DateUtil.offsetWeek(new Date(), 2))
                .setPayload("userId",user.getId())
                .sign();
        Cookie cookieThen = new Cookie(CookieKeyConst.COOKIE_TOKEN_LENGTHEN, signThen);
        cookieThen.setPath("/");
        response.addCookie(cookie);
        response.addCookie(cookieThen);
        return true;
    }

    public boolean addNovelStar(HttpServletResponse response,String userId,String novelId){
        User user = getByUserId(response,userId);
        if(null == user){
            throw new ApiPresetException(User_Not_Find);
        }
        List<String> stars = user.getStars();
        if(null == stars){
            stars = new ArrayList<>();
        }
        if(stars.size()>0){
            for (int i = 0; i < stars.size(); i++) {
                String dbNovelId = stars.get(i);
                if(novelId.equals(dbNovelId)){
                    stars.remove(dbNovelId);
                    i--;
                }
            }
        }
        stars.add(0,novelId);
        user.setStars(stars);
        userRepository.save(user);
        return true;
    }

    public boolean unNovelStar(HttpServletResponse response,String userId,String novelId){
        User user = getByUserId(response,userId);
        if(null == user){
            throw new ApiPresetException(User_Not_Find);
        }
        List<String> stars = user.getStars();
        if(null == stars){
            stars = new ArrayList<>();
        }
        if(stars.size()>0){
            for (int i = 0; i < stars.size(); i++) {
                String dbNovelId = stars.get(i);
                if(novelId.equals(dbNovelId)){
                    stars.remove(dbNovelId);
                    i--;
                }
            }
        }
        user.setStars(stars);
        userRepository.save(user);
        return true;
    }

    /**
     * 阅读历史入库
     * @param userId
     * @param his
     * @return
     */
    public boolean saveHis(HttpServletResponse response,String userId,List<CookieReadHis> his){
        User user = getByUserId(response,userId);
        if(null == user){
            return false;
        }
        user.setHis(his);
        userRepository.save(user);
        return true;
    }

    /**
     * 校验用户是否登录,从cookie中获取userId
     * @param request
     */
    public String getUserId(HttpServletRequest request,HttpServletResponse response){
        Cookie cookie = ServletUtil.getCookie(request, CookieKeyConst.COOKIE_TOKEN);
        JWT jwt = getJwtFromCookie(cookie);
        if(null == jwt){
            return null;
        }
        boolean verify = jwt.verify();
        if(!verify){
            //验证失败
            return null;
        }
        boolean validate = jwt.validate(0L);
        if(validate){
            //验证通过
            return jwt.getPayload("userId").toString();
        }
        //已失效
        Cookie cookieThen = ServletUtil.getCookie(request, CookieKeyConst.COOKIE_TOKEN_LENGTHEN);
        JWT jwtThen = getJwtFromCookie(cookieThen);
        if(null == jwtThen){
            return null;
        }
        boolean verifyThen = jwtThen.verify();
        if(!verifyThen){
            return null;
        }
        boolean validateThen = jwtThen.validate(0L);
        if(!validateThen){
            return null;
        }
        //延期
        jwt.setExpiresAt(DateUtil.offsetWeek(new Date(),1));
        String sign = jwt.sign();
        cookie.setValue(sign);
        response.addCookie(cookie);
        jwtThen.setExpiresAt(DateUtil.offsetWeek(new Date(), 2));
        String signThen = jwtThen.sign();
        cookieThen.setValue(signThen);
        response.addCookie(cookieThen);
        return jwt.getPayload("userId").toString();
    }

    public void setReadHis(HttpServletRequest request, HttpServletResponse response,
                            String novelId, String chapterId) throws InstantiationException, IllegalAccessException {
        CookieReadHisDto cookieReadHisDto = CookieReadHisDto.builder()
                .novelId(novelId)
                .chapterId(chapterId)
                .build();
        Cookie birdsReadHisCookie = ServletUtil.getCookie(request, CookieKeyConst.COOKIE_HIS_KEY);
        List<CookieReadHisDto> cookieReadHisDtos = CookieNovelHisUtil.getNovelHisList(request);
        if(null == birdsReadHisCookie){
            birdsReadHisCookie = new Cookie( CookieKeyConst.COOKIE_HIS_KEY,"");
        }
        //最多存储10条记录
        for (int i = 0; i < cookieReadHisDtos.size() ; i++) {
            String hisNovelId = cookieReadHisDtos.get(i).getNovelId();
            if(novelId.equals(hisNovelId)){
                cookieReadHisDtos.remove(i);
                i--;
            }
        }
        cookieReadHisDtos.add(0,cookieReadHisDto);
        if(cookieReadHisDtos.size()>10){
            cookieReadHisDtos = CollectionUtil.sub(cookieReadHisDtos,0,10);
        }
        //阅读历史入库
        String userId = getUserId(request, response);
        if(StrUtil.isNotBlank(userId)){
            saveHis(response,userId,EntityToDtoUtil.parseDataList(cookieReadHisDtos,CookieReadHis.class));
        }
        birdsReadHisCookie.setValue(Base64.encode(JSONUtil.toJsonStr(cookieReadHisDtos)));
        birdsReadHisCookie.setPath("/");
        response.addCookie(birdsReadHisCookie);
    }




    private JWT getJwtFromCookie(Cookie cookie){
        if(null == cookie){
            return null;
        }
        String tokenStr = cookie.getValue();
        if(StrUtil.isBlank(tokenStr)){
            return null;
        }
        JWT jwt = JWT.of(tokenStr).setKey(tokenSecretKey.getBytes());
        return jwt;
    }

}
