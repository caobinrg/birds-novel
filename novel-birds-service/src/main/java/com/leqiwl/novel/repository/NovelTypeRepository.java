package com.leqiwl.novel.repository;

import com.leqiwl.novel.domain.entify.NovelType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author: 飞鸟不过江
 * @Date: 2021/12/31 11:18
 * @Description:
 */
@Repository
public interface NovelTypeRepository extends MongoRepository<NovelType, String> {

}
