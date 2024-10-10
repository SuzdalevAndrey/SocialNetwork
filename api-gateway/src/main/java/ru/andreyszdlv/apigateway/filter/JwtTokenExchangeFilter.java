package ru.andreyszdlv.apigateway.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtTokenExchangeFilter implements GlobalFilter, Ordered {

    private final WebClient.Builder webClientBuilder;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String token = extractToken(exchange);

        if(token == null) {
            return chain.filter(exchange);
        }

        return webClientBuilder.build()
                .post()
                .uri("lb://auth-service/api/auth/generate-data-user")
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .bodyToMono(Map.class)
                .flatMap(
                        userInfo->{
                            ServerHttpRequest request = exchange.getRequest().mutate()
                                    .header("X-User-Email", (String) userInfo.get("email"))
                                    .header("X-User-Role", (String) userInfo.get("role"))
                                    .build();
                            return chain.filter(exchange.mutate().request(request).build());
                        }
                );
    }

    @Override
    public int getOrder() {
        return -1;
    }

    private String extractToken(ServerWebExchange exchange) {
        String bearerToken = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if(bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            return null;
        }
        return bearerToken.substring(7);
    }
}
