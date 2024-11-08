package ru.andreyszdlv.authservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.andreyszdlv.authservice.exception.ValidateTokenException;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDataService {

    private final AccessAndRefreshJwtService accessAndRefreshJwtService;

    private final JwtSecurityService jwtSecurityService;

    public Map<String, String> generateDataUserUsingToken(String token) {
        log.info("Executing generateDataUserUsingToken in AuthService");

        long userId = jwtSecurityService.extractUserId(token);
        log.info("Extract userId: {}", userId);

        String expectedToken = accessAndRefreshJwtService.getAccessTokenByUserId(userId);

        log.info("Validate token");
        if(jwtSecurityService.validateToken(token)
                && expectedToken!= null
                && expectedToken.equals(token)
        ){
            log.info("Token is valid");

            log.info("Generate data user using token");

            HashMap<String, String> dataUser = new HashMap<>(2);

            dataUser.put("userId", String.valueOf(userId));

            String role = jwtSecurityService.extractRole(token);
            log.info("Extract role: {}", role);

            dataUser.put("role", role);

            return dataUser;
        }
        throw new ValidateTokenException("errors.409.is_not_valid_token");
    }
}
