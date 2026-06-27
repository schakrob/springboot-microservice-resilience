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
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import java.time.Duration;

/**
 * Configuration for RestClient (Spring Boot 4.0+)
 * Replaces RestTemplate with the new RestClient builder
 */
@Slf4j
@Configuration
public class RestClientConfig {

    @Bean
    public RestClient restClient() {
        log.info("Configuring RestClient bean for Spring Boot 4.0+");
        return RestClient.builder()
                .requestFactory(ClientHttpRequestFactorySettings.builder()
                        .connectTimeout(Duration.ofSeconds(5))
                        .readTimeout(Duration.ofSeconds(10))
                        .build())
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
