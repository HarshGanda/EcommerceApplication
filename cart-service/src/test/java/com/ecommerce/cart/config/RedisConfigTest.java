package com.ecommerce.cart.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RedisConfigTest {

    @InjectMocks
    private RedisConfig redisConfig;

    @Mock
    private RedisConnectionFactory connectionFactory;

    @Test
    void testRedisTemplateCreation() {
        // Test: RedisTemplate is created with proper serializers
        RedisTemplate<String, Object> redisTemplate = redisConfig.redisTemplate(connectionFactory);

        assertNotNull(redisTemplate);
        assertNotNull(redisTemplate.getKeySerializer());
        assertNotNull(redisTemplate.getValueSerializer());
        assertNotNull(redisTemplate.getHashKeySerializer());
        assertNotNull(redisTemplate.getHashValueSerializer());
        assertEquals(connectionFactory, redisTemplate.getConnectionFactory());
    }

    @Test
    void testCacheManagerCreation() {
        // Test: CacheManager is created with proper configuration
        CacheManager cacheManager = redisConfig.cacheManager(connectionFactory);

        assertNotNull(cacheManager);
        assertEquals("org.springframework.data.redis.cache.RedisCacheManager",
                     cacheManager.getClass().getName());
    }

    @Test
    void testRedisTemplateKeySerializer() {
        // Test: Key serializer is StringRedisSerializer
        RedisTemplate<String, Object> redisTemplate = redisConfig.redisTemplate(connectionFactory);

        assertEquals("org.springframework.data.redis.serializer.StringRedisSerializer",
                     redisTemplate.getKeySerializer().getClass().getName());
    }

    @Test
    void testRedisTemplateValueSerializer() {
        // Test: Value serializer is GenericJackson2JsonRedisSerializer
        RedisTemplate<String, Object> redisTemplate = redisConfig.redisTemplate(connectionFactory);

        assertEquals("org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer",
                     redisTemplate.getValueSerializer().getClass().getName());
    }
}

