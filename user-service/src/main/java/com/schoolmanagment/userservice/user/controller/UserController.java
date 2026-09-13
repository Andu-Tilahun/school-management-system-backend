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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

        ApiResponse<UserDto> response =
                ApiResponse.success(user, "User created successfully");

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/navigation-menu")
    public ResponseEntity<ApiResponse<List<NavigationMenuItemDto>>> getNavigationMenu(
    ) {
        UUID userId = userContext.getCurrentUserId();
        List<NavigationMenuItemDto> menu = navigationMenuService.buildMenuForUser(userId);
        return ResponseEntity.ok(
                ApiResponse.success(menu, "Navigation menu retrieved successfully")
        );
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
        return ResponseEntity.ok(
                ApiResponse.success(scopes, "Granted scopes retrieved successfully")
        );
    }

    @GetMapping
    @RequiresPermission(resource = "USERS", scope = "READ")
    public ResponseEntity<ApiResponse<Page>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir
    ) {
        UserFilterRequest filterRequest = UserFilterRequest.builder()
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDirection(sortDir)
                .build();

        Page<UserDto> users = userService.filterUsers(filterRequest);

        return ResponseEntity.ok(
                ApiResponse.success(users, "Users retrieved successfully")
        );
    }


    @PostMapping("/filter")
    @RequiresPermission(resource = "USERS", scope = "READ")
    public ResponseEntity<ApiResponse<Page>> filterUsers(@RequestBody UserFilterRequest request) {
        Page<UserDto> users = userService.filterUsers(request);
        return ResponseEntity.ok(
                ApiResponse.success(users, "Users retrieved successfully")
        );
    }

    @GetMapping("/{id}")
    @RequiresPermission(resource = "USERS", scope = "READ")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable UUID id) {
        UserDto user = userService.getUserById(id);
        return ResponseEntity.ok(
                ApiResponse.success(user, "User retrieved successfully")
        );
    }

    @PutMapping("/{id}")
    @RequiresPermission(resource = "USERS", scope = "UPDATE")
    public ResponseEntity<ApiResponse> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UserUpdateRequest request
    ) {
        UserDto user = userService.updateUser(id, request);
        return ResponseEntity.ok(
                ApiResponse.success(user, "User updated successfully")
        );
    }

    @DeleteMapping("/{id}")
    @RequiresPermission(resource = "USERS", scope = "DELETE")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(
                ApiResponse.success("User deleted successfully")
        );
    }


    @PutMapping("/profile")
    @RequiresPermission(resource = "USERS", scope = "UPDATE")
    public ResponseEntity<ApiResponse> updateProfile(
            @Valid @RequestBody UserUpdateRequest request
    ) {
        UUID userId = userContext.getCurrentUserId();
        UserDto user = userService.updateUser(userId, request);
        return ResponseEntity.ok(
                ApiResponse.success(user, "User profile updated successfully")
        );
    }

    @PutMapping("/{id}/lock")
    @RequiresPermission(resource = "USERS", scope = "UPDATE")
    public ResponseEntity<ApiResponse> lockUser(@PathVariable UUID id) {
        UserDto user = userService.lockUser(id);
        return ResponseEntity.ok(
                ApiResponse.success(user, "User locked successfully")
        );
    }

    @PutMapping("/{id}/unlock")
    @RequiresPermission(resource = "USERS", scope = "UPDATE")
    public ResponseEntity<ApiResponse> unlockUser(@PathVariable UUID id) {
        UserDto user = userService.unlockUser(id);
        return ResponseEntity.ok(
                ApiResponse.success(user, "User unlocked successfully")
        );
    }
}
