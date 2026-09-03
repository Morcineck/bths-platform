package com.bths.platform.security;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UsuarioDetailsService usuarioDetailsService;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @AfterEach
    void limparSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void deveContinuarFiltroSemAutenticarQuandoAuthorizationNaoExistir()
            throws Exception {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        jwtAuthenticationFilter.doFilter(
                request,
                response,
                filterChain
        );

        assertNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );

        verify(filterChain)
                .doFilter(request, response);

        verifyNoInteractions(
                jwtService,
                usuarioDetailsService
        );
    }

    @Test
    void deveAutenticarUsuarioQuandoTokenForValido()
            throws Exception {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader(
                "Authorization",
                "Bearer token-valido"
        );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        UserDetails usuario = mock(UserDetails.class);

        when(jwtService.extrairEmail("token-valido"))
                .thenReturn("admin@beattrips.com");

        when(usuarioDetailsService.loadUserByUsername(
                "admin@beattrips.com"
        )).thenReturn(usuario);

        when(usuario.isEnabled())
                .thenReturn(true);

        when(jwtService.tokenValido(
                "token-valido",
                usuario
        )).thenReturn(true);

        var authorities = List.of(
                new SimpleGrantedAuthority("ROLE_ADMIN")
        );

        doReturn(authorities)
                .when(usuario)
                .getAuthorities();

        jwtAuthenticationFilter.doFilter(
                request,
                response,
                filterChain
        );

        var authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        assertNotNull(authentication);
        assertEquals(usuario, authentication.getPrincipal());
        assertEquals(
                "ROLE_ADMIN",
                authentication.getAuthorities()
                        .iterator()
                        .next()
                        .getAuthority()
        );

        verify(jwtService)
                .extrairEmail("token-valido");

        verify(usuarioDetailsService)
                .loadUserByUsername("admin@beattrips.com");

        verify(jwtService)
                .tokenValido("token-valido", usuario);

        verify(filterChain)
                .doFilter(request, response);
    }

    @Test
    void naoDeveAutenticarUsuarioQuandoEstiverDesativado()
            throws Exception {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader(
                "Authorization",
                "Bearer token-valido"
        );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        UserDetails usuario = mock(UserDetails.class);

        when(jwtService.extrairEmail("token-valido"))
                .thenReturn("staff@beattrips.com");

        when(usuarioDetailsService.loadUserByUsername(
                "staff@beattrips.com"
        )).thenReturn(usuario);

        when(usuario.isEnabled())
                .thenReturn(false);

        jwtAuthenticationFilter.doFilter(
                request,
                response,
                filterChain
        );

        assertNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );

        verify(jwtService)
                .extrairEmail("token-valido");

        verify(usuarioDetailsService)
                .loadUserByUsername("staff@beattrips.com");

        verify(jwtService, never())
                .tokenValido(anyString(), any(UserDetails.class));

        verify(filterChain)
                .doFilter(request, response);
    }

    @Test
    void naoDeveAutenticarUsuarioQuandoTokenForInvalido()
            throws Exception {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader(
                "Authorization",
                "Bearer token-invalido"
        );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        UserDetails usuario = mock(UserDetails.class);

        when(jwtService.extrairEmail("token-invalido"))
                .thenReturn("staff@beattrips.com");

        when(usuarioDetailsService.loadUserByUsername(
                "staff@beattrips.com"
        )).thenReturn(usuario);

        when(usuario.isEnabled())
                .thenReturn(true);

        when(jwtService.tokenValido(
                "token-invalido",
                usuario
        )).thenReturn(false);

        jwtAuthenticationFilter.doFilter(
                request,
                response,
                filterChain
        );

        assertNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );

        verify(jwtService)
                .extrairEmail("token-invalido");

        verify(usuarioDetailsService)
                .loadUserByUsername("staff@beattrips.com");

        verify(jwtService)
                .tokenValido("token-invalido", usuario);

        verify(filterChain)
                .doFilter(request, response);
    }

    @Test
    void deveLimparContextoEContinuarFiltroQuandoOcorrerExcecao()
            throws Exception {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.addHeader(
                "Authorization",
                "Bearer token-malformado"
        );

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        when(jwtService.extrairEmail("token-malformado"))
                .thenThrow(new RuntimeException("Token inválido"));

        jwtAuthenticationFilter.doFilter(
                request,
                response,
                filterChain
        );

        assertNull(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );

        verify(jwtService)
                .extrairEmail("token-malformado");

        verifyNoInteractions(usuarioDetailsService);

        verify(filterChain)
                .doFilter(request, response);
    }

}