package com.schoolmanagment.userservice.user.controller;

import com.schoolmanagment.commonapplication.api.ApiResponse;
import com.schoolmanagment.commonsecurity.checker.RequiresPermission;
import com.schoolmanagment.commonsecurity.util.UserContext;
import com.schoolmanagment.userservice.menu.dto.NavigationMenuItemDto;
import com.schoolmanagment.userservice.menu.service.NavigationMenuService;
import com.schoolmanagment.userservice.user.dto.UserDto;
import com.schoolmanagment.userservice.user.dto.UserFilterRequest;
import com.schoolmanagment.userservice.user.dto.UserRegisterRequest;
import com.schoolmanagment.userservice.user.dto.UserUpdateRequest;
import com.schoolmanagment.userservice.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * User API. Authorization uses {@link RequiresPermission} with resource codes from {@code resources.name}
 * (e.g. {@code USERS}) and scope names from {@code scopes.name}. Use {@code READ} for read-only APIs;
 * {@code VIEW} includes {@code READ} for checks and unlocks the sidebar menu for that resource.
 */
@RestController
@RequestMapping(value = "/api/users", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final NavigationMenuService navigationMenuService;

    private final UserContext userContext;

    @PostMapping("/register")
    @RequiresPermission(resource = "USERS", scope = "CREATE")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody UserRegisterRequest request) {

        UserDto user = userService.registerUser(request);

        ApiResponse response = ApiResponse.builder()
                .success(true)
                .message("User registered successfully")
                .data(user)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/navigation-menu")
    public ResponseEntity<ApiResponse<List<NavigationMenuItemDto>>> getNavigationMenu(
    ) {
        UUID userId = userContext.getCurrentUserId();
        List<NavigationMenuItemDto> menu = navigationMenuService.buildMenuForUser(userId);
        ApiResponse<List<NavigationMenuItemDto>> response = ApiResponse.<List<NavigationMenuItemDto>>builder()
                .success(true)
                .data(menu)
                .message("Navigation menu retrieved successfully")
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * Per-resource list of granted scope names (READ, VIEW, CREATE, …). Add new scopes in the database only;
     * the shape stays the same.
     */
    @GetMapping("/me/ui-actions")
    public ResponseEntity<ApiResponse<Map<String, List<String>>>> getGrantedScopesForCurrentUser(
    ) {
        UUID userId = userContext.getCurrentUserId();
        Map<String, List<String>> scopes = navigationMenuService.buildGrantedScopesByResourceForUser(userId);
        ApiResponse<Map<String, List<String>>> response =
                ApiResponse.<Map<String, List<String>>>builder()
                        .success(true)
                        .data(scopes)
                        .message("Granted scopes retrieved successfully")
                        .timestamp(LocalDateTime.now())
                        .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @RequiresPermission(resource = "USERS", scope = "READ")
    public ResponseEntity<ApiResponse<Page>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir
    ) {
        Sort sort = sortDir.equalsIgnoreCase("ASC") ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<UserDto> users = userService.getAllUsers(pageable);

        ApiResponse response = ApiResponse.builder()
                .success(true)
                .data(users)
                .message("Users retrieved successfully")
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }


    @PostMapping("/filter")
    @RequiresPermission(resource = "USERS", scope = "READ")
    public ResponseEntity<ApiResponse<Page>> filterUsers(@RequestBody UserFilterRequest request) {
        Page<UserDto> users = userService.filterUsers(request);
        ApiResponse response = ApiResponse.builder()
                .success(true)
                .data(users)
                .message("Users retrieved successfully")
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @RequiresPermission(resource = "USERS", scope = "READ")
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

    @PutMapping("/{id}")
    @RequiresPermission(resource = "USERS", scope = "UPDATE")
    public ResponseEntity<ApiResponse> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UserUpdateRequest request
    ) {
        UserDto user = userService.updateUser(id, request);
        ApiResponse response = ApiResponse.builder()
                .success(true)
                .data(user)
                .message("User updated successfully")
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @RequiresPermission(resource = "USERS", scope = "DELETE")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        ApiResponse response = ApiResponse.builder()
                .success(true)
                .message("User deleted successfully")
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }


    @PutMapping("/profile")
    @RequiresPermission(resource = "USERS", scope = "UPDATE")
    public ResponseEntity<ApiResponse> updateProfile(
            @Valid @RequestBody UserUpdateRequest request
    ) {
        UUID userId = userContext.getCurrentUserId();
        UserDto user = userService.updateUser(userId, request);
        ApiResponse response = ApiResponse.builder()
                .success(true)
                .data(user)
                .message("User updated successfully")
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/lock")
    @RequiresPermission(resource = "USERS", scope = "UPDATE")
    public ResponseEntity<ApiResponse> lockUser(@PathVariable UUID id) {
        UserDto user = userService.lockUser(id);
        ApiResponse response = ApiResponse.builder()
                .success(true)
                .data(user)
                .message("User locked successfully")
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/unlock")
    @RequiresPermission(resource = "USERS", scope = "UPDATE")
    public ResponseEntity<ApiResponse> unlockUser(@PathVariable UUID id) {
        UserDto user = userService.unlockUser(id);
        ApiResponse response = ApiResponse.builder()
                .success(true)
                .data(user)
                .message("User unlocked successfully")
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }
}
