package com.example.file.config;



import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import cn.hutool.json.JSONUtil;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Spring Cache Redis,默认key的时候，@RequestBody的对象需要实现toString
 * @author cc
 * @date 2021-07-12 10:19
 */
@Configuration
public class RedisCacheConfig extends CachingConfigurerSupport {
    /**
     * 过期时间配置，当需要设置其他缓存时间的时候，在这里添加，并且在本类的 getRedisCacheConfigurationMap 中更新
     * @author cc
     * @date 2021-07-12 10:19
     */
    public interface CacheNames {
        String LIST_DATA = "LIST_DATA"; // 列表数据
    }

    /**
     * 过期时间设置
     * @author cc
     * @date 2021-07-12 10:24
     */
    private Map<String, RedisCacheConfiguration> getRedisCacheConfigurationMap() {
        Map<String, RedisCacheConfiguration> redisCacheConfigurationMap = new HashMap<>();
        // 时间单位：秒
        redisCacheConfigurationMap.put(CacheNames.LIST_DATA, this.getRedisCacheConfigurationWithTtl(300));
        return redisCacheConfigurationMap;
    }

    /**
     * 缓存key的生成策略
     * 最终生成的key 为 cache类注解指定的cacheNames::类名:方法名#参数值1,参数值2...
     * @author cc
     * @date 2021-07-12 10:21
     */
    @Override
    @Bean
    public KeyGenerator keyGenerator() {
        return new KeyGenerator() {
            @Override
            public Object generate(Object target, Method method, Object... params) {
                return target.getClass().getName() +
                        "-" +
                        method.getName() +
                        "#" +
                        JSONUtil.toJsonStr(params);
            }
        };
    }

    /**
     * 配置缓存管理器
     * @author cc
     * @date 2021-07-12 10:20
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        return new RedisCacheManager(
                RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory),
                // 默认缓存时间(秒)
                this.getRedisCacheConfigurationWithTtl(1000),
                // 缓存过期时间设置
                this.getRedisCacheConfigurationMap()
        );
    }

    /**
     * redis序列化
     * @author cc
     * @date 2021-07-12 10:20
     */
    private RedisCacheConfiguration getRedisCacheConfigurationWithTtl(Integer seconds) {
        //关键点，spring cache 的注解使用的序列化都从这来，没有这个配置的话使用的jdk自己的序列化，实际上不影响使用，只是打印出来不适合人眼识别
        return RedisCacheConfiguration.defaultCacheConfig()
                // 将 key 序列化成字符串
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                // 将 value 序列化成 json
                // Jackson2JsonRedisSerializer虽然效率和速度更快，但是不能反序列化泛型，所以再想到解决办法之前只能先用GenericJackson2JsonRedisSerializer了
//                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new Jackson2JsonRedisSerializer(Object.class)))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                // 设置缓存过期时间，单位秒
                .entryTtl(Duration.ofSeconds(seconds))
                // 不缓存空值
                .disableCachingNullValues();
    }
}
