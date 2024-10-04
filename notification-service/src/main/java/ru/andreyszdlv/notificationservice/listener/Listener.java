package ru.andreyszdlv.notificationservice.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import org.springframework.kafka.annotation.KafkaListener;
import ru.andreyszdlv.notificationservice.model.UserModel;

@Component
@Slf4j
public class Listener {

    @KafkaListener(topics = "auth-service", groupId = "notification-group")
    public void listen(UserModel userModel){
        switch (userModel.getAction()){
            case "REGISTER":
                log.info("REGISTER " + userModel.getName() + " " + userModel.getEmail());
                break;
            case "LOGIN":
                log.info("LOGIN "+ userModel.getName() + " " + userModel.getEmail());
                break;
        }
    }
}
