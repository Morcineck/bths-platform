package com.bths.platform.security;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


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

    private ListAppender<ILoggingEvent> appender;
    private Logger loggerDoFiltro;

    @BeforeEach
    void capturarLogs() {
        loggerDoFiltro = (Logger) LoggerFactory.getLogger(JwtAuthenticationFilter.class);
        appender = new ListAppender<>();
        appender.start();
        loggerDoFiltro.addAppender(appender);
    }

    @AfterEach
    void liberarLogs() {
        loggerDoFiltro.detachAppender(appender);
    }

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

    @Test
    void deveRegistrarInfoQuandoTokenEstiverExpirado()
            throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer token-expirado");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtService.extrairEmail("token-expirado"))
                .thenThrow(new ExpiredJwtException(null, null, "expirado"));

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);

        assertEquals(1, appender.list.size());
        assertEquals(Level.INFO, appender.list.get(0).getLevel());
        assertEquals("Token JWT expirado.", appender.list.get(0).getFormattedMessage());
    }

    @Test
    void naoDeveExibirTokenNoLogQuandoTokenForRejeitado()
            throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer token-segredo-123");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtService.extrairEmail("token-segredo-123"))
                .thenThrow(new JwtException("assinatura invalida"));

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);

        assertEquals(1, appender.list.size());
        assertEquals(Level.WARN, appender.list.get(0).getLevel());

        String mensagem = appender.list.get(0).getFormattedMessage();
        assertFalse(mensagem.contains("token-segredo-123"));
        assertFalse(mensagem.contains("assinatura invalida"));
        assertTrue(mensagem.contains("JwtException"));
        assertNull(appender.list.get(0).getThrowableProxy());
    }

    @Test
    void deveRegistrarErroQuandoFalharInfraestrutura()
            throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer token-valido");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtService.extrairEmail("token-valido"))
                .thenReturn("hospede@teste.com");

        when(usuarioDetailsService.loadUserByUsername("hospede@teste.com"))
                .thenThrow(new DataAccessResourceFailureException("banco fora do ar"));

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);

        assertEquals(1, appender.list.size());
        assertEquals(Level.ERROR, appender.list.get(0).getLevel());
        assertNotNull(appender.list.get(0).getThrowableProxy());
    }

}