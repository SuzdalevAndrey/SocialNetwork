package ru.andreyszdlv.authservice.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.andreyszdlv.authservice.controller.dto.LoginRequestDTO;
import ru.andreyszdlv.authservice.controller.dto.LoginResponseDTO;
import ru.andreyszdlv.authservice.controller.dto.RefreshTokenRequestDTO;
import ru.andreyszdlv.authservice.controller.dto.RefreshTokenResponseDTO;
import ru.andreyszdlv.authservice.controller.dto.RegisterRequestDTO;
import ru.andreyszdlv.authservice.controller.dto.RegisterResponseDTO;
import ru.andreyszdlv.authservice.model.User;
import ru.andreyszdlv.authservice.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request,
                                                        BindingResult bindingResult)
            throws BindException {
        if(bindingResult.hasErrors()) {
            if(bindingResult instanceof BindException exception)
                throw exception;
            throw new BindException(bindingResult);
        }
        else {
            User user = authService.registerUser(request);
            return ResponseEntity.ok(new RegisterResponseDTO(user.getName(), user.getEmail()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request,
                                                  BindingResult bindingResult) throws BindException {
        if(bindingResult.hasErrors()) {
            if(bindingResult instanceof BindException exception)
                throw exception;
            throw new BindException(bindingResult);
        }
        else {
            return ResponseEntity.ok(authService.loginUser(request));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponseDTO> refresh(@Valid @RequestBody RefreshTokenRequestDTO request,
                                                           BindingResult bindingResult) throws BindException{
        if(bindingResult.hasErrors()) {
            if(bindingResult instanceof BindException exception)
                throw exception;
            throw new BindException(bindingResult);
        }
        else
            return ResponseEntity.ok(authService.refresh(request));
    }
}
