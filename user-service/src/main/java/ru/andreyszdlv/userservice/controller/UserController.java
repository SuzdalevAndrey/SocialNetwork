package ru.andreyszdlv.userservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @GetMapping
    public ResponseEntity<String> getOk() {
        return ResponseEntity.ok("Пользователь с ролью User");
    }
}
