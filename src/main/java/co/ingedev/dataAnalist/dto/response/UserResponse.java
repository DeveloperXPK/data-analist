package co.ingedev.dataAnalist.dto.response;

import co.ingedev.dataAnalist.entity.User;
import co.ingedev.dataAnalist.enums.Role;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String username,
        String email,
        Role role,
        boolean enabled,
        LocalDateTime createdAt
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.isEnabled(),
                user.getCreatedAt()
        );
    }
}
