package ru.andreyszdlv.postservice.configuration;


import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.andreyszdlv.postservice.context.RequestContext;

@Configuration
@AllArgsConstructor
public class FeignConfig {
    private final RequestContext requestContext;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                template.header("Gateway-Request", "Secret-Key");
                template.header("Authorization", "Bearer " + requestContext.getToken());
            }
        };
    }

}
