package com.example.controller;

import com.example.client.UserServiceClientWithAnnotations;
import com.example.client.UserServiceClientWithSupplier;
import com.example.dto.ApiResponse;
import com.example.dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@Slf4j
@RestController
@RequestMapping("/api/proxy")
public class UserController {

    @Autowired
    private UserServiceClientWithSupplier userServiceClientWithSupplier;

    @Autowired
    private UserServiceClientWithAnnotations userServiceClientWithAnnotations;

    // ==================== Supplier Pattern Endpoints ====================

    @GetMapping("/supplier/users/{id}")
    public ResponseEntity<ApiResponse<UserDto>> getUserByIdWithSupplier(@PathVariable Long id) {
        log.info("GET /api/proxy/supplier/users/{} - Supplier pattern", id);
        try {
            UserDto user = userServiceClientWithSupplier.getUserById(id);
            return ResponseEntity.ok(ApiResponse.<UserDto>builder()
                    .status(HttpStatus.OK.value())
                    .message("User retrieved successfully (Supplier Pattern)")
                    .data(user)
                    .build());
        } catch (Exception e) {
            log.error("Error retrieving user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<UserDto>builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .message("Error: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/supplier/users")
    public ResponseEntity<ApiResponse<UserDto[]>> getAllUsersWithSupplier() {
        log.info("GET /api/proxy/supplier/users - Supplier pattern");
        try {
            UserDto[] users = userServiceClientWithSupplier.getAllUsers();
            return ResponseEntity.ok(ApiResponse.<UserDto[]>builder()
                    .status(HttpStatus.OK.value())
                    .message("Users retrieved successfully (Supplier Pattern)")
                    .data(users)
                    .build());
        } catch (Exception e) {
            log.error("Error retrieving users", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<UserDto[]>builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .message("Error: " + e.getMessage())
                            .build());
        }
    }

    @PostMapping("/supplier/users")
    public ResponseEntity<ApiResponse<UserDto>> createUserWithSupplier(@RequestBody UserDto userDto) {
        log.info("POST /api/proxy/supplier/users - Supplier pattern");
        try {
            UserDto createdUser = userServiceClientWithSupplier.createUser(userDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.<UserDto>builder()
                            .status(HttpStatus.CREATED.value())
                            .message("User created successfully (Supplier Pattern)")
                            .data(createdUser)
                            .build());
        } catch (Exception e) {
            log.error("Error creating user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<UserDto>builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .message("Error: " + e.getMessage())
                            .build());
        }
    }

    // ==================== Annotation Pattern Endpoints ====================

    @GetMapping("/annotation/users/{id}")
    public ResponseEntity<ApiResponse<UserDto>> getUserByIdWithAnnotation(@PathVariable Long id) {
        log.info("GET /api/proxy/annotation/users/{} - Annotation pattern", id);
        try {
            UserDto user = userServiceClientWithAnnotations.getUserById(id);
            return ResponseEntity.ok(ApiResponse.<UserDto>builder()
                    .status(HttpStatus.OK.value())
                    .message("User retrieved successfully (Annotation Pattern)")
                    .data(user)
                    .build());
        } catch (Exception e) {
            log.error("Error retrieving user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<UserDto>builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .message("Error: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/annotation/users")
    public ResponseEntity<ApiResponse<UserDto[]>> getAllUsersWithAnnotation() {
        log.info("GET /api/proxy/annotation/users - Annotation pattern");
        try {
            UserDto[] users = userServiceClientWithAnnotations.getAllUsers();
            return ResponseEntity.ok(ApiResponse.<UserDto[]>builder()
                    .status(HttpStatus.OK.value())
                    .message("Users retrieved successfully (Annotation Pattern)")
                    .data(users)
                    .build());
        } catch (Exception e) {
            log.error("Error retrieving users", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<UserDto[]>builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .message("Error: " + e.getMessage())
                            .build());
        }
    }

    @PostMapping("/annotation/users")
    public ResponseEntity<ApiResponse<UserDto>> createUserWithAnnotation(@RequestBody UserDto userDto) {
        log.info("POST /api/proxy/annotation/users - Annotation pattern");
        try {
            UserDto createdUser = userServiceClientWithAnnotations.createUser(userDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.<UserDto>builder()
                            .status(HttpStatus.CREATED.value())
                            .message("User created successfully (Annotation Pattern)")
                            .data(createdUser)
                            .build());
        } catch (Exception e) {
            log.error("Error creating user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<UserDto>builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .message("Error: " + e.getMessage())
                            .build());
        }
    }

    @PutMapping("/annotation/users/{id}")
    public ResponseEntity<ApiResponse<UserDto>> updateUserWithAnnotation(
            @PathVariable Long id,
            @RequestBody UserDto userDto) {
        log.info("PUT /api/proxy/annotation/users/{} - Annotation pattern", id);
        try {
            UserDto updatedUser = userServiceClientWithAnnotations.updateUser(id, userDto);
            return ResponseEntity.ok(ApiResponse.<UserDto>builder()
                    .status(HttpStatus.OK.value())
                    .message("User updated successfully (Annotation Pattern)")
                    .data(updatedUser)
                    .build());
        } catch (Exception e) {
            log.error("Error updating user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<UserDto>builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .message("Error: " + e.getMessage())
                            .build());
        }
    }

    @DeleteMapping("/annotation/users/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUserWithAnnotation(@PathVariable Long id) {
        log.info("DELETE /api/proxy/annotation/users/{} - Annotation pattern", id);
        try {
            userServiceClientWithAnnotations.deleteUser(id);
            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .status(HttpStatus.OK.value())
                    .message("User deleted successfully (Annotation Pattern)")
                    .build());
        } catch (Exception e) {
            log.error("Error deleting user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Void>builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .message("Error: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .status(HttpStatus.OK.value())
                .message("Microservice is healthy")
                .data("OK")
                .build());
    }
}