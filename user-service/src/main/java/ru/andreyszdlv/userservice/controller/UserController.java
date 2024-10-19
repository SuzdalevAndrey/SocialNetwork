package ru.andreyszdlv.userservice.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.andreyszdlv.userservice.dto.controller.UpdateEmailRequestDTO;
import ru.andreyszdlv.userservice.dto.controller.UpdatePasswordRequestDTO;
import ru.andreyszdlv.userservice.service.LocalizationService;
import ru.andreyszdlv.userservice.service.UserService;

import java.util.Locale;

@Slf4j
@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    private final LocalizationService localizationService;

    @PatchMapping("/edit-email")
    public ResponseEntity<String> updateEmailUser(@Valid @RequestBody UpdateEmailRequestDTO updateEmailRequestDTO,
                                                  BindingResult bindingResult,
                                                  @RequestHeader("X-User-Email") String oldEmail,
                                                  Locale locale)
            throws BindException {

        log.info("Executing updateEmailUser: oldEmail = {}, newEmail = {}",
                oldEmail,
                updateEmailRequestDTO.email()
        );

        if (bindingResult.hasErrors()) {
            log.error("Validation errors during email update: {}",
                    bindingResult.getAllErrors());
            if (bindingResult instanceof BindException exception) {
                throw exception;
            }
            throw new BindException(bindingResult);
        }

        log.info("Validation successful, oldEmail = {}, newEmail = {}",
                oldEmail,
                updateEmailRequestDTO.email()
        );
        userService.updateEmailUser(oldEmail, updateEmailRequestDTO.email());

        log.info("Email update completed successfully");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        localizationService.getLocalizedMessage(
                            "message.ok.update_email",
                            locale
                        )
                );
    }


    @PatchMapping("/change-password")
    public ResponseEntity<String> updatePasswordUser(@Valid @RequestBody UpdatePasswordRequestDTO updatePasswordRequestDTO,
                                                     BindingResult bindingResult,
                                                     @RequestHeader("X-User-Email") String userEmail,
                                                     Locale locale)
            throws BindException{

        log.info("Executing updatePasswordUser for userEmail: {}", userEmail);

        if(bindingResult.hasErrors()) {
            log.error("Validation errors during password update: {}",
                    bindingResult.getAllErrors());
            if(bindingResult instanceof BindException exception)
                throw exception;
            throw new BindException(bindingResult);
        }

        log.info("Validation successful, updating password for userEmail: {}", userEmail);
        userService.updatePasswordUser(userEmail, updatePasswordRequestDTO.oldPassword(), updatePasswordRequestDTO.newPassword());

        log.info("Password update completed successfully");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        localizationService.getLocalizedMessage(
                                "message.ok.change_password",
                                locale
                        )
                );
    }
}
