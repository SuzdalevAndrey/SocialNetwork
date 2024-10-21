package ru.andreyszdlv.postservice.api.userservice;

import io.micrometer.core.annotation.Timed;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", path = "/internal/user")
public interface UserServiceFeignClient {

    @Timed("get_user_email_by_user_id_time")
    @GetMapping("/email/{userId}")
    ResponseEntity<String> getUserEmailByUserId(@PathVariable long userId);

    @Timed("get_name_by_user_email_time")
    @GetMapping("/name/{userId}")
    ResponseEntity<String> getNameByUserId(@PathVariable long userId);
}
