package ru.andreyszdlv.postservice.configuration;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricConfig {

    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }

    @Bean
    public MeterBinder meterBinder() {
        return registry ->{
            Counter.builder("posts_per_user")
                    .description("Количество создаваемых постов на одного пользователя")
                    .tag("id", "-1")
                    .register(registry);

            Counter.builder("comments_per_post")
                    .description("Количество комментариев на один пост")
                    .tag("post_id","-1")
                    .register(registry);

            Counter.builder("likes_per_post")
                    .description("Количество лайков на один пост")
                    .tag("post_id","-1")
                    .register(registry);
        };
    }
}
