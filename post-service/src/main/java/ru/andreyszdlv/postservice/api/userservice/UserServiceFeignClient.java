package ru.andreyszdlv.postservice.api.userservice;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", path = "/internal/user")
public interface UserServiceFeignClient {
    @GetMapping("/id/{email}")
    ResponseEntity<Long> getUserIdByUserEmail(@PathVariable String email);

    @GetMapping("/email/{userId}")
    ResponseEntity<String> getUserEmailByUserId(@PathVariable long userId);

    @GetMapping("/name/{email}")
    ResponseEntity<String> getNameByUserEmail(@PathVariable String email);
}
