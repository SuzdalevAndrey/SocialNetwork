package ru.andreyszdlv.userservice.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.bouncycastle.openssl.PasswordException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.andreyszdlv.userservice.controller.dto.UpdateEmailRequestDTO;
import ru.andreyszdlv.userservice.controller.dto.UpdatePasswordRequestDTO;
import ru.andreyszdlv.userservice.service.jwt.UserService;

import java.util.NoSuchElementException;


@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @PatchMapping("/editemail/{email}")
    public ResponseEntity<String> updateEmailUser(@PathVariable String email,
                                                  @Valid @RequestBody UpdateEmailRequestDTO updateEmailRequestDTO,
                                                  BindingResult bindingResult) throws NoSuchElementException, BindException {
        if(bindingResult.hasErrors()) {
            if(bindingResult instanceof BindException exception)
                throw exception;
            throw new BindException(bindingResult);
        }
        else{
            userService.updateEmailUser(email, updateEmailRequestDTO.email());
            return ResponseEntity.ok("Email успешно изменён");
        }
    }

    @PatchMapping("/editpassword/{email}")
    public ResponseEntity<String> updatePasswordUser(@PathVariable String email,
                                                     @Valid @RequestBody UpdatePasswordRequestDTO updatePasswordRequestDTO,
                                                     BindingResult bindingResult)
            throws NoSuchElementException,
            BindException,
            BadCredentialsException {
        if(bindingResult.hasErrors()) {
            if(bindingResult instanceof BindException exception)
                throw exception;
            throw new BindException(bindingResult);
        }
        else {
            userService.updatePasswordUser(email, updatePasswordRequestDTO.oldPassword(), updatePasswordRequestDTO.newPassword());
            return ResponseEntity.ok("Пароль успешно изменён");
        }
    }

}
