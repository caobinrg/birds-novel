package com.leqiwl.novel.repository;

import com.leqiwl.novel.domain.entify.Content;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 飞鸟不过江
 */
@Repository
public interface ContentRepository extends MongoRepository<Content, String>{

    Content getContentByNovelIdAndChapterId(String novelId,String chapterId);

    List<Content> findAllByNovelIdAndChapterId(String novelId,String chapterId);
}
