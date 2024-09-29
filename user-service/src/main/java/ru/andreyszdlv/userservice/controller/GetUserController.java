package ru.andreyszdlv.userservice.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.andreyszdlv.userservice.service.jwt.UserService;

@RestController
@RequestMapping("/api/getuser")
@AllArgsConstructor
public class GetUserController {
    private final UserService userService;

    @GetMapping("/{email}")
    public ResponseEntity<Long> getUserIdByUserEmail(@PathVariable String email){
        return ResponseEntity.ok(userService.getUserIdByEmail(email));
    }
}
