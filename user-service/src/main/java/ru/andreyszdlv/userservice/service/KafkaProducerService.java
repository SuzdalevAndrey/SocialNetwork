package ru.andreyszdlv.userservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.andreyszdlv.userservice.dto.kafka.EditEmailKafkaDTO;
import ru.andreyszdlv.userservice.dto.kafka.EditPasswordKafkaDTO;
import ru.andreyszdlv.userservice.dto.kafka.UserDetailsKafkaDTO;
import ru.andreyszdlv.userservice.enums.ERole;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {
    @Value("${spring.kafka.producer.topic.name.edit-email}")
    private String nameTopicEditEmail;

    @Value("${spring.kafka.producer.topic.name.edit-password}")
    private String nameTopicEditPassword;

    @Value("${spring.kafka.producer.topic.name.failure-save-user}")
    private String nameTopicFailureSaveUser;

    private final KafkaTemplate<String, EditEmailKafkaDTO> kafkaTemplateEditEmail;

    private final KafkaTemplate<String, EditPasswordKafkaDTO> kafkaTemplateEditPassword;

    private final KafkaTemplate<String, UserDetailsKafkaDTO> kafkaTemplateFailureSave;

    public void sendEditEmailEvent(String oldEmail, String newEmail){
        log.info("Executing sendEditEmailEvent in kafka with oldEmail");
        log.info("nameTopicEditEmail: "+ nameTopicEditEmail);
        kafkaTemplateEditEmail.send(
                nameTopicEditEmail,
                new EditEmailKafkaDTO(oldEmail, newEmail)
        );
    }

    public void sendEditPasswordEvent(String email){
        log.info("Executing sendEditPasswordEvent in kafka");
        log.info("nameTopicEditPassword: "+ nameTopicEditPassword);
        kafkaTemplateEditPassword.send(
                nameTopicEditPassword,
                new EditPasswordKafkaDTO(email)
        );
    }

    public void sendFailureSaveUserEvent(String name,
                                         String email,
                                         String password,
                                         ERole role){
        log.info("Executing sendFailureSaveUserEvent in kafka with name, email: {}, password, role",
                email
        );
        log.info("nameTopicFailureSaveUser: "+ nameTopicFailureSaveUser);
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
