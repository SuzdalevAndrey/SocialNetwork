package ru.andreyszdlv.authservice.api.userservice;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.andreyszdlv.authservice.dto.userservicefeigndto.UserDetailsRequestDTO;
import ru.andreyszdlv.authservice.dto.userservicefeigndto.UserDetailsResponseDTO;
import ru.andreyszdlv.authservice.dto.userservicefeigndto.UserResponseDTO;

@FeignClient(name = "user-service", path = "/internal/user")
public interface UserServiceFeignClient {
    @PostMapping("/save")
    ResponseEntity<String> saveUser(UserDetailsRequestDTO user);

    @GetMapping("/exists/{email}")
    ResponseEntity<Boolean> existsUserByEmail(@PathVariable String email);

    @GetMapping("/{email}")
    ResponseEntity<UserResponseDTO> getUserByEmail(@PathVariable String email);

    @GetMapping("/user-details/{email}")
    ResponseEntity<UserDetailsResponseDTO> getUserDetailsByUserEmail(@PathVariable String email);
}
