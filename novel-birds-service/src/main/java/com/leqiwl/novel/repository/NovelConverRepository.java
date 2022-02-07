package com.leqiwl.novel.repository;

import com.leqiwl.novel.domain.entify.NovelConver;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author 飞鸟不过江
 */
@Repository
public interface NovelConverRepository extends MongoRepository<NovelConver, String>{

    NovelConver findByNovelId(String novelId);
}
