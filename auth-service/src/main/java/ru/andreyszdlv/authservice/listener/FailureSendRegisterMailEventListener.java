package ru.andreyszdlv.authservice.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.andreyszdlv.authservice.dto.kafka.FailureSendRegisterMailKafkaDTO;
import ru.andreyszdlv.authservice.service.compensation.RegisterCompensationService;

@Component
@RequiredArgsConstructor
public class FailureSendRegisterMailEventListener {

    private final ObjectMapper mapper;

    private final RegisterCompensationService registerCompensationService;

    @KafkaListener(
            topics = "${spring.kafka.topic.name.failure-send-register-mail}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void listen(String message) throws JsonProcessingException {
        FailureSendRegisterMailKafkaDTO dto = mapper
                .readValue(message, FailureSendRegisterMailKafkaDTO.class);

        registerCompensationService.handle(dto.email());
    }
}
