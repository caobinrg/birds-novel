package com.leqiwl.novel.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.tokenizer.Result;
import cn.hutool.extra.tokenizer.TokenizerEngine;
import cn.hutool.extra.tokenizer.Word;
import com.leqiwl.novel.domain.entify.NovelType;
import com.leqiwl.novel.repository.NovelTypeRepository;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: 飞鸟不过江
 * @Date: 2021/12/31 11:20
 * @Description:
 */
@Service
public class NovelTypeService {

    @Resource
    private NovelTypeRepository novelTypeRepository;

    @Resource
    private TokenizerEngine tokenizerEngine;

    @CachePut(cacheNames = "novelTypeMap#24h", key="'noveTypeMap'")
    public Map<String,String> saveAll(List<NovelType> types){
        novelTypeRepository.saveAll(types);
        List<NovelType> all = novelTypeRepository.findAll();
        return this.getTypeMap(all);
    }

    @CachePut(cacheNames = "novelTypeMap#24h", key="'noveTypeMap'")
    public Map<String,String> save(NovelType types){
        novelTypeRepository.save(types);
        List<NovelType> all = novelTypeRepository.findAll();
        return this.getTypeMap(all);
    }

    @Cacheable(cacheNames = "novelTypeMap#24h", key="'noveTypeMap'")
    public Map<String,String> getTypeMap(){
        List<NovelType> all = novelTypeRepository.findAll();
        return  this.getTypeMap(all);
    }

    public List<NovelType> getNovelTypes(){
        List<NovelType> all = novelTypeRepository.findAll();
        if(CollectionUtil.isEmpty(all)){
            return new ArrayList<>();
        }
        return all;
    }

    public boolean hasData(){
        long count = novelTypeRepository.count();
        if(count>0){
            return true;
        }
        return false;
    }

    private Map<String,String> getTypeMap(List<NovelType> novelTypes){
        novelTypes = novelTypeRepository.findAll();
        Map<String, String> result = new HashMap<>();
        for (NovelType novelType : novelTypes) {
            String type = novelType.getType();
            String typeWords = novelType.getTypeWords();
            if(StrUtil.isNotBlank(typeWords)){
                //解析文本
                Result typeAnalyzerResult = tokenizerEngine.parse(typeWords);
                //输出：这 两个 方法 的 区别 在于 返回 值
                List<String> typeWordList =
                        CollUtil.newArrayList((Iterator<Word>) typeAnalyzerResult)
                                .stream()
                                .distinct()
                                .map(Word::getText)
                                .collect(Collectors.toList());
                for (String word : typeWordList) {
                    result.putIfAbsent(word,type);
                }
            }
        }
        return result;
    }

}
