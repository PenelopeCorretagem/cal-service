package com.penelopec.calservice.shared.cache;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.Map;

@Configuration
@EnableCaching
public class RedisConfig implements CachingConfigurer {

    private static final Logger log = LoggerFactory.getLogger(RedisConfig.class);
    private static final String APP_PACKAGE = "com.penelopec.calservice";

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig())
                .withInitialCacheConfigurations(Map.of(
                        CacheNames.APPOINTMENT,    ttl(Duration.ofMinutes(15)),
                        CacheNames.APPOINTMENTS,   ttl(Duration.ofMinutes(5)),
                    CacheNames.SCHEDULES,      ttl(Duration.ofMinutes(10)),
                        CacheNames.EVENT_TYPE,     ttl(Duration.ofHours(1)),
                        CacheNames.EVENT_TYPES,    ttl(Duration.ofHours(1)),
                        CacheNames.AVAILABLE_SLOTS, ttl(Duration.ofMinutes(2)),
                        CacheNames.ESTATES,         ttl(Duration.ofMinutes(5))
                ))
                .build();
    }

    @Override
    public CacheErrorHandler errorHandler() {
        return new CacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException e, Cache cache, Object key) {
                log.warn("Redis GET falhou — cache='{}', key='{}': {}", cache.getName(), key, e.getMessage());
            }
            @Override
            public void handleCachePutError(RuntimeException e, Cache cache, Object key, Object value) {
                log.warn("Redis PUT falhou — cache='{}', key='{}': {}", cache.getName(), key, e.getMessage());
            }
            @Override
            public void handleCacheEvictError(RuntimeException e, Cache cache, Object key) {
                log.warn("Redis EVICT falhou — cache='{}', key='{}': {}", cache.getName(), key, e.getMessage());
            }
            @Override
            public void handleCacheClearError(RuntimeException e, Cache cache) {
                log.warn("Redis CLEAR falhou — cache='{}': {}", cache.getName(), e.getMessage());
            }
        };
    }

    private RedisCacheConfiguration defaultConfig() {
        BasicPolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
            .allowIfSubType(APP_PACKAGE)
            .allowIfSubType("java.util")
            .allowIfSubType("java.time")
            .build();

        ObjectMapper jackson = JsonMapper.builder()
                .addModule(new JavaTimeModule())
            .addModule(new ParameterNamesModule())
            .activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.NON_FINAL)
                .build();

        jackson.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        var serializer = new GenericJackson2JsonRedisSerializer(jackson);

        return RedisCacheConfiguration.defaultCacheConfig()
                .disableCachingNullValues()
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(serializer));
    }

    private RedisCacheConfiguration ttl(Duration ttl) {
        return defaultConfig().entryTtl(ttl);
    }
}
