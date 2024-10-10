package ru.andreyszdlv.authservice.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.andreyszdlv.authservice.dto.controllerdto.ConfirmEmailRequestDTO;
import ru.andreyszdlv.authservice.dto.controllerdto.LoginRequestDTO;
import ru.andreyszdlv.authservice.dto.controllerdto.LoginResponseDTO;
import ru.andreyszdlv.authservice.dto.controllerdto.RefreshTokenRequestDTO;
import ru.andreyszdlv.authservice.dto.controllerdto.RefreshTokenResponseDTO;
import ru.andreyszdlv.authservice.dto.controllerdto.RegisterRequestDTO;
import ru.andreyszdlv.authservice.dto.controllerdto.UpdateVerifiCodeRequestDTO;
import ru.andreyszdlv.authservice.service.AuthService;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequestDTO request,
                                                        BindingResult bindingResult)
            throws BindException {

        log.info("Executing register method in AuthController for email: {} and name: {}",
                request.email(),
                request.name());

        if(bindingResult.hasErrors()) {
            log.error("Validation errors occurred during register user: {}",
                    bindingResult.getAllErrors());

            if(bindingResult instanceof BindException exception)
                throw exception;
            throw new BindException(bindingResult);
        }
        else {
            log.info("Validation successful, register user with email: {} and name: {}",
                    request.email(),
                    request.name());

            authService.registerUser(request);

            log.info("User register completed successfully with  email: {} and name: {}",
                    request.email(),
                    request.name());
            return ResponseEntity.ok("Вам на почту было отправлено" +
                    " письмо с кодом для подтверждения email");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request,
                                                  BindingResult bindingResult)
            throws BindException {
        log.info("Executing login method in AuthController for email: {} ", request.email());

        if(bindingResult.hasErrors()) {
            log.error("Validation errors occurred during login user: {}",
                    bindingResult.getAllErrors());

            if(bindingResult instanceof BindException exception)
                throw exception;
            throw new BindException(bindingResult);
        }
        else {
            log.info("Validation successful, login user with email: {}", request.email());

            LoginResponseDTO response = authService.loginUser(request);

            log.info("User login completed successfully with  email: {}", request.email());

            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponseDTO> refresh(@Valid @RequestBody RefreshTokenRequestDTO request,
                                                           BindingResult bindingResult)
            throws BindException{
        log.info("Executing refresh method in AuthController");

        if(bindingResult.hasErrors()) {
            log.error("Validation errors occurred during refresh token: {}",
                    bindingResult.getAllErrors());

            if(bindingResult instanceof BindException exception)
                throw exception;
            throw new BindException(bindingResult);
        }
        else {
            log.info("Validation successful, refreshing token");

            RefreshTokenResponseDTO response = authService.refresh(request);

            log.info("Refresh token completed successfully");

            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/confirm")
    public ResponseEntity<String> confirmEmail(
            @Valid @RequestBody ConfirmEmailRequestDTO request,
            BindingResult bindingResult) throws BindException {

        log.info("Executing confirmEmail method in AuthController");
        if(bindingResult.hasErrors()){
            log.error("Validation errors occurred during confirm email: {}",
                    bindingResult.getAllErrors());

            if(bindingResult instanceof BindException exception)
                throw exception;
            throw new BindException(bindingResult);
        }
        else{
            log.info("Validation successful, confirm email");

            authService.confirmEmail(request);

            log.info("Confirm email completed successfully");

            return ResponseEntity.ok("Вы успешно подтвердили email," +
                    " теперь можете входить в систему!");
        }
    }

    @PatchMapping("/update-verification-code")
    public ResponseEntity<String> updatingVerificationCode(
            @Valid @RequestBody UpdateVerifiCodeRequestDTO request,
            BindingResult bindingResult)
            throws BindException {

        log.info("Executing updatingVerificationCode method in AuthController");

        if(bindingResult.hasErrors()){
            log.error("Validation errors occurred during update verification code: {}",
                    bindingResult.getAllErrors());

            if(bindingResult instanceof BindException exception)
                throw exception;
            throw new BindException(bindingResult);
        }
        else {
            log.info("Validation successful, update verification code");

            authService.updateVerificationCode(request.email());

            log.info("Confirm update verification code completed successfully");

            return ResponseEntity.ok("Код успешно обновлён, проверяйте почту!");
        }
    }

    @PostMapping("/generate-data-user")
    public ResponseEntity<Map<String, String>> generateDataUserUsingToken(
            @RequestHeader("Authorization") String token){
        log.info("Executing generateDataUserUsingToken with AuthController");
        Map<String, String> dataUser = authService.generateDataUserUsingToken(token);

        log.info("Confirm generate data user using token completed successfully");
        return ResponseEntity.ok(dataUser);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("X-User-Email") String email){
        authService.logout(email);
        return ResponseEntity.ok("Вы успешно вышли из системы");
    }
}
