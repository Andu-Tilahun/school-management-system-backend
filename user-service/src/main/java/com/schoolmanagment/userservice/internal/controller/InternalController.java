package com.schoolmanagment.userservice.internal.controller;

import com.schoolmanagment.commonapplication.api.ApiResponse;
import com.schoolmanagment.userservice.internal.dto.InternalRegisterRequest;
import com.schoolmanagment.userservice.user.dto.UserRegisterRequest;
import com.schoolmanagment.userservice.user.dto.UserDto;
import com.schoolmanagment.userservice.policy.entity.Policy;
import com.schoolmanagment.userservice.policy.repository.PolicyRepository;
import com.schoolmanagment.userservice.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/internal/users", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class InternalController {

    private final UserService userService;
    private final PolicyRepository policyRepository;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody InternalRegisterRequest request) {
        Set<UUID> policyIds = new HashSet<>();
        if (request.getPolicyNames() != null) {
            for (String name : request.getPolicyNames()) {
                if (name == null || name.isBlank()) {
                    continue;
                }
                policyRepository.findByName(name.trim()).map(Policy::getId).ifPresent(policyIds::add);
            }
        }

        UserRegisterRequest registerRequest = UserRegisterRequest.builder()
                .username(request.getUsername())
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .lastName(request.getLastName())
                .gender(request.getGender())
                .email(request.getEmail())
                .password(request.getPassword())
                .policyIds(policyIds.isEmpty() ? null : policyIds)
                .externalId(request.getExternalId())
                .build();

        UserDto user = userService.registerUser(registerRequest);

        ApiResponse response = ApiResponse.builder()
                .success(true)
                .message("User registered successfully")
                .data(user)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable UUID id) {
        UserDto user = userService.getUserById(id);
        ApiResponse response = ApiResponse.builder()
                .success(true)
                .data(user)
                .message("User retrieved successfully")
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }
}
