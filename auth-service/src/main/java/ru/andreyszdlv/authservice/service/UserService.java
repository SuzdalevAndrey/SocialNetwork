package ru.andreyszdlv.authservice.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.andreyszdlv.authservice.api.userservice.UserServiceFeignClient;
import ru.andreyszdlv.authservice.dto.userservicefeigndto.UserDetailsResponseDTO;
import ru.andreyszdlv.authservice.model.User;


@Service
@AllArgsConstructor
@Slf4j
public class UserService {

    private final UserServiceFeignClient userServiceFeignClient;

    public UserDetailsService getDetailsService() {

        UserDetailsService detailsService = new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username)
                    throws UsernameNotFoundException {
                log.info("getDetailsService in getDetailsService");
                UserDetailsResponseDTO user = userServiceFeignClient
                        .getUserDetailsByUserEmail(username)
                        .getBody();

                return User
                        .builder()
                        .id(user.id())
                        .email(user.email())
                        .password(user.password())
                        .name(user.name())
                        .role(user.role())
                        .build();
            }
        };

        return detailsService;
    }

}
