package ru.andreyszdlv.imageservice.api.userservice;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", path = "/internal/user")
public interface UserServiceFeignClient {

    @GetMapping("/{userId}/idImage")
    ResponseEntity<String> getIdImageByUserId(@PathVariable long userId);

    @DeleteMapping("/{userId}/imageId")
    ResponseEntity<String> deleteImageIdByUserId(@PathVariable long userId);
}