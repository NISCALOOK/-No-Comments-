package com.classmateai.backend.auth;

import com.classmateai.backend.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private User mockUser;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();

        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setName("Test User");
        mockUser.setEmail("test@example.com");
    }

    @Test
    @DisplayName("Generar token correctamente")
    void generateToken_ShouldReturnValidJwt() {
        String token = jwtUtil.generateToken(mockUser);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    @DisplayName("Extraer username correctamente desde un token válido")
    void extractUsername_ShouldReturnCorrectEmail() {
        String token = jwtUtil.generateToken(mockUser);

        String username = jwtUtil.extractUsername(token);

        assertEquals("test@example.com", username);
    }

    @Test
    @DisplayName("Token malformado debe lanzar MalformedJwtException")
    void extractUsername_MalformedToken_ShouldThrowException() {
        String malformed = "this.is.not.a.jwt";

        assertThrows(MalformedJwtException.class, () -> {
            jwtUtil.extractUsername(malformed);
        });
    }

    @Test
    @DisplayName("Token expirado debe lanzar ExpiredJwtException")
    void extractUsername_ExpiredToken_ShouldThrowExpiredJwtException() {
        String expiredToken = Jwts.builder()
                .setSubject("test@example.com")
                .setExpiration(new Date(System.currentTimeMillis() - 1000)) // ya expirado
                .signWith(SignatureAlgorithm.HS256,
                        "EstaEsUnaClaveSecretaSuperLargaParaClassMateAIQueNadieDebeSaber12345")
                .compact();

        assertThrows(ExpiredJwtException.class, () -> {
            jwtUtil.extractUsername(expiredToken);
        });
    }

    @Test
    @DisplayName("El token contiene todos los claims correctos")
    void token_ShouldContainCorrectClaims() {
        String token = jwtUtil.generateToken(mockUser);

        Claims claims = Jwts.parser()
                .setSigningKey("EstaEsUnaClaveSecretaSuperLargaParaClassMateAIQueNadieDebeSaber12345")
                .parseClaimsJws(token)
                .getBody();

        assertEquals("test@example.com", claims.getSubject());
        assertEquals(1L, ((Number) claims.get("id")).longValue());
        assertEquals("Test User", claims.get("name"));
    }
}