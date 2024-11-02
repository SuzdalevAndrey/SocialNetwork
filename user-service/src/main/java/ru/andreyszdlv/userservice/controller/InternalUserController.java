package ru.andreyszdlv.userservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.andreyszdlv.userservice.dto.controller.IdImageRequestDTO;
import ru.andreyszdlv.userservice.dto.controller.InternalUserResponseDTO;
import ru.andreyszdlv.userservice.dto.controller.UserDetailsResponseDTO;
import ru.andreyszdlv.userservice.dto.controller.UserResponseDTO;
import ru.andreyszdlv.userservice.service.InternalUserService;

@RestController
@RequestMapping("/internal/user")
@RequiredArgsConstructor
@Slf4j
public class InternalUserController {

    private final InternalUserService internalUserService;

    @GetMapping("/email/{userId}")
    public ResponseEntity<String> getUserEmailByUserId(@PathVariable long userId) {
        log.info("Executing getUserEmailByUserId for userId: {}", userId);

        log.info("Getting email by id: {}", userId);
        String email = internalUserService.getUserEmailByUserId(userId);

        log.info("Successfully get email: {} by userId: {}", email, userId);
        return ResponseEntity.ok(email);
    }

    @GetMapping("/name/{userId}")
    public ResponseEntity<String> getNameByUserId(@PathVariable long userId) {
        log.info("Executing getNameByUserEmail for userId: {}", userId);

        log.info("Getting name by userId: {}", userId);
        String name = internalUserService.getNameByUserId(userId);

        log.info("Successfully get name: {} by userId: {}", name, userId);
        return ResponseEntity.ok(name);
    }

    @GetMapping("/user-details/{email}")
    public ResponseEntity<UserDetailsResponseDTO> getUserDetailsByUserEmail(@PathVariable String email) {
        log.info("Executing getUserDetailsByUserEmail by email: {}", email);

        log.info("Getting user details by email: {}", email);
        UserDetailsResponseDTO response = internalUserService.getUserDetailsByEmail(email);

        log.info("Successfully get user details by email: {}", email);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/exists/{email}")
    public ResponseEntity<Boolean> existsUserByEmail(@PathVariable String email) {
        log.info("Executing existsUserByEmail by email: {}", email);

        log.info("Checking exists user by email: {}", email);
        boolean exists = internalUserService.existsUserByEmail(email);

        log.info("Successfully exists user by email: {}", email);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/{email}")
    public ResponseEntity<InternalUserResponseDTO> getUserByEmail(@PathVariable String email) {
        log.info("Executing getUserByEmail for email: {}", email);

        log.info("Getting user by email: {}", email);
        InternalUserResponseDTO userResponse = internalUserService.getUserByUserEmail(email);

        log.info("Successfully get user by email: {}", email);
        return ResponseEntity.ok(userResponse);
    }

    @PostMapping("/save-id-image/{userId}")
    public ResponseEntity<Void> saveUserIdImage(@PathVariable long userId,
                                                @RequestBody IdImageRequestDTO requestDTO) {

        internalUserService.saveIdImage(userId, requestDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @GetMapping("/{userId}/idImage")
    public ResponseEntity<String> getIdImageByUserId(@PathVariable long userId) {

        return ResponseEntity.ok(internalUserService.getIdImageByUserId(userId));
    }
}
