package ru.andreyszdlv.userservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.andreyszdlv.userservice.dto.controllerDto.SaveUserRequestDTO;
import ru.andreyszdlv.userservice.dto.controllerDto.UserDetailsResponseDTO;
import ru.andreyszdlv.userservice.dto.controllerDto.UserResponseDTO;
import ru.andreyszdlv.userservice.service.UserService;

@RestController
@RequestMapping("/internal/user")
@RequiredArgsConstructor
@Slf4j
public class InternalUserController {

    private final UserService userService;

    @GetMapping("/id/{email}")
    public ResponseEntity<Long> getUserIdByUserEmail(@PathVariable String email){
        log.info("Executing getUserIdByUserEmail method for getting a userId by email");

        Long userId = userService.getUserIdByEmail(email);

        log.info("Successfully retrieved userId: {} by email", userId);
        return ResponseEntity.ok(userId);
    }

    @GetMapping("/email/{userId}")
    public ResponseEntity<String> getUserEmailByUserId(@PathVariable long userId){
        log.info("Executing getUserEmailByUserId method for getting a userEmail by userId");

        String email = userService.getUserEmailByUserId(userId);

        log.info("Successfully retrieved email: {} by userId: {}", email, userId);
        return ResponseEntity.ok(email);
    }

    @GetMapping("/name/{email}")
    public ResponseEntity<String> getNameByUserEmail(@PathVariable String email){
        log.info("Executing getNameByUserEmail method for getting a name by userEmail");

        String name = userService.getNameByUserEmail(email);

        log.info("Successfully retrieved name: {} by userEmail", name);
        return ResponseEntity.ok(name);
    }

    @GetMapping("/user-details/{email}")
    public ResponseEntity<UserDetailsResponseDTO> getUserDetailsByUserEmail(@PathVariable String email){
        log.info("getUserDetailsByUserEmail: {}", email);
        return ResponseEntity.ok(userService.getUserDetailsByEmail(email));
    }

    @PostMapping("/save")
    public ResponseEntity<String> saveUser(@RequestBody SaveUserRequestDTO user){
        userService.saveUser(
                user.name(),
                user.email(),
                user.password(),
                user.role()
        );
        return ResponseEntity.ok("Пользователь успешно сохранён");
    }

    @GetMapping("/exists/{email}")
    public ResponseEntity<Boolean> existsUserByEmail(@PathVariable String email){
        return ResponseEntity.ok(userService.existsUserByEmail(email));
    }

    @GetMapping("/{email}")
    public ResponseEntity<UserResponseDTO> getUserByEmail(@PathVariable String email){
        return ResponseEntity.ok(userService.getUserByUserEmail(email));
    }
}
