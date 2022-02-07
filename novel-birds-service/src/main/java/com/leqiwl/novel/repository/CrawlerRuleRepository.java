package com.leqiwl.novel.repository;

import com.leqiwl.novel.domain.entify.crawler.CrawlerRule;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;



/**
 * @author 飞鸟不过江
 */
@Repository
public interface CrawlerRuleRepository extends MongoRepository<CrawlerRule, String>{


    CrawlerRule findByRuleId(String ruleId);


}
