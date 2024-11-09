package ru.andreyszdlv.authservice.service.impl;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ru.andreyszdlv.authservice.service.KafkaMessageIdService;

import java.util.UUID;

@Service
public class KafkaMessageIdCacheServiceImpl implements KafkaMessageIdService {

    @CachePut(value = "${spring.redis.kafkaMessageIdsNameCache}", key = "#messageId.toString()")
    public boolean saveMessageId(UUID messageId){
        return true;
    }

    @Cacheable(value = "${spring.redis.kafkaMessageIdsNameCache}", key = "#messageId.toString()")
    public boolean isMessageIdExists(UUID messageId){
        return false;
    }
}
