package co.ingedev.dataAnalist.controller;

import co.ingedev.dataAnalist.dto.request.LoginRequest;
import co.ingedev.dataAnalist.dto.request.RegisterRequest;
import co.ingedev.dataAnalist.dto.response.JwtResponse;
import co.ingedev.dataAnalist.dto.response.UserResponse;
import co.ingedev.dataAnalist.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Login and registration endpoints")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Authenticate user and get JWT token")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user (role USER by default)")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }
}
