package com.schoolmanagment.commonsecurity.client;

import com.schoolmanagment.commonapplication.api.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.Map;

/**
 * Loads the current user's granted scopes from user-service.
 */
@FeignClient(name = "user-service-rbac", url = "${user-service.url}", contextId = "userRbacClient")
public interface UserRbacClient {

    @GetMapping("/api/users/me/ui-actions")
    ApiResponse<Map<String, List<String>>> getGrantedScopes(@RequestHeader("Authorization") String authorization);
}
