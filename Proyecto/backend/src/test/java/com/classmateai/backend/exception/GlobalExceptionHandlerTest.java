package com.classmateai.backend.exception;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleEmailAlreadyExists_ShouldReturnConflictStatus() {
        EmailAlreadyExistsException ex =
                new EmailAlreadyExistsException("Email ya registrado");

        ResponseEntity<Map<String, String>> response =
                handler.handleEmailAlreadyExists(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Email ya registrado", response.getBody().get("error"));
    }

    @Test
    void handleInvalidCredentials_ShouldReturnUnauthorizedStatus() {
        InvalidCredentialsException ex =
                new InvalidCredentialsException("Credenciales inválidas");

        ResponseEntity<Map<String, String>> response =
                handler.handleInvalidCredentials(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Credenciales inválidas", response.getBody().get("error"));
    }

    @Test
    void handleResourceNotFound_ShouldReturnNotFoundStatus() {
        ResourceNotFoundException ex =
                new ResourceNotFoundException("Recurso no encontrado");

        ResponseEntity<Map<String, String>> response =
                handler.handleResourceNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Recurso no encontrado", response.getBody().get("error"));
    }

    @Test
    void handleAccessDenied_ShouldReturnForbiddenStatus() {
        AccessDeniedException ex = new AccessDeniedException("denied");

        ResponseEntity<Map<String, String>> response =
                handler.handleAccessDenied(ex);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("No tienes permiso para acceder a este recurso.",
                response.getBody().get("error"));
    }

    @Test
    void handleValidationExceptions_ShouldReturnBadRequestWithErrors() {

        // Mock del BindingResult
        BindingResult bindingResult = Mockito.mock(BindingResult.class);
        MethodArgumentNotValidException ex =
                new MethodArgumentNotValidException(null, bindingResult);

        // Field errors simulados
        FieldError error1 = new FieldError("user", "email", "Email inválido");
        FieldError error2 = new FieldError("user", "password", "Contraseña requerida");

        Mockito.when(bindingResult.getFieldErrors())
                .thenReturn(List.of(error1, error2));

        ResponseEntity<Map<String, String>> response =
                handler.handleValidationExceptions(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        Map<String, String> body = response.getBody();
        assertEquals("Email inválido", body.get("email"));
        assertEquals("Contraseña requerida", body.get("password"));
    }

    @Test
    void handleHttpMediaTypeNotSupported_ShouldReturn415() {
        HttpMediaTypeNotSupportedException ex =
                new HttpMediaTypeNotSupportedException("application/xml no soportado");

        ResponseEntity<Map<String, String>> response =
                handler.handleHttpMediaTypeNotSupported(ex);

        assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, response.getStatusCode());
        assertTrue(response.getBody().get("error")
                .contains("application/xml no soportado"));
    }

    @Test
    void handleGeneralException_ShouldReturn500() {
        Exception ex = new Exception("Falla inesperada");

        ResponseEntity<Map<String, String>> response =
                handler.handleGeneralException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error interno del servidor: Falla inesperada",
                response.getBody().get("error"));
    }
}
