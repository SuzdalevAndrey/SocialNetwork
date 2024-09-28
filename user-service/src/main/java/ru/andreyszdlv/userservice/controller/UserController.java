package ru.andreyszdlv.userservice.controller;

import lombok.AllArgsConstructor;
import org.bouncycastle.openssl.PasswordException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.andreyszdlv.userservice.controller.dto.UpdateEmailRequestDTO;
import ru.andreyszdlv.userservice.controller.dto.UpdatePasswordRequestDTO;
import ru.andreyszdlv.userservice.service.jwt.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @PatchMapping("/editemail/{email}")
    public ResponseEntity<String> updateEmailUser(@PathVariable String email, @RequestBody UpdateEmailRequestDTO updateEmailRequestDTO) {
        userService.updateEmailUser(email, updateEmailRequestDTO.email());
        return ResponseEntity.ok("Email успешно изменён");
    }

    @PatchMapping("/editpassword/{email}")
    public ResponseEntity<String> updatePasswordUser(@PathVariable String email, @RequestBody UpdatePasswordRequestDTO updatePasswordRequestDTO) throws PasswordException {
        userService.updatePasswordUser(email, updatePasswordRequestDTO.oldPassword(),updatePasswordRequestDTO.newPassword());
        return ResponseEntity.ok("Пароль успешно изменён");
    }

}
