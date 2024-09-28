package ru.andreyszdlv.userservice.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.andreyszdlv.userservice.controller.dto.LoginRequestDTO;
import ru.andreyszdlv.userservice.controller.dto.LoginResponseDTO;
import ru.andreyszdlv.userservice.controller.dto.RefreshTokenRequestDTO;
import ru.andreyszdlv.userservice.controller.dto.RefreshTokenResponseDTO;
import ru.andreyszdlv.userservice.controller.dto.RegisterRequestDTO;
import ru.andreyszdlv.userservice.controller.dto.RegisterResponseDTO;
import ru.andreyszdlv.userservice.model.User;
import ru.andreyszdlv.userservice.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(@RequestBody RegisterRequestDTO request) {
        User user = authService.registerUser(request);
        return ResponseEntity.ok(new RegisterResponseDTO(user.getName(), user.getEmail()));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.loginUser(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponseDTO> refresh(@RequestBody RefreshTokenRequestDTO request) {
        return ResponseEntity.ok(authService.refresh(request));
    }
}
