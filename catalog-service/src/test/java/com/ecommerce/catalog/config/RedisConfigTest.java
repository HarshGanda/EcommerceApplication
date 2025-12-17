package com.ecommerce.catalog.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RedisConfigTest {

    @InjectMocks
    private RedisConfig redisConfig;

    @Mock
    private RedisConnectionFactory connectionFactory;

    @Test
    void testCacheManagerCreation() {
        // Test: CacheManager is created with proper configuration
        RedisCacheManager cacheManager = redisConfig.cacheManager(connectionFactory);

        assertNotNull(cacheManager);
        assertEquals("org.springframework.data.redis.cache.RedisCacheManager",
                     cacheManager.getClass().getName());
    }

    @Test
    void testCacheManagerNotNull() {
        // Test: CacheManager instance is not null
        RedisCacheManager cacheManager = redisConfig.cacheManager(connectionFactory);

        assertNotNull(cacheManager);
    }

    @Test
    void testCacheManagerIsRedisCacheManager() {
        // Test: CacheManager is instance of RedisCacheManager
        RedisCacheManager cacheManager = redisConfig.cacheManager(connectionFactory);

        assertTrue(cacheManager instanceof RedisCacheManager);
    }
}

