package com.leqiwl.novel.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.leqiwl.novel.domain.dto.NovelInfoBySetTypeInDto;
import com.leqiwl.novel.domain.dto.NovelSetInDto;
import com.leqiwl.novel.domain.entify.Novel;
import com.leqiwl.novel.domain.entify.NovelSet;
import com.leqiwl.novel.repository.NovelSetRepository;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author: 飞鸟不过江
 * @Date: 2022/1/4 20:13
 * @Description:
 */
@Service
public class NovelSetService {

    @Resource
    private NovelSetRepository novelSetRepository;

    @Resource
    private NovelService novelService;

    @Resource
    private MongoTemplate mongoTemplate;


    @CachePut(cacheNames = "novelset#10m", key="#setType")
    public List<NovelSet> saveByType(String novelId,String setType){
        Novel novel = novelService.getByNovelId(novelId);
        if(StrUtil.isNotBlank(novel.getNovelId())){
            NovelSet dbNovelSet = novelSetRepository.findNovelSetByNovelIdAndSetType(novelId, setType);
            if(null == dbNovelSet){
                NovelSet novelSet = new NovelSet();
                BeanUtil.copyProperties(novel,novelSet);
                novelSet.setId(null);
                novelSet.setSetType(setType);
                novelSet.setCreateTime(new Date());
                novelSetRepository.save(novelSet);
            }
        }
        List<NovelSet> novelSetList = novelSetRepository.findBySetType(setType);
        if(CollectionUtil.isEmpty(novelSetList)){
            return new ArrayList<>();
        }
        return novelSetList;
    }


    @Cacheable(cacheNames = "novelset#10m", key = "#setType")
    public List<NovelSet> getBySetType(String setType){
        List<NovelSet> novelSetList = novelSetRepository.findBySetType(setType);
        if(CollectionUtil.isEmpty(novelSetList)){
            return new ArrayList<>();
        }
        return novelSetList;
    }


    public Page<NovelSet> getPageBySetType(NovelInfoBySetTypeInDto dto){
        Pageable pageable = PageRequest.of(dto.getPageNo(),dto.getPageSize(),
                Sort.by(Sort.Direction.DESC,"createTime"));
        if(StrUtil.isBlank(dto.getSetType())){
            return  novelSetRepository.findAll(pageable);
        }
        return novelSetRepository.findAllBySetType(dto.getSetType(), pageable);
    }

    public void removeSet(NovelSetInDto dto){
       String novelId = dto.getNovelId();
         String setType = dto.getSetType();
        novelSetRepository.deleteBySetTypeAndNovelId(setType,novelId);
    }



    public List<NovelSet> getByRandom(String setType){
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("setType").is(setType)),
                Aggregation.sample(6)
        );
        AggregationResults<NovelSet> results = mongoTemplate.aggregate(agg, NovelSet.class, NovelSet.class);
        return results.getMappedResults();

    }


}
