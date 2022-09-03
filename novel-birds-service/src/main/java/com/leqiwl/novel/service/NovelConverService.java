package com.leqiwl.novel.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.leqiwl.novel.domain.entify.Novel;
import com.leqiwl.novel.domain.entify.NovelConver;
import com.leqiwl.novel.enums.RankTypeEnum;
import com.leqiwl.novel.repository.NovelConverRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author: 飞鸟不过江
 * @Date: 2022/1/10 11:02
 * @Description:
 */
@Service
public class NovelConverService {

    @Resource
    private NovelConverRepository novelConverRepository;

    @Resource
    private MongoTemplate mongoTemplate;

    @Resource
    private NovelService novelService;


    public NovelConver getByNovelId(String novelId){
       return novelConverRepository.findByNovelId(novelId);
    }

    public NovelConver save(NovelConver novelConver){
        NovelConver save = novelConverRepository.save(novelConver);
        return save;
    }

    public NovelConver generateConver(String novelId){
        Novel novel = novelService.getByNovelId(novelId);
        NovelConver novelConver = new NovelConver();
        if(null == novel || StrUtil.isBlank(novel.getNovelId())){
            return novelConver ;
        }
        BeanUtil.copyProperties(novel,novelConver);
        Date date = new Date();
        novelConver.setCreateTime(date);
        novelConver.setUpdateTime(date);
        return novelConver ;
    }

    @Cacheable(value = "novelConver#10m",key = "#rankType")
    public List<NovelConver> getByRankType(int rankType){
        RankTypeEnum rankTypeEnum = RankTypeEnum.getByType(rankType);
        if(null == rankTypeEnum){
            return new ArrayList<>();
        }
        Query query = new Query();
        query.with(Sort.by(Sort.Direction.DESC,rankTypeEnum.getColumn())).limit(20);
        List<NovelConver> novelConvers = mongoTemplate.find(query, NovelConver.class);
        if(CollectionUtil.isEmpty(novelConvers)){
            return new ArrayList<>();
        }
        return novelConvers;
    }

    @Cacheable(value = "novelConver#10m",key = "#rankType")
    public List<NovelConver> getByRankTypeWithSkip(int rankType,int skip){
        RankTypeEnum rankTypeEnum = RankTypeEnum.getByType(rankType);
        if(null == rankTypeEnum){
            return new ArrayList<>();
        }
        Query query = new Query();
        query.with(Sort.by(Sort.Direction.DESC,rankTypeEnum.getColumn())).skip(skip).limit(20);
        List<NovelConver> novelConvers = mongoTemplate.find(query, NovelConver.class);
        if(CollectionUtil.isEmpty(novelConvers)){
            return new ArrayList<>();
        }
        return novelConvers;
    }

}
