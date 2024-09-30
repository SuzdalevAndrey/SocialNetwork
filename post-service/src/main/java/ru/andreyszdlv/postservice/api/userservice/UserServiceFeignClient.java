package ru.andreyszdlv.postservice.api.userservice;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
@Component
public interface UserServiceFeignClient {
    @GetMapping("/api/user")
    ResponseEntity<Long> getUserIdByUserEmail();
}
