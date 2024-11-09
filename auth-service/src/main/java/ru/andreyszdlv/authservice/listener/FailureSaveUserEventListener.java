package ru.andreyszdlv.authservice.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.andreyszdlv.authservice.dto.kafka.UserDetailsKafkaDTO;
import ru.andreyszdlv.authservice.service.KafkaMessageIdService;
import ru.andreyszdlv.authservice.service.compensation.ConfirmEmailCompensationService;

@Component
@RequiredArgsConstructor
@Slf4j
public class FailureSaveUserEventListener {

    private final ConfirmEmailCompensationService compensationService;

    private final ObjectMapper mapper;

    private final KafkaMessageIdService KafkaMessageIdService;

    @KafkaListener(
            topics = "#{@kafkaConsumerProperties.topicNameFailureSaveUser}",
            groupId = "#{@kafkaConsumerProperties.groupId}"
    )
    public void listen(String messageUser) throws JsonProcessingException {
        log.info("Executing failure save user event");

        log.info("Mapping String in UserDetailsKafkaDTO");
        UserDetailsKafkaDTO user = mapper
                .readValue(messageUser, UserDetailsKafkaDTO.class);

        log.info("Checking exist messageId in cache");
        if(!KafkaMessageIdService.isMessageIdExists(user.messageId())){

            log.info("Adding messageId in cache");
            KafkaMessageIdService.saveMessageId(user.messageId());

            log.info("Execution compensation action for email: {}", user.email());
            compensationService.handle(user.name(), user.email(), user.password(), user.role());

            return;
        }
        log.info("MessageId already exists in cache");
    }
}
