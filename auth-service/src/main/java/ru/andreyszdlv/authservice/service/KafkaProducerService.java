package ru.andreyszdlv.authservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.andreyszdlv.authservice.dto.kafka.UserDetailsKafkaDTO;
import ru.andreyszdlv.authservice.dto.kafka.LoginUserKafkaDTO;
import ru.andreyszdlv.authservice.dto.kafka.RegisterUserKafkaDTO;
import ru.andreyszdlv.authservice.enums.ERole;
import ru.andreyszdlv.authservice.props.KafkaProducerProperties;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaProducerProperties kafkaProducerProperties;

    private final KafkaTemplate<String, LoginUserKafkaDTO> kafkaTemplateLogin;

    private final KafkaTemplate<String, RegisterUserKafkaDTO> kafkaTemplateRegister;

    private final KafkaTemplate<String, UserDetailsKafkaDTO> kafkaTemplateSaveUser;

    public void sendRegisterEvent(String email, String code){
        log.info("Executing sendRegisterEvent in kafka with email: {} and verification code", email);
        kafkaTemplateRegister.send(
                kafkaProducerProperties.getTopicNameRegisterUser(),
                new RegisterUserKafkaDTO(email, code));
    }

    public void sendLoginEvent(String name, String email){
        log.info("Executing sendLoginEvent in kafka with name: {}, email: {}", name, email);
        kafkaTemplateLogin.send(
                kafkaProducerProperties.getTopicNameLoginUser(),
                new LoginUserKafkaDTO(name, email)
        );
    }

    public void sendSaveUserEvent(String name,
                                  String email,
                                  String password,
                                  ERole role){
        log.info("Executing sendSaveUserEvent in kafka with userName: {}, userEmail: {}",
                name,
                email
        );
        kafkaTemplateSaveUser.send(
                kafkaProducerProperties.getTopicNameSaveUser(),
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
