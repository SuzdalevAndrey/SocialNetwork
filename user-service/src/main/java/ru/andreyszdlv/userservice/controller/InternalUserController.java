package ru.andreyszdlv.userservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.andreyszdlv.userservice.dto.controller.UserDetailsResponseDTO;
import ru.andreyszdlv.userservice.dto.controller.UserResponseDTO;
import ru.andreyszdlv.userservice.service.UserService;

@RestController
@RequestMapping("/internal/user")
@RequiredArgsConstructor
@Slf4j
public class InternalUserController {

    private final UserService userService;

    @GetMapping("/id/{email}")
    public ResponseEntity<Long> getUserIdByUserEmail(@PathVariable String email){
        log.info("Executing getUserIdByUserEmail for email: {}", email);

        log.info("Getting user id by email: {}", email);
        Long userId = userService.getUserIdByEmail(email);

        log.info("Successfully get userId: {} by email: {}", userId, email);
        return ResponseEntity.ok(userId);
    }

    @GetMapping("/email/{userId}")
    public ResponseEntity<String> getUserEmailByUserId(@PathVariable long userId){
        log.info("Executing getUserEmailByUserId for userId: {}", userId);

        log.info("Getting email by id: {}", userId);
        String email = userService.getUserEmailByUserId(userId);

        log.info("Successfully get email: {} by userId: {}", email, userId);
        return ResponseEntity.ok(email);
    }

    @GetMapping("/name/{email}")
    public ResponseEntity<String> getNameByUserEmail(@PathVariable String email){
        log.info("Executing getNameByUserEmail for email: {}", email);

        log.info("Getting name by email: {}", email);
        String name = userService.getNameByUserEmail(email);

        log.info("Successfully get name: {} by email: {}", name, email);
        return ResponseEntity.ok(name);
    }

    @GetMapping("/user-details/{email}")
    public ResponseEntity<UserDetailsResponseDTO> getUserDetailsByUserEmail(@PathVariable String email){
        log.info("Executing getUserDetailsByUserEmail by email: {}", email);

        log.info("Getting user details by email: {}", email);
        UserDetailsResponseDTO response = userService.getUserDetailsByEmail(email);

        log.info("Successfully get user details by email: {}", email);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/exists/{email}")
    public ResponseEntity<Boolean> existsUserByEmail(@PathVariable String email){
        log.info("Executing existsUserByEmail by email: {}", email);

        log.info("Checking exists user by email: {}", email);
        boolean exists = userService.existsUserByEmail(email);

        log.info("Successfully exists user by email: {}", email);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/{email}")
    public ResponseEntity<UserResponseDTO> getUserByEmail(@PathVariable String email){
        log.info("Executing getUserByEmail for email: {}", email);

        log.info("Getting user by email: {}", email);
        UserResponseDTO userResponse = userService.getUserByUserEmail(email);

        log.info("Successfully get user by email: {}", email);
        return ResponseEntity.ok(userResponse);
    }
}
