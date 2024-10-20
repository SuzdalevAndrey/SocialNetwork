package ru.andreyszdlv.authservice.api.userservice;

import io.micrometer.core.annotation.Timed;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.andreyszdlv.authservice.dto.feignclient.UserDetailsResponseDTO;
import ru.andreyszdlv.authservice.dto.feignclient.UserResponseDTO;

@FeignClient(name = "user-service", path = "/internal/user")
public interface UserServiceFeignClient {

    @Timed("exists_user_by_email_time")
    @GetMapping("/exists/{email}")
    ResponseEntity<Boolean> existsUserByEmail(@PathVariable String email);

    @Timed("get_user_by_email_time")
    @GetMapping("/{email}")
    ResponseEntity<UserResponseDTO> getUserByEmail(@PathVariable String email);

    @Timed("get_user_details_by_user_email_time")
    @GetMapping("/user-details/{email}")
    ResponseEntity<UserDetailsResponseDTO> getUserDetailsByUserEmail(@PathVariable String email);
}
