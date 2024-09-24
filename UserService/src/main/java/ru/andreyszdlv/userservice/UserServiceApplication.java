package ru.andreyszdlv.userservice;

import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.andreyszdlv.userservice.enums.ERole;
import ru.andreyszdlv.userservice.model.User;
import ru.andreyszdlv.userservice.repository.UserRepo;

@AllArgsConstructor
@SpringBootApplication
public class UserServiceApplication implements CommandLineRunner {

    private final UserRepo userRepository;

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

    @Override
    public void run(String... args) {
        createUserIfNotExists("Андрей", "admin@admin.admin", ERole.ADMIN);
        createUserIfNotExists("Вася","user@user.user", ERole.USER);
    }

    private void createUserIfNotExists(String name, String email, ERole role) {
        if (userRepository.findByEmail(email).isEmpty()) {
            User account = new User();
            account.setName(name);
            account.setEmail(email);
            account.setPassword(new BCryptPasswordEncoder().encode("pass"));
            account.setRole(role);
            userRepository.save(account);
        }
    }
}
