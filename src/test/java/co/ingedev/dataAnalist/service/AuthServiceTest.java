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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthServiceImpl authService;

    private User savedUser;

    @BeforeEach
    void setUp() {
        savedUser = User.builder()
                .username("john")
                .email("john@example.com")
                .password("hashed-password")
                .role(Role.USER)
                .build();
    }

    @Test
    void register_WithValidData_ShouldReturnUserResponse() {
        RegisterRequest request = new RegisterRequest("john", "john@example.com", "password123");
        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed-password");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponse response = authService.register(request);

        assertThat(response.username()).isEqualTo("john");
        assertThat(response.role()).isEqualTo(Role.USER);
        verify(passwordEncoder).encode("password123");
    }

    @Test
    void register_WithExistingUsername_ShouldThrowDuplicateResourceException() {
        RegisterRequest request = new RegisterRequest("john", "other@example.com", "password123");
        when(userRepository.existsByUsername("john")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("john");

        verify(userRepository, never()).save(any());
    }

    @Test
    void register_WithExistingEmail_ShouldThrowDuplicateResourceException() {
        RegisterRequest request = new RegisterRequest("newuser", "john@example.com", "password123");
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("john@example.com");
    }

    @Test
    void register_ShouldAlwaysEncodePassword() {
        RegisterRequest request = new RegisterRequest("newuser", "new@example.com", "plaintext");
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode("plaintext")).thenReturn("$2a$12$encoded");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        authService.register(request);

        verify(passwordEncoder).encode("plaintext");
        verify(userRepository).save(argThat(u -> u.getPassword().equals("$2a$12$encoded")));
    }

    @Test
    void login_WithValidCredentials_ShouldReturnJwtResponse() {
        LoginRequest request = new LoginRequest("john", "password123");
        Authentication authentication = new UsernamePasswordAuthenticationToken(savedUser, null, savedUser.getAuthorities());

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtTokenProvider.generateToken(authentication)).thenReturn("jwt-token");

        JwtResponse response = authService.login(request);

        assertThat(response.token()).isEqualTo("jwt-token");
        assertThat(response.username()).isEqualTo("john");
        assertThat(response.type()).isEqualTo("Bearer");
    }
}
