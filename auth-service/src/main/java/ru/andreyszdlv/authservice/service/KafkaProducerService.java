package ru.andreyszdlv.authservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.andreyszdlv.authservice.dto.kafkadto.UserDetailsKafkaDTO;
import ru.andreyszdlv.authservice.dto.kafkadto.LoginUserKafkaDTO;
import ru.andreyszdlv.authservice.dto.kafkadto.RegisterUserKafkaDTO;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    @Value("${spring.kafka.topic.name.login-user}")
    private String nameTopicLoginUser;

    @Value("${spring.kafka.topic.name.register-user}")
    private String nameTopicRegisterUser;

    @Value("${spring.kafka.topic.name.save-user}")
    private String nameTopicSaveUser;

    private final KafkaTemplate<String, LoginUserKafkaDTO> kafkaTemplateLogin;

    private final KafkaTemplate<String, RegisterUserKafkaDTO> kafkaTemplateRegister;

    private final KafkaTemplate<String, UserDetailsKafkaDTO> kafkaTemplateSaveUser;

    public void sendRegisterEvent(String email, String code){
        kafkaTemplateRegister.send(
                nameTopicRegisterUser,
                new RegisterUserKafkaDTO(email, code));
    }

    public void sendLoginEvent(String name, String email){
        kafkaTemplateLogin.send(
                nameTopicLoginUser,
                new LoginUserKafkaDTO(name, email)
        );
    }

    public void sendSaveUserEvent(UserDetailsKafkaDTO user){
        kafkaTemplateSaveUser.send(
                nameTopicSaveUser,
                user);
    }
}
