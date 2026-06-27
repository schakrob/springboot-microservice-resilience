package com.example.client;

import com.example.dto.ApiResponse;
import com.example.dto.UserDto;
import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.retry.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.function.Supplier;

/**
 * REST Client with Supplier and Decorators Pattern using Spring Boot 4.0+ RestClient
 * Applies: CircuitBreaker, Retry, RateLimiter, Bulkhead
 */
@Slf4j
@Component
public class UserServiceClientWithSupplier {

    private final RestClient restClient;
    private final CircuitBreaker circuitBreaker;
    private final Retry retry;
    private final RateLimiter rateLimiter;
    private final Bulkhead bulkhead;

    private static final String REMOTE_SERVICE_URL = "http://localhost:8081/api";

    public UserServiceClientWithSupplier(
            RestClient restClient,
            CircuitBreaker circuitBreaker,
            Retry retry,
            RateLimiter rateLimiter,
            Bulkhead bulkhead) {
        this.restClient = restClient;
        this.circuitBreaker = circuitBreaker;
        this.retry = retry;
        this.rateLimiter = rateLimiter;
        this.bulkhead = bulkhead;
    }

    /**
     * Get user by ID using decorator supplier pattern with RestClient
     * Applies all resilience decorators in sequence
     */
    public UserDto getUserById(Long userId) {
        log.info("Fetching user with ID: {} (Supplier Pattern)", userId);
        
        Supplier<UserDto> supplier = () -> {
            String url = REMOTE_SERVICE_URL + "/users/" + userId;
            log.debug("Calling remote service: {}", url);
            try {
                UserDto user = restClient.get()
                        .uri(url)
                        .retrieve()
                        .body(UserDto.class);
                
                if (user != null) {
                    log.info("Successfully retrieved user: {}", user);
                    return user;
                }
                throw new RuntimeException("User not found");
            } catch (Exception e) {
                log.error("Error fetching user from {}: {}", url, e.getMessage());
                throw new RuntimeException("Failed to fetch user", e);
            }
        };

        // Apply decorators in sequence: Bulkhead -> RateLimiter -> Retry -> CircuitBreaker
        Supplier<UserDto> decoratedSupplier = Bulkhead.decorateSupplier(bulkhead, supplier);
        decoratedSupplier = RateLimiter.decorateSupplier(rateLimiter, decoratedSupplier);
        decoratedSupplier = Retry.decorateSupplier(retry, decoratedSupplier);
        decoratedSupplier = CircuitBreaker.decorateSupplier(circuitBreaker, decoratedSupplier);

        try {
            return decoratedSupplier.get();
        } catch (Exception e) {
            log.error("Error fetching user (all resilience decorators failed): {}", e.getMessage(), e);
            // Return fallback user
            return UserDto.builder()
                    .id(userId)
                    .name("Fallback User")
                    .email("fallback@example.com")
                    .phone("N/A")
                    .build();
        }
    }

    /**
     * Get all users using decorator supplier pattern with RestClient
     */
    public UserDto[] getAllUsers() {
        log.info("Fetching all users (Supplier Pattern)");
        
        Supplier<UserDto[]> supplier = () -> {
            String url = REMOTE_SERVICE_URL + "/users";
            log.debug("Calling remote service: {}", url);
            try {
                UserDto[] users = restClient.get()
                        .uri(url)
                        .retrieve()
                        .body(UserDto[].class);
                
                if (users != null) {
                    log.info("Successfully retrieved {} users", users.length);
                    return users;
                }
                return new UserDto[0];
            } catch (Exception e) {
                log.error("Error fetching users from {}: {}", url, e.getMessage());
                throw new RuntimeException("Failed to fetch users", e);
            }
        };

        // Apply decorators
        Supplier<UserDto[]> decoratedSupplier = Bulkhead.decorateSupplier(bulkhead, supplier);
        decoratedSupplier = RateLimiter.decorateSupplier(rateLimiter, decoratedSupplier);
        decoratedSupplier = Retry.decorateSupplier(retry, decoratedSupplier);
        decoratedSupplier = CircuitBreaker.decorateSupplier(circuitBreaker, decoratedSupplier);

        try {
            UserDto[] users = decoratedSupplier.get();
            log.info("Successfully retrieved {} users from remote service", users != null ? users.length : 0);
            return users != null ? users : new UserDto[0];
        } catch (Exception e) {
            log.error("Error fetching users (all resilience decorators failed): {}", e.getMessage(), e);
            return new UserDto[0]; // Return empty array as fallback
        }
    }

    /**
     * Create user using decorator supplier pattern with RestClient
     */
    public UserDto createUser(UserDto userDto) {
        log.info("Creating user (Supplier Pattern): {}", userDto);
        
        Supplier<UserDto> supplier = () -> {
            String url = REMOTE_SERVICE_URL + "/users";
            log.debug("Calling remote service to create user: {}", url);
            try {
                UserDto createdUser = restClient.post()
                        .uri(url)
                        .body(userDto)
                        .retrieve()
                        .body(UserDto.class);
                
                if (createdUser != null) {
                    log.info("User created successfully: {}", createdUser);
                    return createdUser;
                }
                throw new RuntimeException("Failed to create user");
            } catch (Exception e) {
                log.error("Error creating user at {}: {}", url, e.getMessage());
                throw new RuntimeException("Failed to create user", e);
            }
        };

        // Apply decorators
        Supplier<UserDto> decoratedSupplier = Bulkhead.decorateSupplier(bulkhead, supplier);
        decoratedSupplier = RateLimiter.decorateSupplier(rateLimiter, decoratedSupplier);
        decoratedSupplier = Retry.decorateSupplier(retry, decoratedSupplier);
        decoratedSupplier = CircuitBreaker.decorateSupplier(circuitBreaker, decoratedSupplier);

        try {
            return decoratedSupplier.get();
        } catch (Exception e) {
            log.error("Error creating user (all resilience decorators failed): {}", e.getMessage(), e);
            userDto.setId(0L);
            return userDto; // Return the input as fallback
        }
    }

    /**
     * Update user using decorator supplier pattern with RestClient
     */
    public UserDto updateUser(Long userId, UserDto userDto) {
        log.info("Updating user (Supplier Pattern): {}", userId);
        
        Supplier<UserDto> supplier = () -> {
            String url = REMOTE_SERVICE_URL + "/users/" + userId;
            log.debug("Calling remote service to update user: {}", url);
            try {
                restClient.put()
                        .uri(url)
                        .body(userDto)
                        .retrieve()
                        .toBodilessEntity();
                
                log.info("User updated successfully: {}", userId);
                return userDto;
            } catch (Exception e) {
                log.error("Error updating user at {}: {}", url, e.getMessage());
                throw new RuntimeException("Failed to update user", e);
            }
        };

        // Apply decorators
        Supplier<UserDto> decoratedSupplier = Bulkhead.decorateSupplier(bulkhead, supplier);
        decoratedSupplier = RateLimiter.decorateSupplier(rateLimiter, decoratedSupplier);
        decoratedSupplier = Retry.decorateSupplier(retry, decoratedSupplier);
        decoratedSupplier = CircuitBreaker.decorateSupplier(circuitBreaker, decoratedSupplier);

        try {
            return decoratedSupplier.get();
        } catch (Exception e) {
            log.error("Error updating user (all resilience decorators failed): {}", e.getMessage(), e);
            return userDto; // Return the input as fallback
        }
    }

    /**
     * Delete user using decorator supplier pattern with RestClient
     */
    public void deleteUser(Long userId) {
        log.info("Deleting user (Supplier Pattern): {}", userId);
        
        Supplier<Void> supplier = () -> {
            String url = REMOTE_SERVICE_URL + "/users/" + userId;
            log.debug("Calling remote service to delete user: {}", url);
            try {
                restClient.delete()
                        .uri(url)
                        .retrieve()
                        .toBodilessEntity();
                
                log.info("User deleted successfully: {}", userId);
                return null;
            } catch (Exception e) {
                log.error("Error deleting user at {}: {}", url, e.getMessage());
                throw new RuntimeException("Failed to delete user", e);
            }
        };

        // Apply decorators
        Supplier<Void> decoratedSupplier = Bulkhead.decorateSupplier(bulkhead, supplier);
        decoratedSupplier = RateLimiter.decorateSupplier(rateLimiter, decoratedSupplier);
        decoratedSupplier = Retry.decorateSupplier(retry, decoratedSupplier);
        decoratedSupplier = CircuitBreaker.decorateSupplier(circuitBreaker, decoratedSupplier);

        try {
            decoratedSupplier.get();
        } catch (Exception e) {
            log.error("Error deleting user (all resilience decorators failed): {}", e.getMessage(), e);
        }
    }
}
