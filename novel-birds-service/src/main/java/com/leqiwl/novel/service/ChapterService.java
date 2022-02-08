package com.leqiwl.novel.service;

import cn.hutool.core.collection.CollectionUtil;
import com.leqiwl.novel.domain.entify.Chapter;
import com.leqiwl.novel.repository.ChapterRepository;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: 飞鸟不过江
 * @Date: 2022/1/4 14:35
 * @Description:
 */
@Service
public class ChapterService {

    @Resource
    private ChapterRepository chapterRepository;

    @Resource
    private MongoTemplate mongoTemplate;

    @Cacheable(cacheNames = "chapter#1m", key = "#novelId")
    public List<Chapter> findByNovelId(String novelId){
        List<Chapter> chapters = chapterRepository.
                findChapterByNovelIdOrderByChapterIndex(novelId);
        if(CollectionUtil.isEmpty(chapters)){
            return new ArrayList<>();
        }
        return chapters;
    }

    @CachePut(cacheNames = "chapter#1m", key = "#chapter.getNovelId()")
    public List<Chapter> save(Chapter chapter){
        chapterRepository.save(chapter);
        List<Chapter> chapters = chapterRepository.
                findChapterByNovelIdOrderByChapterIndex(chapter.getNovelId());
        if(CollectionUtil.isEmpty(chapters)){
            return new ArrayList<>();
        }
        return chapters;
    }

    @CachePut(cacheNames = "chapter#1m", key = "#novelId")
    public List<Chapter> save(List<Chapter> chapters,String novelId){
        chapterRepository.saveAll(chapters);
        List<Chapter> chapterList = chapterRepository.
                findChapterByNovelIdOrderByChapterIndex(novelId);
        if(CollectionUtil.isEmpty(chapters)){
            return new ArrayList<>();
        }
        return chapterList;
    }

    public Chapter getFirstChapter(String novelId){
        Query query = new Query();
        Criteria criteria = Criteria.where("novelId").is(novelId);
        query.addCriteria(criteria);
        query.with(Sort.by(Sort.Direction.ASC,"chapterIndex")).limit(1);
        List<Chapter> Chapters = mongoTemplate.find(query, Chapter.class);
        if(CollectionUtil.isNotEmpty(Chapters)){
           return Chapters.get(0);
        }
        return new Chapter();
    }

}
