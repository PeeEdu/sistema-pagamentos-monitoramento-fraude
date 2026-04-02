package com.user.service;

import com.user.entities.UserEntity;
import com.user.repository.UserRepository;
import com.user.stub.UserEntityStub;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    private final UserRepository userRepository;
    private final CustomUserDetailsService customUserDetailsService;

    CustomUserDetailsServiceTest() {
        this.userRepository = mock(UserRepository.class);
        this.customUserDetailsService = new CustomUserDetailsService(userRepository);
    }

    @Test
    void loadUserByUsername_DeveRetornarUserDetails_QuandoUsuarioExistirEEstiverAtivo() {
        UserEntity user = UserEntityStub.buildEntity();

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        UserDetails response = customUserDetailsService.loadUserByUsername(user.getEmail());

        assertNotNull(response);
        assertEquals(user.getEmail(), response.getUsername());
        assertEquals(user.getPassword(), response.getPassword());
        assertTrue(response.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_USER")));
        assertTrue(response.isAccountNonExpired());
        assertTrue(response.isAccountNonLocked());
        assertTrue(response.isCredentialsNonExpired());
        assertTrue(response.isEnabled());

        verify(userRepository).findByEmail(user.getEmail());
    }

    @Test
    void loadUserByUsername_DeveRetornarUserDetailsComContaBloqueadaEDesabilitada_QuandoUsuarioEstiverInativo() {
        UserEntity user = UserEntityStub.buildInactiveEntity();

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        UserDetails response = customUserDetailsService.loadUserByUsername(user.getEmail());

        assertNotNull(response);
        assertEquals(user.getEmail(), response.getUsername());
        assertEquals(user.getPassword(), response.getPassword());
        assertTrue(response.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_USER")));
        assertTrue(response.isAccountNonExpired());
        assertFalse(response.isAccountNonLocked());
        assertTrue(response.isCredentialsNonExpired());
        assertFalse(response.isEnabled());

        verify(userRepository).findByEmail(user.getEmail());
    }

    @Test
    void loadUserByUsername_DeveLancarUsernameNotFoundException_QuandoUsuarioNaoExistir() {
        String email = "usuario.inexistente@email.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername(email)
        );

        assertEquals("User not found with email: " + email, exception.getMessage());

        verify(userRepository).findByEmail(email);
    }
}