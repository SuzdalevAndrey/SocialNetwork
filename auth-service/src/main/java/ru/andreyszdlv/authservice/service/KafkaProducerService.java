package ru.andreyszdlv.authservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.andreyszdlv.authservice.dto.kafkadto.LoginUserKafkaDTO;
import ru.andreyszdlv.authservice.dto.kafkadto.RegisterUserKafkaDTO;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {
    private final KafkaTemplate<String, LoginUserKafkaDTO> kafkaTemplateLogin;

    private final KafkaTemplate<String, RegisterUserKafkaDTO> kafkaTemplateRegister;

    public void sendRegisterEvent(String email, String code){
        kafkaTemplateRegister.send("auth-event-register", new RegisterUserKafkaDTO(email, code));
    }

    public void sendLoginEvent(String name, String email){
        kafkaTemplateLogin.send("auth-event-login", new LoginUserKafkaDTO(name, email));
    }
}
