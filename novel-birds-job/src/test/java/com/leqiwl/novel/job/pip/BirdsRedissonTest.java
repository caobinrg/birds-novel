package com.leqiwl.novel.job.pip;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.Redisson;
import org.redisson.api.RSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@SpringBootTest(classes = CrawlerTestApplication.class)
@RunWith(SpringRunner.class)
public class BirdsRedissonTest {

    @Autowired
    private Redisson redisson;

    @Test
    public void TestAdd() {
        RSet<Object> test = redisson.getSet("test");
        test.add("飞鸟不过江");
    }

    @Test
    public void TestRemove(){
        RSet<Object> test = redisson.getSet("test");
        for (Object o : test) {
            System.out.println(o.toString());
        }
        test.remove("飞鸟不过江");
    }

}