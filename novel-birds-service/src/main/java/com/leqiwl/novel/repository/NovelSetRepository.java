package com.leqiwl.novel.repository;

import com.leqiwl.novel.domain.entify.NovelSet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @author: 飞鸟不过江
 * @Date: 2022/1/4 20:12
 * @Description:
 */
public interface NovelSetRepository extends MongoRepository<NovelSet, String> {

    List<NovelSet> findBySetType(String setType);

    NovelSet findNovelSetByNovelIdAndSetType(String novelId,String setType);

    Page<NovelSet> findAllBySetType(String setType, Pageable pageable);

    void deleteBySetTypeAndNovelId(String setType,String novelId);
}
