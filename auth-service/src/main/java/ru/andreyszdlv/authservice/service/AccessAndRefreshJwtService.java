package ru.andreyszdlv.authservice.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

@Service
public class AccessAndRefreshJwtService {

    @CachePut(value = "auth-service::getAccessToken", key = "#email")
    public String saveAccessTokenByUserEmail(String email, String accessToken){
        return accessToken;
    }

    @CachePut(value = "auth-service::getRefreshToken", key = "#email")
    public String saveRefreshTokenByUserEmail(String email, String refreshToken){
        return refreshToken;
    }

    @Cacheable(value = "auth-service::getAccessToken", key = "#email")
    public String getAccessTokenByUserEmail(String email){
        return null;
    }

    @Cacheable(value = "auth-service::getRefreshToken", key = "#email")
    public String getRefreshTokenByUserEmail(String email){
        return null;
    }

    @Caching(evict = {
            @CacheEvict(value = "auth-service::getAccessToken", key = "#email"),
            @CacheEvict(value = "auth-service::getRefreshToken", key = "#email")
    })
    public void deleteByUserEmail(String email){}
}
