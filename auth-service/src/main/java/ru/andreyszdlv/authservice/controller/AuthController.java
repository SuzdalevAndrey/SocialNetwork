package ru.andreyszdlv.authservice.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.andreyszdlv.authservice.dto.controllerDto.ConfirmEmailRequestDTO;
import ru.andreyszdlv.authservice.dto.controllerDto.LoginRequestDTO;
import ru.andreyszdlv.authservice.dto.controllerDto.LoginResponseDTO;
import ru.andreyszdlv.authservice.dto.controllerDto.RefreshTokenRequestDTO;
import ru.andreyszdlv.authservice.dto.controllerDto.RefreshTokenResponseDTO;
import ru.andreyszdlv.authservice.dto.controllerDto.RegisterRequestDTO;
import ru.andreyszdlv.authservice.service.AuthService;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequestDTO request,
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
            return ResponseEntity.ok().build();
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

    @PatchMapping("/verification-codes/{userEmail}")
    public ResponseEntity<String> UpdatingVerificationCode(@PathVariable String userEmail){
        authService.updateVerificationCode(userEmail);
        return ResponseEntity.ok("Код успешно обновлён, проверяйте почту!");
    }
}
