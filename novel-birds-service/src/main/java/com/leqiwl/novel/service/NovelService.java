package com.leqiwl.novel.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.leqiwl.novel.domain.dto.NovelInfoByNameInDto;
import com.leqiwl.novel.domain.dto.NovelInfoByTypeInDto;
import com.leqiwl.novel.domain.entify.Novel;
import com.leqiwl.novel.repository.NovelRepository;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author 飞鸟不过江
 * @description:
 * @date 2021/12/27 0027 0:04
 */
@Slf4j
@Service
public class NovelService {

    @Resource
    private NovelRepository novelRepository;

    @Resource
    private MongoTemplate mongoTemplate;

    @Resource
    private RedissonClient redissonClient;

    @CachePut(cacheNames = "novel#2m", key="#novel.getIdMark()")
    public Novel save(Novel novel) throws InterruptedException {
        RLock lock = null;
        try {
            lock = redissonClient.getLock("novelSave" + novel.getIdMark());
            lock.tryLock(3,6, TimeUnit.SECONDS);
            Novel dbNovel = this.novelRepository.getNovelByIdMark(novel.getIdMark());
            if(null == dbNovel){
                return this.novelRepository.save(novel);
            }
            if(novel.getNovelId().equals(dbNovel.getNovelId())){
                novel.setId(dbNovel.getId());
                return this.novelRepository.save(novel);
            }
            //idMark 冲突，忽略改书
           return dbNovel;
        } catch (InterruptedException e) {
           throw e;
        }finally {
            if(null != lock){
                lock.unlock();
            }
        }
    }

    @Cacheable(cacheNames = "novel#2m", key = "#idMark")
    public Novel getByIdMark(String idMark){
        Novel novel = this.novelRepository.getNovelByIdMark(idMark);
        if(null == novel){
            return new Novel();
        }
        return novel;
    }

    @Cacheable(cacheNames = "novelById#2m", key = "#novelId")
    public Novel getByNovelId(String novelId){
        Novel novel = this.novelRepository.getNovelByNovelId(novelId);
        if(null == novel){
            return new Novel();
        }
        return novel;
    }

    @Cacheable(cacheNames = "novelLast#10m")
    public List<Novel> getLastUpdate(){
        Query query = new Query();
        query.with(Sort.by(Sort.Direction.DESC,"updateTime")).limit(6);
        List<Novel> novels = mongoTemplate.find(query, Novel.class);
        if(CollectionUtil.isEmpty(novels)){
            return new ArrayList<>();
        }
        return novels;
    }

    public List<Novel> getNovelByNovelIds(List<String> novelIdList){
        List<Novel> novels = novelRepository.getNovelsByNovelIdIn(novelIdList);
        Map<String, Novel> collect =
                novels.stream().collect(Collectors.toMap(Novel::getNovelId, novel -> novel));
        List<Novel> result = new ArrayList<>();
        for (String novelId : novelIdList) {
            if(null != collect.get(novelId)){
                result.add(collect.get(novelId));
            }
        }
        return result;
    }


    public Page<Novel> getNovelByType(NovelInfoByTypeInDto dto){
        Pageable pageable = PageRequest.of(dto.getPageNo(),dto.getPageSize(),
                Sort.by(Sort.Direction.DESC,"updateTime"));
        return novelRepository.findAllByNovelType(dto.getNovelType(), pageable);
    }

    public Page<Novel> getNovelByName(NovelInfoByNameInDto dto){
        Pageable pageable = PageRequest.of(dto.getPageNo(),dto.getPageSize(),
                Sort.by(Sort.Direction.DESC,"updateTime"));
        String novelName = dto.getNovelName();
        if(StrUtil.isBlank(dto.getNovelName())){
            return novelRepository.findAll(pageable);
        }
        return novelRepository.findAllByNameLike(novelName, pageable);
    }



    @CacheEvict(cacheNames="novel#10m", allEntries=true)
    public void reloadNovel(){
        return;
    }


}
