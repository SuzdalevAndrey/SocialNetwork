package ru.andreyszdlv.authservice.client;

import io.micrometer.core.annotation.Timed;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.andreyszdlv.authservice.dto.client.UserDetailsResponseDTO;
import ru.andreyszdlv.authservice.dto.client.UserResponseDTO;

@FeignClient(name = "user-service", path = "/internal/user")
public interface UserServiceClient {

    @Timed("exists_user_by_email_time")
    @GetMapping("/{email}/exists")
    ResponseEntity<Boolean> existsUserByEmail(@PathVariable String email);

    @Timed("get_user_by_email_time")
    @GetMapping("/{email}")
    ResponseEntity<UserResponseDTO> getUserByEmail(@PathVariable String email);

    @Timed("get_user_details_by_user_email_time")
    @GetMapping("/{email}/user-details")
    ResponseEntity<UserDetailsResponseDTO> getUserDetailsByUserEmail(@PathVariable String email);
}
