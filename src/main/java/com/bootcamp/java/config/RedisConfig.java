package com.bootcamp.java.config;


import com.bootcamp.java.dto.ClientResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Slf4j
@Configuration
@ConditionalOnProperty(name = "cache.enabled", havingValue = "true")
public class RedisConfig {

    //private static final String KEY = "clientresponse";

    public static String key;

    @Value("${KEY_REDIS_NAME:clientresponse}")
    public void setKey(String KEY_REDIS_NAME) {
        RedisConfig.key = KEY_REDIS_NAME;
    }

    @Bean
    public ReactiveHashOperations<String, String, ClientResponseDTO> hashOperations(ReactiveRedisConnectionFactory redisConnectionFactory){
        var template = new ReactiveRedisTemplate<>(
                redisConnectionFactory,
                RedisSerializationContext.<String, ClientResponseDTO>newSerializationContext(new StringRedisSerializer())
                                         .hashKey(new GenericToStringSerializer<>(String.class)) //Aqui era Integer
                                         .hashValue(new Jackson2JsonRedisSerializer<>(ClientResponseDTO.class))
                                         .build()
        );

        return template.opsForHash();
    }


}
