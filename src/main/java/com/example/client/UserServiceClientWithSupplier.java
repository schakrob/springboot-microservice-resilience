package com.example.client;

import com.example.dto.ApiResponse;
import com.example.dto.UserDto;
import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.retry.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.function.Supplier;

/**
 * REST Client with Supplier and Decorators Pattern
 * Applies: CircuitBreaker, Retry, RateLimiter, Bulkhead
 */
@Slf4j
@Component
public class UserServiceClientWithSupplier {

    private final RestTemplate restTemplate;
    private final CircuitBreaker circuitBreaker;
    private final Retry retry;
    private final RateLimiter rateLimiter;
    private final Bulkhead bulkhead;

    private static final String REMOTE_SERVICE_URL = "http://localhost:8081/api";

    public UserServiceClientWithSupplier(
            RestTemplate restTemplate,
            CircuitBreaker circuitBreaker,
            Retry retry,
            RateLimiter rateLimiter,
            Bulkhead bulkhead) {
        this.restTemplate = restTemplate;
        this.circuitBreaker = circuitBreaker;
        this.retry = retry;
        this.rateLimiter = rateLimiter;
        this.bulkhead = bulkhead;
    }

    /**
     * Get user by ID using decorator supplier pattern
     * Applies all resilience decorators in sequence
     */
    public UserDto getUserById(Long userId) {
        log.info("Fetching user with ID: {}", userId);
        
        Supplier<UserDto> supplier = () -> {
            String url = REMOTE_SERVICE_URL + "/users/" + userId;
            log.debug("Calling remote service: {}", url);
            ApiResponse<UserDto> response = restTemplate.getForObject(
                    url,
                    ApiResponse.class
            );
            if (response != null && response.getData() != null) {
                log.info("Successfully retrieved user: {}", response.getData());
                return response.getData();
            }
            throw new RuntimeException("User not found");
        };

        // Apply decorators in sequence
        Supplier<UserDto> decoratedSupplier = Bulkhead.decorateSupplier(bulkhead, supplier);
        decoratedSupplier = RateLimiter.decorateSupplier(rateLimiter, decoratedSupplier);
        decoratedSupplier = Retry.decorateSupplier(retry, decoratedSupplier);
        decoratedSupplier = CircuitBreaker.decorateSupplier(circuitBreaker, decoratedSupplier);

        try {
            return decoratedSupplier.get();
        } catch (Exception e) {
            log.error("Error fetching user: {}", e.getMessage(), e);
            // Return fallback user
            return UserDto.builder()
                    .id(userId)
                    .name("Fallback User")
                    .email("fallback@example.com")
                    .build();
        }
    }

    /**
     * Get all users using decorator supplier pattern
     */
    public UserDto[] getAllUsers() {
        log.info("Fetching all users");
        
        Supplier<UserDto[]> supplier = () -> {
            String url = REMOTE_SERVICE_URL + "/users";
            log.debug("Calling remote service: {}", url);
            return restTemplate.getForObject(url, UserDto[].class);
        };

        // Apply decorators
        Supplier<UserDto[]> decoratedSupplier = Bulkhead.decorateSupplier(bulkhead, supplier);
        decoratedSupplier = RateLimiter.decorateSupplier(rateLimiter, decoratedSupplier);
        decoratedSupplier = Retry.decorateSupplier(retry, decoratedSupplier);
        decoratedSupplier = CircuitBreaker.decorateSupplier(circuitBreaker, decoratedSupplier);

        try {
            UserDto[] users = decoratedSupplier.get();
            log.info("Successfully retrieved {} users", users != null ? users.length : 0);
            return users != null ? users : new UserDto[0];
        } catch (Exception e) {
            log.error("Error fetching users: {}", e.getMessage(), e);
            return new UserDto[0]; // Return empty array as fallback
        }
    }

    /**
     * Create user using decorator supplier pattern
     */
    public UserDto createUser(UserDto userDto) {
        log.info("Creating user: {}", userDto);
        
        Supplier<UserDto> supplier = () -> {
            String url = REMOTE_SERVICE_URL + "/users";
            log.debug("Calling remote service to create user: {}", url);
            ApiResponse<UserDto> response = restTemplate.postForObject(
                    url,
                    userDto,
                    ApiResponse.class
            );
            if (response != null && response.getData() != null) {
                log.info("User created successfully: {}", response.getData());
                return response.getData();
            }
            throw new RuntimeException("Failed to create user");
        };

        // Apply decorators
        Supplier<UserDto> decoratedSupplier = Bulkhead.decorateSupplier(bulkhead, supplier);
        decoratedSupplier = RateLimiter.decorateSupplier(rateLimiter, decoratedSupplier);
        decoratedSupplier = Retry.decorateSupplier(retry, decoratedSupplier);
        decoratedSupplier = CircuitBreaker.decorateSupplier(circuitBreaker, decoratedSupplier);

        try {
            return decoratedSupplier.get();
        } catch (Exception e) {
            log.error("Error creating user: {}", e.getMessage(), e);
            userDto.setId(0L);
            return userDto; // Return the input as fallback
        }
    }
}