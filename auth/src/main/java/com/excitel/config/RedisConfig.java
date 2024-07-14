package com.excitel.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
/**
 * This class defines configuration beans for connecting to and interacting with a Redis server.
 * It also configures Redis caching for the application.
 *
 * @author (your name here) (if applicable)
 * @since (version of your application) (if applicable)
 */
@Configuration
@EnableCaching
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private int redisPort;
    /**
     * Creates and returns a bean of type {@link LettuceConnectionFactory}.
     * This bean is used to establish a connection to the Redis server.
     *
     * @return A bean of type {@link LettuceConnectionFactory}.
     */
    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(redisHost, redisPort);
    }

    /**
     * Creates and returns a bean of type {@link StringRedisTemplate}.
     * This bean is a specialized template for String key-value operations in Redis.
     * It uses String serializers for both keys and values.
     *
     * @param redisConnectionFactory An instance of {@link RedisConnectionFactory}
     * used to connect to the Redis server.
     * @return A bean of type {@link StringRedisTemplate}.
     */
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }
    /**
     * Creates and returns a bean of type {@link RedisTemplate}.
     * This bean is a general-purpose template for interacting with Redis
     * and supports various data types for keys and values. It uses String serializers
     * for both keys and values in this configuration.
     *
     * @param connectionFactory An instance of {@link RedisConnectionFactory}
     * used to connect to the Redis server.
     * @return A bean of type {@link RedisTemplate}.
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }
    /**
     * Creates and returns a bean of type {@link RedisCacheManager}.
     * This bean is used to manage Redis caches within the application.
     * It configures a default cache configuration with appropriate settings.
     *
     * @param redisConnectionFactory An instance of {@link RedisConnectionFactory}
     * used to connect to the Redis server.
     * @return A bean of type {@link RedisCacheManager}.
     */
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(config)
                .build();
    }
}