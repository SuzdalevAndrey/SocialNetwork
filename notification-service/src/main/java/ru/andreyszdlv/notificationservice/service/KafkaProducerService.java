package ru.andreyszdlv.notificationservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.andreyszdlv.notificationservice.dto.auth.FailureSendRegisterMailDTO;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {
    @Value("${spring.kafka.producer.topic.name.failure-send-register-mail}")
    private String nameTopicFailureSendRegisterMail;

    private final KafkaTemplate<String, FailureSendRegisterMailDTO> kafkaTemplateMailFailure;

    public void sendFailureRegisterMailEvent(String email){
        log.info("Executing sendFailureRegisterMailEvent in kafka with email: {}", email);
        kafkaTemplateMailFailure.send(
                nameTopicFailureSendRegisterMail,
                new FailureSendRegisterMailDTO(email)
        );
    }
}
