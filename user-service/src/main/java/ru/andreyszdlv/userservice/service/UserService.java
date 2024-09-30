package ru.andreyszdlv.userservice.service;

import lombok.AllArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.andreyszdlv.userservice.model.User;
import ru.andreyszdlv.userservice.repository.UserRepo;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class UserService {
    private String getEmailAuthenticationUser() throws UsernameNotFoundException{
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            if (authentication.getPrincipal() instanceof UserDetails) {
                return ((UserDetails) authentication.getPrincipal()).getUsername();
            }
        }
        throw new UsernameNotFoundException("errors.404.user_not_found");
    }

    private final UserRepo userRepository;

    public void updateEmailUser(String newEmail)
            throws NoSuchElementException{
        User user = userRepository.findByEmail(getEmailAuthenticationUser())
                .orElseThrow(()->new NoSuchElementException("errors.404.user_not_found"));
        user.setEmail(newEmail);
        userRepository.save(user);
    }

    public void updatePasswordUser(String oldPassword, String newPassword)
            throws BadCredentialsException {
        User user = userRepository.findByEmail(getEmailAuthenticationUser()).
                orElseThrow(()->new NoSuchElementException("errors.404.user_not_found"));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if(passwordEncoder.matches(oldPassword, user.getPassword())){
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        }
        else{
            throw new BadCredentialsException("errors.400.invalid_password");
        }
    }

    public Long getUserIdByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(()->new NoSuchElementException("errors.404.user_not_found")).getId();
    }
}
