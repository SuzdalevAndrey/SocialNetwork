package ru.andreyszdlv.authservice.api.userservice;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.andreyszdlv.authservice.dto.feignclient.UserDetailsResponseDTO;
import ru.andreyszdlv.authservice.dto.feignclient.UserResponseDTO;

@FeignClient(name = "user-service", path = "/internal/user")
public interface UserServiceFeignClient {

    @GetMapping("/exists/{email}")
    ResponseEntity<Boolean> existsUserByEmail(@PathVariable String email);

    @GetMapping("/{email}")
    ResponseEntity<UserResponseDTO> getUserByEmail(@PathVariable String email);

    @GetMapping("/user-details/{email}")
    ResponseEntity<UserDetailsResponseDTO> getUserDetailsByUserEmail(@PathVariable String email);
}
