package com.example.config;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        log.info("Configuring RestTemplate bean");
        return builder
                .setConnectTimeout(java.time.Duration.ofSeconds(5))
                .setReadTimeout(java.time.Duration.ofSeconds(10))
                .build();
    }

    @Bean
    public CircuitBreaker circuitBreaker(CircuitBreakerRegistry circuitBreakerRegistry) {
        log.info("Configuring CircuitBreaker: userServiceCB");
        return circuitBreakerRegistry.circuitBreaker("userServiceCB");
    }

    @Bean
    public Retry retry(RetryRegistry retryRegistry) {
        log.info("Configuring Retry: userServiceRetry");
        return retryRegistry.retry("userServiceRetry");
    }

    @Bean
    public RateLimiter rateLimiter(RateLimiterRegistry rateLimiterRegistry) {
        log.info("Configuring RateLimiter: userServiceRateLimit");
        return rateLimiterRegistry.rateLimiter("userServiceRateLimit");
    }

    @Bean
    public Bulkhead bulkhead(BulkheadRegistry bulkheadRegistry) {
        log.info("Configuring Bulkhead: userServiceBulkhead");
        return bulkheadRegistry.bulkhead("userServiceBulkhead");
    }
}