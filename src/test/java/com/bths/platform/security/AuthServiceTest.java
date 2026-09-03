package com.bths.platform.security;

import com.bths.platform.exception.CredenciaisInvalidasException;
import com.bths.platform.security.dto.LoginRequest;
import com.bths.platform.security.dto.LoginResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private AuthService authService;

    @Test
    void deveRealizarLoginEGerarToken() {

        LoginRequest request = new LoginRequest();
        request.setEmail("admin@beattrips.com");
        request.setSenha("senha123");

        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        )).thenReturn(authentication);

        when(authentication.getPrincipal())
                .thenReturn(userDetails);

        when(jwtService.gerarToken(userDetails))
                .thenReturn("jwt-token-gerado");

        LoginResponse response =
                authService.login(request);

        assertNotNull(response);
        assertEquals("jwt-token-gerado", response.getToken());

        verify(authenticationManager).authenticate(
                argThat(auth ->
                        auth.getPrincipal().equals("admin@beattrips.com")
                                && auth.getCredentials().equals("senha123")
                )
        );

        verify(jwtService).gerarToken(userDetails);
    }

    @Test
    void deveLancarExcecaoQuandoCredenciaisForemInvalidas() {

        LoginRequest request = new LoginRequest();
        request.setEmail("admin@beattrips.com");
        request.setSenha("senha-incorreta");

        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        )).thenThrow(
                new BadCredentialsException("Credenciais inválidas")
        );

        CredenciaisInvalidasException exception =
                assertThrows(
                        CredenciaisInvalidasException.class,
                        () -> authService.login(request)
                );

        assertEquals(
                "E-mail ou senha inválidos!",
                exception.getMessage()
        );

        verify(authenticationManager).authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        );

        verify(jwtService, never())
                .gerarToken(any(UserDetails.class));
    }
}
