package ru.andreyszdlv.userservice.service.jwt;

import lombok.AllArgsConstructor;
import org.bouncycastle.openssl.PasswordException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.andreyszdlv.userservice.model.User;
import ru.andreyszdlv.userservice.repository.UserRepo;

import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepo userRepository;

    public UserDetailsService getDetailsService() {

        UserDetailsService detailsService = new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                User user = userRepository.findByEmail(username)
                        .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
                return user;
            }
        };

        return detailsService;
    }

    public void updateEmailUser(String currentEmail, String newEmail)
            throws NoSuchElementException{
        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(()->new NoSuchElementException("errors.404.usernotfound"));
        user.setEmail(newEmail);
        userRepository.save(user);
    }

    public void updatePasswordUser(String emailUser, String oldPassword, String newPassword)
            throws BadCredentialsException {
        User user = userRepository.findByEmail(emailUser).
                orElseThrow(()->new NoSuchElementException("errors.404.usernotfound"));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if(passwordEncoder.matches(oldPassword, user.getPassword())){
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        }
        else{
            throw new BadCredentialsException("errors.400.invalidpassword");
        }
    }
}
