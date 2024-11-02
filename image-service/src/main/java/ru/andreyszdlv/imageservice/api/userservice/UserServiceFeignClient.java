package ru.andreyszdlv.imageservice.api.userservice;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.andreyszdlv.imageservice.dto.IdImageRequestDTO;

@FeignClient(name = "user-service", path = "/internal/user")
public interface UserServiceFeignClient {
    @PostMapping("/save-id-image/{userId}")
    ResponseEntity<Void> saveUserIdImage(@PathVariable long userId,
                                                @RequestBody IdImageRequestDTO requestDTO);

    @GetMapping("/{userId}/idImage")
    ResponseEntity<String> getIdImageByUserId(@PathVariable long userId);
}
