package ru.andreyszdlv.userservice.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andreyszdlv.userservice.exception.NoSuchUserException;
import ru.andreyszdlv.userservice.model.User;
import ru.andreyszdlv.userservice.repository.UserRepo;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {

    private final UserRepo userRepository;

    @Transactional(readOnly = true)
    public User getUserByIdOrThrow(long id){
        log.info("Executing getUserById");

        log.info("Getting user by id: {}", id);
        User user = userRepository
                .findById(id)
                .orElseThrow(
                        ()->new NoSuchUserException("errors.404.user_not_found")
                );
        return user;
    }

    @Transactional
    public User getUserByEmaildOrThrow(String email){
        log.info("Executing getUserByEmail");

        log.info("Getting user by email: {}", email);
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(
                        ()->new NoSuchUserException("errors.404.user_not_found")
                );
        return user;
    }

    @Transactional
    public void save(User user) {
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
