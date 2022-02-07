package com.leqiwl.novel.job.pip;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.tokenizer.Result;
import cn.hutool.extra.tokenizer.TokenizerEngine;
import cn.hutool.extra.tokenizer.Word;
import com.leqiwl.novel.domain.entify.NovelType;
import com.leqiwl.novel.repository.NovelTypeRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author: 飞鸟不过江
 * @Date: 2022/1/27 15:15
 * @Description:
 */
@Slf4j
@SpringBootTest(classes = CrawlerTestApplication.class)
@RunWith(SpringRunner.class)
public class AnalyzerTest {


    @Resource
    private NovelTypeRepository novelTypeRepository;

    @Resource
    private TokenizerEngine tokenizerEngine;


    @Test
    public void analyzer(){
        List<NovelType> all = novelTypeRepository.findAll();
        log.info("getTypeMap:{}",getTypeMap(all));
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
