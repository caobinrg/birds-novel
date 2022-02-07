package com.leqiwl.novel.repository;

import com.leqiwl.novel.domain.entify.Chapter;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * @author 飞鸟不过江
 */
@Repository
public interface ChapterRepository extends MongoRepository<Chapter, String>{

    List<Chapter> findChapterByNovelIdOrderByChapterIndex(String novelId);

}
