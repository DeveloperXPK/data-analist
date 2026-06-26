package co.ingedev.dataAnalist.service;

import co.ingedev.dataAnalist.dto.request.LoginRequest;
import co.ingedev.dataAnalist.dto.request.RegisterRequest;
import co.ingedev.dataAnalist.dto.response.JwtResponse;
import co.ingedev.dataAnalist.dto.response.UserResponse;
import co.ingedev.dataAnalist.entity.User;
import co.ingedev.dataAnalist.enums.Role;
import co.ingedev.dataAnalist.exception.DuplicateResourceException;
import co.ingedev.dataAnalist.repository.UserRepository;
import co.ingedev.dataAnalist.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public JwtResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        String token = jwtTokenProvider.generateToken(authentication);
        User user = (User) authentication.getPrincipal();
        return new JwtResponse(token, user.getUsername(), user.getRole().name());
    }

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new DuplicateResourceException("Username already taken: " + request.username());
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Email already registered: " + request.email());
        }

        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .build();

        return UserResponse.from(userRepository.save(user));
    }
}
