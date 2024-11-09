package ru.andreyszdlv.authservice.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;

public interface AccessAndRefreshJwtService {
    String generateAccessToken(long userId, String role);

    String generateRefreshToken(long userId, String role);

    String getAccessTokenByUserId(long userId);

    String getRefreshTokenByUserId(long userId);

    void deleteByUserId(long userId);
}
