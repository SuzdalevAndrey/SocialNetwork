package ru.andreyszdlv.userservice.service;

import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.andreyszdlv.userservice.controller.dto.LoginRequestDTO;
import ru.andreyszdlv.userservice.controller.dto.LoginResponseDTO;
import ru.andreyszdlv.userservice.controller.dto.RefreshTokenRequestDTO;
import ru.andreyszdlv.userservice.controller.dto.RefreshTokenResponseDTO;
import ru.andreyszdlv.userservice.controller.dto.RegisterRequestDTO;
import ru.andreyszdlv.userservice.enums.ERole;
import ru.andreyszdlv.userservice.model.User;
import ru.andreyszdlv.userservice.repository.UserRepo;
import ru.andreyszdlv.userservice.service.jwt.JwtSecurityService;

import java.util.HashMap;

@Service
@AllArgsConstructor
public class AuthService {
    private final UserRepo userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtSecurityService jwtSecurityService;

    private final AuthenticationManager authenticationManager;

    public User registerUser(RegisterRequestDTO request) {
        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(ERole.USER);
        return userRepository.save(user);
    }

    public LoginResponseDTO loginUser(LoginRequestDTO request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                ));
        User user = userRepository.findByEmail(request.email()).orElseThrow(()->new UsernameNotFoundException("Пользователь не найден!"));
        String token = jwtSecurityService.generateToken(user);
        String refreshToken = jwtSecurityService.generateRefreshToken(new HashMap<>(), user);

        return new LoginResponseDTO(user.getEmail(), token, refreshToken);
    }

    public RefreshTokenResponseDTO refresh(RefreshTokenRequestDTO request) {
        String jwt = request.refreshToken();
        String email = jwtSecurityService.extractUsername(jwt);

        User user = userRepository.findByEmail(email).orElseThrow();

        if(jwtSecurityService.validateToken(jwt, user)) {
            return new RefreshTokenResponseDTO(
                    jwtSecurityService.generateToken(user),
                    jwtSecurityService.generateRefreshToken(new HashMap<>(), user)
            );
        }
        return null;
    }

}
