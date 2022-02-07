package com.leqiwl.novel.repository;

import com.leqiwl.novel.domain.entify.Novel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 飞鸟不过江
 */
@Repository
public interface NovelRepository extends MongoRepository<Novel, String>{

    Novel getNovelByIdMark(String idMark);

    Novel getNovelByNovelId(String novelId);

    List<Novel> getNovelsByNovelIdIn(List<String> novelIdList);

    Page<Novel> findAllByNovelType(String novelType, Pageable pageable);

    Page<Novel> findAllByNameLike(String name,Pageable pageable);

}
