package com.user.util;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", "minha-chave-secreta-para-jwt-com-pelo-menos-32-bytes");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 86400000L);
    }

    @Test
    void generateToken_DeveRetornarTokenValido_QuandoInformarDadosDoUsuario() {
        String token = jwtUtil.generateToken("user-123", "joao.silva@email.com", "João Silva");

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void validateToken_DeveRetornarTrue_QuandoTokenForValido() {
        String token = jwtUtil.generateToken("user-123", "joao.silva@email.com", "João Silva");

        boolean response = jwtUtil.validateToken(token);

        assertTrue(response);
    }

    @Test
    void validateToken_DeveRetornarFalse_QuandoTokenForInvalido() {
        boolean response = jwtUtil.validateToken("token-invalido");

        assertFalse(response);
    }

    @Test
    void validateToken_DeveRetornarFalse_QuandoTokenEstiverExpirado() throws InterruptedException {
        ReflectionTestUtils.setField(jwtUtil, "expiration", 1L);

        String token = jwtUtil.generateToken("user-123", "joao.silva@email.com", "João Silva");

        Thread.sleep(10);

        boolean response = jwtUtil.validateToken(token);

        assertFalse(response);
    }

    @Test
    void extractEmail_DeveRetornarEmail_QuandoTokenForValido() {
        String token = jwtUtil.generateToken("user-123", "joao.silva@email.com", "João Silva");

        String email = jwtUtil.extractEmail(token);

        assertEquals("joao.silva@email.com", email);
    }

    @Test
    void extractUserId_DeveRetornarUserId_QuandoTokenForValido() {
        String token = jwtUtil.generateToken("user-123", "joao.silva@email.com", "João Silva");

        String userId = jwtUtil.extractUserId(token);

        assertEquals("user-123", userId);
    }

    @Test
    void extractName_DeveRetornarName_QuandoTokenForValido() {
        String token = jwtUtil.generateToken("user-123", "joao.silva@email.com", "João Silva");

        String name = jwtUtil.extractName(token);

        assertEquals("João Silva", name);
    }

    @Test
    void extractExpiration_DeveRetornarDataDeExpiracao_QuandoTokenForValido() {
        String token = jwtUtil.generateToken("user-123", "joao.silva@email.com", "João Silva");

        Date expiration = jwtUtil.extractExpiration(token);

        assertNotNull(expiration);
        assertTrue(expiration.after(new Date(System.currentTimeMillis() - 1000)));
    }

    @Test
    void extractClaim_DeveRetornarClaimPersonalizada_QuandoTokenForValido() {
        String token = jwtUtil.generateToken("user-123", "joao.silva@email.com", "João Silva");

        String userId = jwtUtil.extractClaim(token, claims -> claims.get("userId", String.class));
        String name = jwtUtil.extractClaim(token, claims -> claims.get("name", String.class));
        String subject = jwtUtil.extractClaim(token, Claims::getSubject);

        assertEquals("user-123", userId);
        assertEquals("João Silva", name);
        assertEquals("joao.silva@email.com", subject);
    }

    @Test
    void extractEmail_DeveLancarException_QuandoTokenForInvalido() {
        Exception exception = assertThrows(
                Exception.class,
                () -> jwtUtil.extractEmail("token-invalido")
        );

        assertNotNull(exception);
    }

    @Test
    void extractUserId_DeveLancarException_QuandoTokenForInvalido() {
        Exception exception = assertThrows(
                Exception.class,
                () -> jwtUtil.extractUserId("token-invalido")
        );

        assertNotNull(exception);
    }

    @Test
    void extractName_DeveLancarException_QuandoTokenForInvalido() {
        Exception exception = assertThrows(
                Exception.class,
                () -> jwtUtil.extractName("token-invalido")
        );

        assertNotNull(exception);
    }

    @Test
    void extractExpiration_DeveLancarException_QuandoTokenForInvalido() {
        Exception exception = assertThrows(
                Exception.class,
                () -> jwtUtil.extractExpiration("token-invalido")
        );

        assertNotNull(exception);
    }

    @Test
    void extractClaim_DeveLancarException_QuandoTokenForInvalido() {
        Exception exception = assertThrows(
                Exception.class,
                () -> jwtUtil.extractClaim("token-invalido", Claims::getSubject)
        );

        assertNotNull(exception);
    }
}