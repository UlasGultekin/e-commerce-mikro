package com.ulasgltkn.cartservice.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@Configuration
@EnableCaching
public class RedisConfig {

    /**
     * @EnableCaching:
     *   - Spring Cache mekanizmasını aktif eder.
     *   - @Cacheable, @CachePut, @CacheEvict anotasyonlarını kullanabilmemizi sağlar.
     *
     * CacheManager:
     *   - Uygulamadaki cache'lerin nasıl yönetileceğini belirler.
     *   - Burada RedisCacheManager ile Redis'i cache backend olarak kullanıyoruz.
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        return RedisCacheManager.builder(connectionFactory)
                .build();
    }
}
