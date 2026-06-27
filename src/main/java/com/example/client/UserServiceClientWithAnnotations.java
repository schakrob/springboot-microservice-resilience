package com.example.client;

import com.example.dto.UserDto;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * REST Client with Annotation-based Decorators
 * Applies: @CircuitBreaker, @Retry, @RateLimiter, @Bulkhead
 */
@Slf4j
@Component
public class UserServiceClientWithAnnotations {

    private final RestTemplate restTemplate;
    private static final String REMOTE_SERVICE_URL = "http://localhost:8081/api";

    public UserServiceClientWithAnnotations(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Get user by ID with all resilience annotations
     */
    @CircuitBreaker(name = "userServiceCB", fallbackMethod = "getUserByIdFallback")
    @Retry(name = "userServiceRetry")
    @RateLimiter(name = "userServiceRateLimit")
    @Bulkhead(name = "userServiceBulkhead")
    public UserDto getUserById(Long userId) {
        log.info("Fetching user with ID: {} (annotation-based)", userId);
        String url = REMOTE_SERVICE_URL + "/users/" + userId;
        log.debug("Calling remote service: {}", url);
        
        try {
            UserDto user = restTemplate.getForObject(url, UserDto.class);
            log.info("Successfully retrieved user: {}", user);
            return user;
        } catch (Exception e) {
            log.error("Error fetching user: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Fallback method for getUserById
     */
    public UserDto getUserByIdFallback(Long userId, Exception ex) {
        log.warn("CircuitBreaker fallback triggered for getUserById. Exception: {}", ex.getMessage());
        return UserDto.builder()
                .id(userId)
                .name("Fallback User")
                .email("fallback@example.com")
                .phone("N/A")
                .build();
    }

    /**
     * Get all users with all resilience annotations
     */
    @CircuitBreaker(name = "userServiceCB", fallbackMethod = "getAllUsersFallback")
    @Retry(name = "userServiceRetry")
    @RateLimiter(name = "userServiceRateLimit")
    @Bulkhead(name = "userServiceBulkhead")
    public UserDto[] getAllUsers() {
        log.info("Fetching all users (annotation-based)");
        String url = REMOTE_SERVICE_URL + "/users";
        log.debug("Calling remote service: {}", url);
        
        try {
            UserDto[] users = restTemplate.getForObject(url, UserDto[].class);
            log.info("Successfully retrieved {} users", users != null ? users.length : 0);
            return users;
        } catch (Exception e) {
            log.error("Error fetching users: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Fallback method for getAllUsers
     */
    public UserDto[] getAllUsersFallback(Exception ex) {
        log.warn("CircuitBreaker fallback triggered for getAllUsers. Exception: {}", ex.getMessage());
        return new UserDto[0];
    }

    /**
     * Create user with all resilience annotations
     */
    @CircuitBreaker(name = "userServiceCB", fallbackMethod = "createUserFallback")
    @Retry(name = "userServiceRetry")
    @RateLimiter(name = "userServiceRateLimit")
    @Bulkhead(name = "userServiceBulkhead")
    public UserDto createUser(UserDto userDto) {
        log.info("Creating user (annotation-based): {}", userDto);
        String url = REMOTE_SERVICE_URL + "/users";
        log.debug("Calling remote service to create user: {}", url);
        
        try {
            UserDto createdUser = restTemplate.postForObject(url, userDto, UserDto.class);
            log.info("User created successfully: {}", createdUser);
            return createdUser;
        } catch (Exception e) {
            log.error("Error creating user: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Fallback method for createUser
     */
    public UserDto createUserFallback(UserDto userDto, Exception ex) {
        log.warn("CircuitBreaker fallback triggered for createUser. Exception: {}", ex.getMessage());
        userDto.setId(0L);
        return userDto;
    }

    /**
     * Update user with all resilience annotations
     */
    @CircuitBreaker(name = "userServiceCB", fallbackMethod = "updateUserFallback")
    @Retry(name = "userServiceRetry")
    @RateLimiter(name = "userServiceRateLimit")
    @Bulkhead(name = "userServiceBulkhead")
    public UserDto updateUser(Long userId, UserDto userDto) {
        log.info("Updating user: {} (annotation-based)", userId);
        String url = REMOTE_SERVICE_URL + "/users/" + userId;
        log.debug("Calling remote service to update user: {}", url);
        
        try {
            restTemplate.put(url, userDto);
            log.info("User updated successfully");
            return userDto;
        } catch (Exception e) {
            log.error("Error updating user: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Fallback method for updateUser
     */
    public UserDto updateUserFallback(Long userId, UserDto userDto, Exception ex) {
        log.warn("CircuitBreaker fallback triggered for updateUser. Exception: {}", ex.getMessage());
        return userDto;
    }

    /**
     * Delete user with all resilience annotations
     */
    @CircuitBreaker(name = "userServiceCB", fallbackMethod = "deleteUserFallback")
    @Retry(name = "userServiceRetry")
    @RateLimiter(name = "userServiceRateLimit")
    @Bulkhead(name = "userServiceBulkhead")
    public void deleteUser(Long userId) {
        log.info("Deleting user: {} (annotation-based)", userId);
        String url = REMOTE_SERVICE_URL + "/users/" + userId;
        log.debug("Calling remote service to delete user: {}", url);
        
        try {
            restTemplate.delete(url);
            log.info("User deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting user: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Fallback method for deleteUser
     */
    public void deleteUserFallback(Long userId, Exception ex) {
        log.warn("CircuitBreaker fallback triggered for deleteUser. Exception: {}", ex.getMessage());
    }
}