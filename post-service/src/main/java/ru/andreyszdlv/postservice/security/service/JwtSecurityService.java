package ru.andreyszdlv.postservice.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

@Service
public class JwtSecurityService {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    // Генерация ключа для подписи на основе секретного ключа
    private SecretKey getSigningKey(){
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Метод для извлечения конкретного клейма
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Извлечение всех клеймов из токена
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Извлечение email пользователя
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Извлечение даты истечения токена
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Проверка, истек ли срок действия токена
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Извлечение ролей пользователя из токена
    public String extractRoles(String token) {
        return extractAllClaims(token).get("roles").toString();
    }

    // Валидация токена (опционально, если нужно)
    public boolean validateToken(String token) {
        return !isTokenExpired(token);
    }
}