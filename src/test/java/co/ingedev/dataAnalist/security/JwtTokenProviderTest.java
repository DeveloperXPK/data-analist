package co.ingedev.dataAnalist.security;

import co.ingedev.dataAnalist.config.AppProperties;
import co.ingedev.dataAnalist.entity.User;
import co.ingedev.dataAnalist.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Base64;

import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class JwtTokenProviderTest {

    @Mock
    private AppProperties appProperties;

    @InjectMocks
    private JwtTokenProvider jwtTokenProvider;

    private static final String SECRET =
            Base64.getEncoder().encodeToString("test-secret-key-for-testing-only-must-be-256-bits".getBytes());
    private static final long EXPIRATION_MS = 3_600_000L;

    private User testUser;

    @BeforeEach
    void setUp() {
        when(appProperties.jwtSecret()).thenReturn(SECRET);
        when(appProperties.jwtExpirationMs()).thenReturn(EXPIRATION_MS);

        testUser = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("encoded-password")
                .role(Role.USER)
                .build();
    }

    @Test
    void generateToken_ShouldReturnNonNullToken() {
        Authentication auth = new UsernamePasswordAuthenticationToken(testUser, null, testUser.getAuthorities());
        String token = jwtTokenProvider.generateToken(auth);
        assertThat(token).isNotNull().isNotBlank();
    }

    @Test
    void getUsernameFromToken_ShouldExtractCorrectSubject() {
        Authentication auth = new UsernamePasswordAuthenticationToken(testUser, null, testUser.getAuthorities());
        String token = jwtTokenProvider.generateToken(auth);
        String username = jwtTokenProvider.getUsernameFromToken(token);
        assertThat(username).isEqualTo("testuser");
    }

    @Test
    void validateToken_WithValidToken_ShouldReturnTrue() {
        Authentication auth = new UsernamePasswordAuthenticationToken(testUser, null, testUser.getAuthorities());
        String token = jwtTokenProvider.generateToken(auth);
        assertThat(jwtTokenProvider.validateToken(token)).isTrue();
    }

    @Test
    void validateToken_WithTamperedToken_ShouldReturnFalse() {
        Authentication auth = new UsernamePasswordAuthenticationToken(testUser, null, testUser.getAuthorities());
        String token = jwtTokenProvider.generateToken(auth);
        String tampered = token.substring(0, token.length() - 5) + "XXXXX";
        assertThat(jwtTokenProvider.validateToken(tampered)).isFalse();
    }

    @Test
    void validateToken_WithInvalidToken_ShouldReturnFalse() {
        assertThat(jwtTokenProvider.validateToken("not.a.valid.jwt")).isFalse();
    }

    @Test
    void validateToken_WithEmptyToken_ShouldReturnFalse() {
        assertThat(jwtTokenProvider.validateToken("")).isFalse();
    }
}
