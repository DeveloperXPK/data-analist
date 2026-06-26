package co.ingedev.dataAnalist.service;

import co.ingedev.dataAnalist.dto.request.RegisterRequest;
import co.ingedev.dataAnalist.dto.response.UserResponse;
import co.ingedev.dataAnalist.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {
    Page<UserResponse> getAll(Pageable pageable);
    UserResponse getById(UUID id);
    UserResponse updateRole(UUID id, Role role);
    UserResponse toggleEnabled(UUID id, boolean enabled);
    void delete(UUID id);
}
