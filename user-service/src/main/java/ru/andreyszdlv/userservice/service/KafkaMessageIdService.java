package ru.andreyszdlv.userservice.service;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

import java.util.UUID;

public interface KafkaMessageIdService {

    boolean saveMessageId(UUID messageId);

    boolean isMessageIdExists(UUID messageId);

}
