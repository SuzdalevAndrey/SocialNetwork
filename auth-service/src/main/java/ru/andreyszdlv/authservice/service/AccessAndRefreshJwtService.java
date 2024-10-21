package ru.andreyszdlv.authservice.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

@Service
public class AccessAndRefreshJwtService {

    @CachePut(value = "auth-service::getAccessToken", key = "#userId")
    public String saveAccessTokenByUserId(long userId, String accessToken){
        return accessToken;
    }

    @CachePut(value = "auth-service::getRefreshToken", key = "#userId")
    public String saveRefreshTokenByUserId(long userId, String refreshToken){
        return refreshToken;
    }

    @Cacheable(value = "auth-service::getAccessToken", key = "#userId")
    public String getAccessTokenByUserId(long userId){
        return null;
    }

    @Cacheable(value = "auth-service::getRefreshToken", key = "#userId")
    public String getRefreshTokenByUserId(long userId){
        return null;
    }

    @Caching(evict = {
            @CacheEvict(value = "auth-service::getAccessToken", key = "#userId"),
            @CacheEvict(value = "auth-service::getRefreshToken", key = "#userId")
    })
    public void deleteByUserId(long userId){}
}
