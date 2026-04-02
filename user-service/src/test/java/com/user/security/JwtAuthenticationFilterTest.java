package com.user.security;

import com.user.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final FilterChain filterChain;
    private final TestableJwtAuthenticationFilter jwtAuthenticationFilter;

    JwtAuthenticationFilterTest() {
        this.jwtUtil = mock(JwtUtil.class);
        this.userDetailsService = mock(UserDetailsService.class);
        this.filterChain = mock(FilterChain.class);
        this.jwtAuthenticationFilter = new TestableJwtAuthenticationFilter(jwtUtil, userDetailsService);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_DeveIgnorarFiltro_QuandoUrlForPublica() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setRequestURI("/api/auth/login");

        jwtAuthenticationFilter.executeFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtUtil);
        verifyNoInteractions(userDetailsService);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_DeveContinuarChain_QuandoAuthorizationNaoForInformado() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setRequestURI("/api/user");

        jwtAuthenticationFilter.executeFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtUtil);
        verifyNoInteractions(userDetailsService);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_DeveContinuarChain_QuandoAuthorizationNaoComecarComBearer() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setRequestURI("/api/user");
        request.addHeader("Authorization", "Basic abc123");

        jwtAuthenticationFilter.executeFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtUtil);
        verifyNoInteractions(userDetailsService);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_DeveContinuarChain_QuandoTokenForInvalido() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setRequestURI("/api/user");
        request.addHeader("Authorization", "Bearer token-invalido");

        when(jwtUtil.validateToken("token-invalido")).thenReturn(false);

        jwtAuthenticationFilter.executeFilter(request, response, filterChain);

        verify(jwtUtil).validateToken("token-invalido");
        verify(filterChain).doFilter(request, response);
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_DeveAutenticarUsuario_QuandoTokenForValidoESecurityContextEstiverVazio() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setRequestURI("/api/user");
        request.addHeader("Authorization", "Bearer token-valido");

        UserDetails userDetails = new User(
                "joao.silva@email.com",
                "senha",
                List.of()
        );

        when(jwtUtil.validateToken("token-valido")).thenReturn(true);
        when(jwtUtil.extractEmail("token-valido")).thenReturn("joao.silva@email.com");
        when(userDetailsService.loadUserByUsername("joao.silva@email.com")).thenReturn(userDetails);

        jwtAuthenticationFilter.executeFilter(request, response, filterChain);

        verify(jwtUtil).validateToken("token-valido");
        verify(jwtUtil).extractEmail("token-valido");
        verify(userDetailsService).loadUserByUsername("joao.silva@email.com");
        verify(filterChain).doFilter(request, response);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("joao.silva@email.com", SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Test
    void doFilterInternal_DeveNaoAutenticarNovamente_QuandoSecurityContextJaPossuirAuthentication() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setRequestURI("/api/user");
        request.addHeader("Authorization", "Bearer token-valido");

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("usuario.existente", null, List.of());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(jwtUtil.validateToken("token-valido")).thenReturn(true);
        when(jwtUtil.extractEmail("token-valido")).thenReturn("joao.silva@email.com");

        jwtAuthenticationFilter.executeFilter(request, response, filterChain);

        verify(jwtUtil).validateToken("token-valido");
        verify(jwtUtil).extractEmail("token-valido");
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(filterChain).doFilter(request, response);

        assertEquals("usuario.existente", SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Test
    void doFilterInternal_DeveContinuarChain_QuandoOcorrerExcecaoAoProcessarToken() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setRequestURI("/api/user");
        request.addHeader("Authorization", "Bearer token-valido");

        when(jwtUtil.validateToken("token-valido")).thenThrow(new RuntimeException("Erro ao validar token"));

        jwtAuthenticationFilter.executeFilter(request, response, filterChain);

        verify(jwtUtil).validateToken("token-valido");
        verify(filterChain).doFilter(request, response);
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    static class TestableJwtAuthenticationFilter extends JwtAuthenticationFilter {

        public TestableJwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
            super(jwtUtil, userDetailsService);
        }

        public void executeFilter(
                MockHttpServletRequest request,
                MockHttpServletResponse response,
                FilterChain filterChain
        ) throws ServletException, IOException {
            super.doFilterInternal(request, response, filterChain);
        }
    }
}