package com.classmateai.backend.auth;

import com.classmateai.backend.entity.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.status(201).body(
                java.util.Map.of("message", "Usuario registrado exitosamente")
        );
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
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
        @NotBlank(message = "El nombre no puede estar vacío")
        private String name;

        @NotBlank(message = "El email no puede estar vacío")
        @Email(message = "Debe ser una dirección de email válida")
        private String email;

        @NotBlank(message = "La contraseña no puede estar vacía")
        @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
        private String password;
    }

    @Data
    static class LoginRequest {
        @NotBlank(message = "El email es requerido")
        @Email(message = "Formato de email inválido")
        private String email;

        @NotBlank(message = "La contraseña es requerida")
        private String password;
    }
}
