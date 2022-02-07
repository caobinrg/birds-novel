package com.leqiwl.novel.job.register;

import com.leqiwl.novel.job.job.SpiderStart;
import com.leqiwl.novel.job.pip.SpiderStartContainer;
import com.leqiwl.novel.remote.SpiderContainerRemote;
import com.leqiwl.novel.remote.SpiderJobStartRemote;
import org.redisson.api.RRemoteService;
import org.redisson.api.RedissonClient;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author: 飞鸟不过江
 * @Date: 2022/1/5 10:25
 * @Description: 服务注册，供rpc调用
 */
@Component
@Order(999)
public class RemoteJobRegister implements ApplicationRunner {

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private SpiderStartContainer spiderStartContainer;

    @Resource
    private SpiderStart spiderStart;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // spider 注册
        RRemoteService remoteService = redissonClient.getRemoteService();
        remoteService.register(SpiderContainerRemote.class,spiderStartContainer);
        remoteService.register(SpiderJobStartRemote.class,spiderStart);
    }
}
