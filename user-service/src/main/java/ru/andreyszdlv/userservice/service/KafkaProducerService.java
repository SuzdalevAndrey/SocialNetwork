package ru.andreyszdlv.userservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.andreyszdlv.userservice.dto.kafkaDto.EditEmailKafkaDTO;
import ru.andreyszdlv.userservice.dto.kafkaDto.EditPasswordKafkaDTO;
import ru.andreyszdlv.userservice.dto.kafkaDto.UserDetailsKafkaDTO;
import ru.andreyszdlv.userservice.security.enums.ERole;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {
    @Value("${spring.kafka.topic.name.edit-email}")
    private String nameTopicEditEmail;

    @Value("${spring.kafka.topic.name.edit-password}")
    private String nameTopicEditPassword;

    @Value("${spring.kafka.topic.name.failure-save-user}")
    private String nameTopicFailureSaveUser;

    private final KafkaTemplate<String, EditEmailKafkaDTO> kafkaTemplateEditEmail;

    private final KafkaTemplate<String, EditPasswordKafkaDTO> kafkaTemplateEditPassword;

    private final KafkaTemplate<String, UserDetailsKafkaDTO> kafkaTemplateFailureSave;

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

    public void sendFailureSaveUserEvent(String name,
                                         String email,
                                         String password,
                                         ERole role){
        kafkaTemplateFailureSave.send(
                nameTopicFailureSaveUser,
                UserDetailsKafkaDTO
                        .builder()
                        .name(name)
                        .email(email)
                        .password(password)
                        .role(role)
                        .build()
        );
    }

}
