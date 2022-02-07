package com.leqiwl.novel.config.redis;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;

import java.util.*;


/**
 * @author 飞鸟不过江
 */
@Slf4j
public class ExpireRedisCacheManager extends RedisCacheManager {

    /**
     *  过期时间分隔符
     */
    private static final String SEPARATOR = "#";

    private Map<String, RedisCacheConfiguration>  expireCacheConfigurations;

    public ExpireRedisCacheManager(RedisCacheWriter cacheWriter,
                                   RedisCacheConfiguration defaultCacheConfiguration,
                                   Map<String, RedisCacheConfiguration> initialCacheConfigurations) {
        super(cacheWriter, defaultCacheConfiguration, initialCacheConfigurations);
        this.expireCacheConfigurations = initialCacheConfigurations;
    }

    @Override
    protected RedisCache createRedisCache(String name, RedisCacheConfiguration cacheConfig) {
        if(StrUtil.isNotBlank(name) && name.contains(SEPARATOR)){
            String[] split = name.split(SEPARATOR);
            name = split[0] + "("+split[1]+")";
            RedisCacheConfiguration expireConfiguration = expireCacheConfigurations.get(split[1]);
            if(null != expireConfiguration){
                cacheConfig = expireConfiguration;
            }
        }
        return super.createRedisCache(name, cacheConfig);
    }
}
