package ru.andreyszdlv.authservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.andreyszdlv.authservice.model.LoginUser;
import ru.andreyszdlv.authservice.model.RegisterUser;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {
    private final KafkaTemplate<String, LoginUser> kafkaTemplateLogin;

    private final KafkaTemplate<String, RegisterUser> kafkaTemplateRegister;

    public void sendRegisterEvent(String email, String code){
        kafkaTemplateRegister.send("auth-event-register",new RegisterUser(email, code));
    }

    public void sendLoginEvent(String name, String email){
        kafkaTemplateLogin.send("auth-event-login",new LoginUser(name, email));
    }
}
