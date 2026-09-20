package com.bths.platform.security;

import com.bths.platform.security.dto.UsuarioAutenticadoResponse;
import com.bths.platform.security.exception.CredenciaisInvalidasException;
import com.bths.platform.security.dto.LoginRequest;
import com.bths.platform.security.dto.LoginResponse;
import com.bths.platform.usuario.Usuario;
import com.bths.platform.usuario.UsuarioRepository;
import com.bths.platform.usuario.enums.PerfilUsuario;
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

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UsuarioRepository usuarioRepository;

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

    @Test
    void deveBuscarUsuarioAutenticado() {

        UUID id = UUID.randomUUID();

        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setNome("Administrador Beat Trips");
        usuario.setEmail("admin@beattrips.com");
        usuario.setPerfil(PerfilUsuario.ADMIN);
        usuario.setAtivo(true);

        when(usuarioRepository.findByEmail(
                "admin@beattrips.com"
        )).thenReturn(Optional.of(usuario));

        UsuarioAutenticadoResponse response =
                authService.buscarUsuarioAutenticado(
                        "admin@beattrips.com"
                );

        assertNotNull(response);
        assertEquals(id, response.getId());
        assertEquals(
                "Administrador Beat Trips",
                response.getNome()
        );
        assertEquals(
                "admin@beattrips.com",
                response.getEmail()
        );
        assertEquals(
                PerfilUsuario.ADMIN,
                response.getPerfil()
        );

        verify(usuarioRepository)
                .findByEmail("admin@beattrips.com");
    }
}
