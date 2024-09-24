package ru.andreyszdlv.userservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @GetMapping
    public ResponseEntity<String> get(){
        return ResponseEntity.ok("Пользователь с ролью админ");
    }

}
