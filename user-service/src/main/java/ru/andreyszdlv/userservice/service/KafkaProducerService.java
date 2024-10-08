package ru.andreyszdlv.userservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.andreyszdlv.userservice.dto.kafkaDto.EditEmailKafkaDTO;
import ru.andreyszdlv.userservice.dto.kafkaDto.EditPasswordKafkaDTO;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {
    @Value("${spring.kafka.topic.nameTopicEditEmail}")
    private String nameTopicEditEmail;

    @Value("${spring.kafka.topic.nameTopicEditPassword}")
    private String nameTopicEditPassword;

    private final KafkaTemplate<String, EditEmailKafkaDTO> kafkaTemplateEditEmail;

    private final KafkaTemplate<String, EditPasswordKafkaDTO> kafkaTemplateEditPassword;

    public void sendEditEmailEvent(String oldEmail, String newEmail){
        kafkaTemplateEditEmail.send(
                nameTopicEditEmail,
                new EditEmailKafkaDTO(oldEmail, newEmail)
        );
    }

    public void sendEditPasswordEvent(String email){
        kafkaTemplateEditPassword.send(
                nameTopicEditPassword,
                new EditPasswordKafkaDTO(email)
        );
    }

}
