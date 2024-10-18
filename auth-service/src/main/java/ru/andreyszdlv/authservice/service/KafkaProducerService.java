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

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    @Value("${spring.kafka.producer.topic.name.login-user}")
    private String nameTopicLoginUser;

    @Value("${spring.kafka.producer.topic.name.register-user}")
    private String nameTopicRegisterUser;

    @Value("${spring.kafka.producer.topic.name.save-user}")
    private String nameTopicSaveUser;

    private final KafkaTemplate<String, LoginUserKafkaDTO> kafkaTemplateLogin;

    private final KafkaTemplate<String, RegisterUserKafkaDTO> kafkaTemplateRegister;

    private final KafkaTemplate<String, UserDetailsKafkaDTO> kafkaTemplateSaveUser;

    public void sendRegisterEvent(String email, String code){
        log.info("Executing sendRegisterEvent in kafka with email: {} and verification code", email);
        kafkaTemplateRegister.send(
                nameTopicRegisterUser,
                new RegisterUserKafkaDTO(email, code));
    }

    public void sendLoginEvent(String name, String email){
        log.info("Executing sendLoginEvent in kafka with name: {}, email: {}", name, email);
        kafkaTemplateLogin.send(
                nameTopicLoginUser,
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
                nameTopicSaveUser,
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
