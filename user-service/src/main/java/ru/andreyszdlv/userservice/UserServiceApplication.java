package ru.andreyszdlv.userservice;

import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.andreyszdlv.userservice.enums.ERole;
import ru.andreyszdlv.userservice.model.User;
import ru.andreyszdlv.userservice.repository.UserRepo;

@SpringBootApplication
@EnableDiscoveryClient
public class UserServiceApplication{
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
