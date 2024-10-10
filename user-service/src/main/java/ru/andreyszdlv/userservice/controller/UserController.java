package ru.andreyszdlv.userservice.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.andreyszdlv.userservice.dto.controllerDto.SaveUserRequestDTO;
import ru.andreyszdlv.userservice.dto.controllerDto.UpdateEmailRequestDTO;
import ru.andreyszdlv.userservice.dto.controllerDto.UpdatePasswordRequestDTO;
import ru.andreyszdlv.userservice.dto.controllerDto.UserDetailsResponseDTO;
import ru.andreyszdlv.userservice.dto.controllerDto.UserResponseDTO;
import ru.andreyszdlv.userservice.model.User;
import ru.andreyszdlv.userservice.service.UserService;

import java.util.NoSuchElementException;

@Slf4j
@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @PatchMapping("/editemail")
    public ResponseEntity<String> updateEmailUser(@Valid @RequestBody UpdateEmailRequestDTO updateEmailRequestDTO,
                                                  BindingResult bindingResult,
                                                  @RequestHeader("X-User-Email") String oldEmail)
            throws NoSuchElementException, BindException {

        log.info("Executing updateEmailUser method for email update request");

        if (bindingResult.hasErrors()) {
            log.error("Validation errors occurred during email update: {}",
                    bindingResult.getAllErrors());

            if (bindingResult instanceof BindException exception) {
                throw exception;
            }

            throw new BindException(bindingResult);
        }

        log.info("Validation successful, updating email for user");
        userService.updateEmailUser(oldEmail, updateEmailRequestDTO.email());

        log.info("Email update completed successfully");
        return ResponseEntity.ok("Email успешно изменён");
    }


    @PatchMapping("/editpassword")
    public ResponseEntity<String> updatePasswordUser(@Valid @RequestBody UpdatePasswordRequestDTO updatePasswordRequestDTO,
                                                     BindingResult bindingResult,
                                                     @RequestHeader("X-User-Email") String userEmail)
            throws NoSuchElementException,
            BindException,
            BadCredentialsException {

        log.info("Executing updatePasswordUser method for password update request");

        if(bindingResult.hasErrors()) {

            log.error("Validation errors occurred during password update: {}",
                    bindingResult.getAllErrors());

            if(bindingResult instanceof BindException exception)
                throw exception;
            throw new BindException(bindingResult);
        }

        log.info("Validation successful, updating password for user");
        userService.updatePasswordUser(userEmail, updatePasswordRequestDTO.oldPassword(), updatePasswordRequestDTO.newPassword());

        log.info("Password update completed successfully");
        return ResponseEntity.ok("Пароль успешно изменён");
    }
}
