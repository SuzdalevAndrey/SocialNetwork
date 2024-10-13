package ru.andreyszdlv.notificationservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.andreyszdlv.notificationservice.dto.auth.RegisterCompensationDTO;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {
    private final KafkaTemplate<String, RegisterCompensationDTO> kafkaTemplateMailFailure;

    public void sendRegisterCompensation(String email){
        kafkaTemplateMailFailure.send(
                "${spring.kafka.topic.name.TopicRegisterCompensation}",
                new RegisterCompensationDTO(email)
        );
    }
}
