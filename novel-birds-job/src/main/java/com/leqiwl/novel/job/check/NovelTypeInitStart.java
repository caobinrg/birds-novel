package com.leqiwl.novel.job.check;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;
import com.leqiwl.novel.domain.entify.NovelType;
import com.leqiwl.novel.service.NovelTypeService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: 飞鸟不过江
 * @Date: 2021/12/31 12:11
 * @Description: 类型持久化
 */
@Component
@Order(19)
public class NovelTypeInitStart implements ApplicationRunner {


    @Resource
    private NovelTypeService novelTypeService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if(!novelTypeService.hasData()){
            String s = ResourceUtil.readUtf8Str("novelTypeInit.json");
            List<NovelType> novelTypes = JSONUtil.toList(s, NovelType.class);
            novelTypeService.saveAll(novelTypes);
        }
    }
}
