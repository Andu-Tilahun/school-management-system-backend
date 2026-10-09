package com.schoolmanagment.coreservice.client;

import com.schoolmanagment.commonapplication.api.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(
        name = "user-service",
        url = "${user-service.url}",
        path = "/api/internal/users",
        contextId = "userInternalClient"
)
public interface UserInternalClient {

    @GetMapping("/{id}")
    ApiResponse<UserDto> getUserById(@PathVariable("id") UUID userId);

    @PostMapping("/register")
    ApiResponse<UserDto> register(@Valid @RequestBody InternalUserRegisterRequest request);
}
