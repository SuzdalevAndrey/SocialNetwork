package ru.andreyszdlv.authservice.service;

import java.util.UUID;

public interface KafkaMessageIdService {

    boolean saveMessageId(UUID messageId);

    boolean isMessageIdExists(UUID messageId);

}
