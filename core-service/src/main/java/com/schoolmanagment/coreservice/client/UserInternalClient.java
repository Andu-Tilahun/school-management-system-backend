package com.schoolmanagment.coreservice.client;

import com.schoolmanagment.commonapplication.api.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-service-internal", url = "${user-service.url}", contextId = "userInternalClient")
public interface UserInternalClient {

    @PostMapping("/api/internal/users/register")
    ApiResponse<Object> register(@RequestBody InternalUserRegisterRequest request);
}
