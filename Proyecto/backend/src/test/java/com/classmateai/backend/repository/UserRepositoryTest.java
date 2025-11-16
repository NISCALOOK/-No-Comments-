package com.classmateai.backend.repository;

import com.classmateai.backend.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest // Configura una base de datos en memoria (H2) solo para este test
@DisplayName("Tests para UserRepository - Método existsByEmail")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager; // Nos ayuda a insertar datos de prueba fácilmente

    @Autowired
    private UserRepository userRepository; // El repositorio que vamos a probar

    private User testUser;

    @BeforeEach
    void setUp() {
        // Preparamos un objeto usuario antes de cada test
        testUser = new User();
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPasswordHash("hashedPassword");
        // Nota: No lo guardamos aquí todavía, lo guardamos dentro de cada test según se necesite
    }

    @Test
    @DisplayName("existsByEmail - Email existente retorna true")
    void existsByEmail_ExistingEmail_ReturnsTrue() {
        // Arrange: Guardamos el usuario en la BD simulada
        entityManager.persistAndFlush(testUser);

        // Act: Ejecutamos el método del repositorio preguntando por ese email
        boolean exists = userRepository.existsByEmail("test@example.com");

        // Assert: Verificamos que el resultado sea verdadero
        assertTrue(exists, "Debería retornar true porque acabamos de guardar ese usuario");
    }

    @Test
    @DisplayName("existsByEmail - Email no existente retorna false")
    void existsByEmail_NonExistingEmail_ReturnsFalse() {
        // Arrange: Guardamos un usuario (con email "test@example.com")
        entityManager.persistAndFlush(testUser);

        // Act: Preguntamos por un email DIFERENTE que no está en la BD
        boolean exists = userRepository.existsByEmail("nonexistent@example.com");

        // Assert: Verificamos que el resultado sea falso
        assertFalse(exists, "Debería retornar false porque ese email no existe en la BD");
    }
}
