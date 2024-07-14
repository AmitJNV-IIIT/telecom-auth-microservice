package com.excitel.config;

import org.junit.jupiter.api.Test;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class RedisConfigTest {
    private final RedisConfig config = new RedisConfig();

    @Test
    public void testRedisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName("localhost");
        config.setPort(6379);
        LettuceConnectionFactory factory = new LettuceConnectionFactory(config);
        assertThat(factory).isNotNull();
    }

    @Test
    public void testStringRedisTemplate() {
        RedisConnectionFactory factory = mock(RedisConnectionFactory.class);
        StringRedisTemplate template = config.stringRedisTemplate(factory);
        assertThat(template).isNotNull();
    }

    @Test
    public void testRedisTemplate() {
        RedisConnectionFactory factory = mock(RedisConnectionFactory.class);
        RedisTemplate<String, Object> template = config.redisTemplate(factory);
        assertThat(template).isNotNull();
    }

    @Test
    public void testCacheManager() {
        RedisConnectionFactory factory = mock(RedisConnectionFactory.class);
        RedisCacheManager cacheManager = config.cacheManager(factory);
        assertThat(cacheManager).isNotNull();
    }
}
