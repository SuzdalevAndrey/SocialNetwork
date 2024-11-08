package ru.andreyszdlv.authservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.andreyszdlv.authservice.service.AuthService;
import ru.andreyszdlv.authservice.service.UserDataService;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class UserDataController {

    private final UserDataService userDataService;

    @PostMapping("/generate-data-user")
    public ResponseEntity<Map<String, String>> generateDataUserUsingToken(
            @RequestHeader("Authorization") String token){
        log.info("Executing generateDataUserUsingToken with AuthController");
        Map<String, String> dataUser = userDataService.generateDataUserUsingToken(token);

        log.info("Confirm generate data user using token successfully");
        return ResponseEntity.ok(dataUser);
    }
}
