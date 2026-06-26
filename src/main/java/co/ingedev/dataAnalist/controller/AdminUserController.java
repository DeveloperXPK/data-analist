package co.ingedev.dataAnalist.controller;

import co.ingedev.dataAnalist.dto.response.UserResponse;
import co.ingedev.dataAnalist.enums.Role;
import co.ingedev.dataAnalist.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@Tag(name = "Admin — Users", description = "Admin-only user management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "List all users (paginated)")
    public ResponseEntity<Page<UserResponse>> getAll(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(userService.getAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<UserResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    @PatchMapping("/{id}/role")
    @Operation(summary = "Update user role")
    public ResponseEntity<UserResponse> updateRole(@PathVariable UUID id, @RequestParam Role role) {
        return ResponseEntity.ok(userService.updateRole(id, role));
    }

    @PatchMapping("/{id}/enabled")
    @Operation(summary = "Enable or disable a user account")
    public ResponseEntity<UserResponse> toggleEnabled(@PathVariable UUID id, @RequestParam boolean enabled) {
        return ResponseEntity.ok(userService.toggleEnabled(id, enabled));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
