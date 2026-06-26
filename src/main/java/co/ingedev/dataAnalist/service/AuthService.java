package co.ingedev.dataAnalist.service;

import co.ingedev.dataAnalist.dto.request.LoginRequest;
import co.ingedev.dataAnalist.dto.request.RegisterRequest;
import co.ingedev.dataAnalist.dto.response.JwtResponse;
import co.ingedev.dataAnalist.dto.response.UserResponse;

public interface AuthService {
    JwtResponse login(LoginRequest request);
    UserResponse register(RegisterRequest request);
}
