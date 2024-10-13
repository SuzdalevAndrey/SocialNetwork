package ru.andreyszdlv.authservice.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.andreyszdlv.authservice.dto.kafkadto.RegisterCompensationKafkaDTO;
import ru.andreyszdlv.authservice.service.RegisterCompensationService;

@Component
@RequiredArgsConstructor
public class RegisterCompensationEventListener {

    private final ObjectMapper mapper;

    private final RegisterCompensationService registerCompensationService;

    @KafkaListener(
            topics = "${spring.kafka.topic.name.TopicRegisterCompensation}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void listen(String message) throws JsonProcessingException {
        RegisterCompensationKafkaDTO registerCompensation = mapper
                .readValue(message, RegisterCompensationKafkaDTO.class);

        registerCompensationService.handle(registerCompensation.email());
    }
}
