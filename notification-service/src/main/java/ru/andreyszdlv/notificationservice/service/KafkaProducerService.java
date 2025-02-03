package ru.andreyszdlv.notificationservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.andreyszdlv.notificationservice.dto.auth.FailureSendRegisterMailDTO;
import ru.andreyszdlv.notificationservice.props.KafkaProducerProperties;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaProducerProperties kafkaProducerProperties;

    private final KafkaTemplate<String, FailureSendRegisterMailDTO> kafkaTemplateMailFailure;

    public void sendFailureRegisterMailEvent(String email){
        log.info("Executing sendFailureRegisterMailEvent in kafka with email: {}", email);
        kafkaTemplateMailFailure.send(
                kafkaProducerProperties.getTopicNameFailureSendRegisterMail(),
                FailureSendRegisterMailDTO
                        .builder()
                        .messageId(UUID.randomUUID())
                        .email(email)
                        .build()
        );
    }
}
