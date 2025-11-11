package com.classmateai.backend.auth;

import com.classmateai.backend.entity.User;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.status(201).body(
                java.util.Map.of("message", "Usuario registrado exitosamente")
        );
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        User user = authService.login(request);

        String token = jwtUtil.generateToken(user);

        return ResponseEntity.ok(
                java.util.Map.of(
                        "token", token,
                        "user", java.util.Map.of(
                                "id", user.getId(),
                                "name", user.getName(),
                                "email", user.getEmail()
                        )
                )
        );
    }

    @Data
    static class RegisterRequest {
        private String name;
        private String email;
        private String password;
    }

    @Data
    static class LoginRequest {
        private String email;
        private String password;
    }
}
