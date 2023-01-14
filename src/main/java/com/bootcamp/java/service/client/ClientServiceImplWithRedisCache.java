package com.bootcamp.java.service.client;

import com.bootcamp.java.config.RedisConfig;
import com.bootcamp.java.dto.ClientDTO;
import com.bootcamp.java.dto.ClientResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@ConditionalOnProperty(name = "cache.enabled", havingValue = "true")
public class ClientServiceImplWithRedisCache extends ClientServiceImpl{

    //private static final String KEY = "clientresponse";
    public static String key;

    @Value("${KEY_REDIS_NAME:clientresponse}")
    public void setKey(String KEY_REDIS_NAME) {
        RedisConfig.key = KEY_REDIS_NAME;
    }

    @Autowired
    private ReactiveHashOperations<String, String, ClientResponseDTO> hashOperations;

    @Override
    public Mono<ClientResponseDTO> findByDocumentNumber(String documentNumber) {
        log.info("ClientServiceImplWithRedisCache: {}","findByDocumentNumber");
        return hashOperations.get(key, documentNumber)
                .switchIfEmpty(this.getFromDatabaseAndCache(documentNumber));
    }

    private Mono<ClientResponseDTO> getFromDatabaseAndCache(String documentNumber) {
        return super.findByDocumentNumber(documentNumber)
                .flatMap(dto -> {
                    log.info("getFromDatabaseAndCache: {}",dto.toString());
                    return this.hashOperations.put(key, documentNumber, dto).thenReturn(dto);
                });
    }

    @Override
    public Mono<ClientDTO> delete(Integer idClient) {
        return super.delete(idClient)
                .flatMap(dto -> {
                    return this.hashOperations.remove(key, dto.getDocumentNumber()).thenReturn(dto);
                });
    }

    @Override
    public Mono<ClientDTO> update(Integer idClient,ClientDTO clientDTO){
        return super.update(idClient, clientDTO)
                .flatMap(dto -> {
                    return this.hashOperations.remove(key, clientDTO.getDocumentNumber()).thenReturn(dto);
                });
    }

}
