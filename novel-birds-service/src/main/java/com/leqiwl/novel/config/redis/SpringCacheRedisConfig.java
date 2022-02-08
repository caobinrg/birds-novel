package com.leqiwl.novel.config.redis;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leqiwl.novel.config.sysconst.RedisKeyConst;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 飞鸟不过江
 */
@Configuration
@EnableCaching
@EnableAspectJAutoProxy(exposeProxy = true)
public class SpringCacheRedisConfig {


    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        ExpireRedisCacheManager cacheManager = new ExpireRedisCacheManager(
                RedisCacheWriter.nonLockingRedisCacheWriter(factory),
                // 默认策略，未配置的 key 会使用这个
                this.getRedisCacheConfigurationWithTtl(10 * 60),
                //指定key策略
                this.getRedisCacheConfigurationMap()
        );

        return cacheManager;
    }

    private Map<String, RedisCacheConfiguration> getRedisCacheConfigurationMap() {
        Map<String, RedisCacheConfiguration> redisCacheConfigurationMap = new HashMap<>();
        //进行过期时间配置
        redisCacheConfigurationMap.put("24h", this.getRedisCacheConfigurationWithTtl(24 * 60 * 60));
        redisCacheConfigurationMap.put("6h", this.getRedisCacheConfigurationWithTtl(6 * 60 * 60));
        redisCacheConfigurationMap.put("1h", this.getRedisCacheConfigurationWithTtl(60 * 60));
        redisCacheConfigurationMap.put("30m", this.getRedisCacheConfigurationWithTtl(30 * 60));
        redisCacheConfigurationMap.put("10m", this.getRedisCacheConfigurationWithTtl(10 * 60));
        redisCacheConfigurationMap.put("5m", this.getRedisCacheConfigurationWithTtl(5 * 60));
        redisCacheConfigurationMap.put("2m", this.getRedisCacheConfigurationWithTtl(2 * 60));
        redisCacheConfigurationMap.put("1m", this.getRedisCacheConfigurationWithTtl(1 * 60));
        return redisCacheConfigurationMap;
    }

    private RedisCacheConfiguration getRedisCacheConfigurationWithTtl(Integer seconds) {
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        //解决查询缓存转换异常的问题
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig();
        redisCacheConfiguration = redisCacheConfiguration.serializeValuesWith(
                RedisSerializationContext
                        .SerializationPair
                        .fromSerializer(jackson2JsonRedisSerializer)
        )
                //定义默认的cache time-to-live. -1表示永不过期.
                .entryTtl(Duration.ofSeconds(seconds))
                .computePrefixWith(cacheName -> RedisKeyConst.serviceKeySpace.concat(cacheName.concat(":")))
                .disableCachingNullValues() //如果是空值，不缓存
        ;
        return redisCacheConfiguration;
    }

    /**
     * 所有参数 顺序组成key controller+method:{param1}:{param2}:{param3}
     * 如果参数不存在，使用controller+method
     *
     * @return
     */
    @Bean
    public KeyGenerator keyGenerator() {
        return new KeyGenerator() {
            @Override
            public Object generate(Object target, Method method, Object... params) {
                StringBuilder sb = new StringBuilder();
                String className = target.getClass().getName();
                String[] names = className.split("\\.");
                if (names.length > 0) {
                    sb.append(names[names.length - 1].toLowerCase()).append(".").append(method.getName().toLowerCase());
                }
                if (params == null || params.length == 0 || params[0] == null) {
                    return sb.toString();
                }
                String join = String.join(":", Arrays.stream(params).map(Object::toString).collect(Collectors.toList()));
                String format = String.format("%s:%s", sb.toString(), join);
                return format;
            }
        };
    }
}
