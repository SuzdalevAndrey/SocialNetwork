package ru.andreyszdlv.authservice.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.andreyszdlv.authservice.dto.kafka.FailureSendRegisterMailKafkaDTO;
import ru.andreyszdlv.authservice.service.KafkaMessageIdCacheService;
import ru.andreyszdlv.authservice.service.compensation.RegisterCompensationService;

@Component
@RequiredArgsConstructor
@Slf4j
public class FailureSendRegisterMailEventListener {

    private final ObjectMapper mapper;

    private final RegisterCompensationService registerCompensationService;

    private final KafkaMessageIdCacheService kafkaMessageIdCacheService;

    @KafkaListener(
            topics = "#{@kafkaConsumerProperties.topicNameFailureSendRegisterMail}",
            groupId = "#{@kafkaConsumerProperties.groupId}"
    )
    public void listen(String message) throws JsonProcessingException {
        log.info("Executing failure send register mail event");

        log.info("Mapping String in FailureSendRegisterMailKafkaDTO");
        FailureSendRegisterMailKafkaDTO dto = mapper
                .readValue(message, FailureSendRegisterMailKafkaDTO.class);

        log.info("Checking exist messageId in cache");
        if(!kafkaMessageIdCacheService.isMessageIdExists(dto.messageId())){

            log.info("Adding messageId in cache");
            kafkaMessageIdCacheService.saveMessageId(dto.messageId());

            log.info("Execution compensation action for email: {}", dto.email());
            registerCompensationService.handle(dto.email());

            return;
        }
        log.info("MessageId already exists in cache");
    }
}
