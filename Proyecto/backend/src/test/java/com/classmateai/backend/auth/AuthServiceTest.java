package com.classmateai.backend.auth; 

// Importaciones de JUnit y Mockito (Las herramientas)
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

// Importaciones del proyecto
import com.classmateai.backend.entity.User;
import com.classmateai.backend.exception.EmailAlreadyExistsException;
import com.classmateai.backend.exception.InvalidCredentialsException;
import com.classmateai.backend.repository.UserRepository;

// Importaciones estáticas para verificar 
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) 
class AuthServiceTest {

    @Mock 
    private UserRepository userRepository;

    @Mock 
    private PasswordEncoder passwordEncoder;

    @InjectMocks 
    private AuthService authService; 

    // --- Variables que usaremos en las pruebas ---
    private AuthController.RegisterRequest registerRequest;
    private AuthController.LoginRequest loginRequest;
    private User mockUser;

    @BeforeEach //
    void setUp() {
        // Preparamos un DTO de registro de prueba
        registerRequest = new AuthController.RegisterRequest();
        registerRequest.setName("Test User");
        registerRequest.setEmail("test@correo.com");
        registerRequest.setPassword("password123");

        // Preparamos un DTO de login de prueba
        loginRequest = new AuthController.LoginRequest();
        loginRequest.setEmail("test@correo.com");
        loginRequest.setPassword("password123");

        // Preparamos un Usuario FALSO que simula estar en la BD
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("test@correo.com");
        mockUser.setPasswordHash("hashedPassword123"); // Contraseña hasheada
    }

    // --- PRUEBA 1: AuthService - Registro OK  ---
    @Test
    void testRegister_WhenNewUser_ShouldSaveUser() {
  
        when(userRepository.existsByEmail("test@correo.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword123");
        authService.register(registerRequest);
        verify(userRepository, times(1)).save(any(User.class));
    }

    // --- PRUEBA 2: AuthService - Email Existe  ---
    @Test
    void testRegister_WhenEmailAlreadyExists_ShouldThrowException() {
    
        when(userRepository.existsByEmail("test@correo.com")).thenReturn(true);
        assertThrows(EmailAlreadyExistsException.class, () -> {
            authService.register(registerRequest); 
        });
        verify(userRepository, never()).save(any(User.class));
    }

    // --- PRUEBA 3: AuthService - Login Inválido (Usuario No Existe)  ---
    @Test
    void testLogin_WhenUserNotFound_ShouldThrowException() {
  
        when(userRepository.findByEmail("test@correo.com")).thenReturn(Optional.empty());
        assertThrows(InvalidCredentialsException.class, () -> {
            authService.login(loginRequest); 
        });
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }
}