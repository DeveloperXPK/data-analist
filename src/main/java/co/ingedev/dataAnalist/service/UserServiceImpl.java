package co.ingedev.dataAnalist.service;

import co.ingedev.dataAnalist.dto.response.UserResponse;
import co.ingedev.dataAnalist.entity.User;
import co.ingedev.dataAnalist.enums.Role;
import co.ingedev.dataAnalist.exception.ResourceNotFoundException;
import co.ingedev.dataAnalist.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAll(Pageable pageable) {
        return userRepository.findAll(pageable).map(UserResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getById(UUID id) {
        return UserResponse.from(findUserById(id));
    }

    @Override
    @Transactional
    public UserResponse updateRole(UUID id, Role role) {
        User user = findUserById(id);
        user.setRole(role);
        return UserResponse.from(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserResponse toggleEnabled(UUID id, boolean enabled) {
        User user = findUserById(id);
        user.setEnabled(enabled);
        return UserResponse.from(userRepository.save(user));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found: " + id);
        }
        userRepository.deleteById(id);
    }

    private User findUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }
}
