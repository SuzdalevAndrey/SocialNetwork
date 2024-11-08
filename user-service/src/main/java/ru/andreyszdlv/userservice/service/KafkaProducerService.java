package ru.andreyszdlv.userservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.andreyszdlv.userservice.dto.kafka.EditEmailKafkaDTO;
import ru.andreyszdlv.userservice.dto.kafka.EditPasswordKafkaDTO;
import ru.andreyszdlv.userservice.dto.kafka.UserDetailsKafkaDTO;
import ru.andreyszdlv.userservice.enums.ERole;
import ru.andreyszdlv.userservice.props.KafkaProducerProperties;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaProducerProperties producerProperties;

    private final KafkaTemplate<String, EditEmailKafkaDTO> kafkaTemplateEditEmail;

    private final KafkaTemplate<String, EditPasswordKafkaDTO> kafkaTemplateEditPassword;

    private final KafkaTemplate<String, UserDetailsKafkaDTO> kafkaTemplateFailureSaveUser;

    public void sendEditEmailEvent(String oldEmail, String newEmail){
        log.info("Executing sendEditEmailEvent in kafka with oldEmail");
        kafkaTemplateEditEmail.send(
                producerProperties.getTopicNameEditEmail(),
                new EditEmailKafkaDTO(oldEmail, newEmail)
        );
    }

    public void sendEditPasswordEvent(String email){
        log.info("Executing sendEditPasswordEvent in kafka");
        kafkaTemplateEditPassword.send(
                producerProperties.getTopicNameEditPassword(),
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
        kafkaTemplateFailureSaveUser.send(
                producerProperties.getTopicNameFailureSaveUser(),
                UserDetailsKafkaDTO
                        .builder()
                        .messageId(UUID.randomUUID())
                        .name(name)
                        .email(email)
                        .password(password)
                        .role(role)
                        .build()
        );
    }
}
