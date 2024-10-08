package ru.andreyszdlv.authservice.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.andreyszdlv.authservice.enums.ERole;
import ru.andreyszdlv.authservice.model.PendingUser;
import ru.andreyszdlv.authservice.model.User;
import ru.andreyszdlv.authservice.repository.PendingUserRepo;
import ru.andreyszdlv.authservice.repository.UserRepo;

import java.time.LocalDateTime;


@Service
@AllArgsConstructor
public class UserService {

    private final UserRepo userRepository;

    public UserDetailsService getDetailsService() {

        UserDetailsService detailsService = new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username)
                    throws UsernameNotFoundException {
                User user = userRepository.findByEmail(username)
                        .orElseThrow(() -> new UsernameNotFoundException("errors.404.user_not_found"));
                return user;
            }
        };

        return detailsService;
    }

}
