package ru.andreyszdlv.userservice.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.andreyszdlv.userservice.dto.kafka.UserDetailsKafkaDTO;
import ru.andreyszdlv.userservice.props.KafkaConsumerProperties;
import ru.andreyszdlv.userservice.service.InternalUserService;
import ru.andreyszdlv.userservice.service.KafkaMessageIdCacheService;
import ru.andreyszdlv.userservice.service.UserService;

@Component
@RequiredArgsConstructor
@Slf4j
public class SaveUserEventListener {

    private final ObjectMapper mapper;

    private final InternalUserService internalUserService;

    private final KafkaMessageIdCacheService kafkaMessageIdCacheService;

    @KafkaListener(
            topics = "#{@kafkaConsumerProperties.topicNameSaveUser}",
            groupId = "#{@kafkaConsumerProperties.groupId}"
    )
    public void listen(String messageUser)
            throws JsonProcessingException {
        log.info("Executing save user event");

        log.info("Mapping String in UserDetailsKafkaDTO");
        UserDetailsKafkaDTO user = mapper.readValue(messageUser, UserDetailsKafkaDTO.class);

        log.info("Checking exist messageId in cache");
        if(!kafkaMessageIdCacheService.isMessageIdExists(user.messageId())) {

            log.info("Adding messageId in cache");
            kafkaMessageIdCacheService.saveMessageId(user.messageId());

            log.info("Saving user with email: {}", user.email());
            internalUserService.saveUser(
                    user.name(),
                    user.email(),
                    user.password(),
                    user.role()
            );
            return;
        }
        log.info("MessageId already exists in cache");
    }
}
