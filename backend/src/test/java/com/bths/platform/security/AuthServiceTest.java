package com.bths.platform.security;

import com.bths.platform.security.dto.UsuarioAutenticadoResponse;
import com.bths.platform.security.exception.CredenciaisInvalidasException;
import com.bths.platform.security.dto.LoginRequest;
import com.bths.platform.security.dto.LoginResponse;
import com.bths.platform.security.exception.LoginBloqueadoException;
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
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.InternalAuthenticationServiceException;

import java.time.Duration;
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

    @Mock
    private LimiteTentativasLogin limiteTentativasLogin;

    @InjectMocks
    private AuthService authService;

    private ListAppender<ILoggingEvent> appender;
    private Logger loggerDoServico;

    @BeforeEach
    void capturarLogs() {
        loggerDoServico = (Logger) LoggerFactory.getLogger(AuthService.class);
        appender = new ListAppender<>();
        appender.start();
        loggerDoServico.addAppender(appender);
    }

    @AfterEach
    void liberarLogs() {
        loggerDoServico.detachAppender(appender);
    }

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

    @Test
    void deveRegistrarErroQuandoFalharInfraestruturaNoLogin() {

        LoginRequest request = new LoginRequest();
        request.setEmail("admin@beattrips.com");
        request.setSenha("senha123");

        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        )).thenThrow(
                new InternalAuthenticationServiceException("banco fora do ar")
        );

        assertThrows(
                CredenciaisInvalidasException.class,
                () -> authService.login(request)
        );

        assertEquals(1, appender.list.size());
        assertEquals(Level.ERROR, appender.list.get(0).getLevel());
        assertNotNull(appender.list.get(0).getThrowableProxy());
        assertFalse(appender.list.get(0).getFormattedMessage().contains("senha123"));
    }

    @Test
    void naoDeveRegistrarErroQuandoCredenciaisForemApenasInvalidas() {

        LoginRequest request = new LoginRequest();
        request.setEmail("admin@beattrips.com");
        request.setSenha("senha-errada");

        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        )).thenThrow(new BadCredentialsException("credenciais invalidas"));

        assertThrows(
                CredenciaisInvalidasException.class,
                () -> authService.login(request)
        );

        assertTrue(appender.list.isEmpty());
    }

    @Test
    void deveRegistrarFalhaQuandoCredenciaisForemInvalidas() {

        LoginRequest request = new LoginRequest();
        request.setEmail("admin@beattrips.com");
        request.setSenha("senha-errada");

        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        )).thenThrow(new BadCredentialsException("credenciais invalidas"));

        assertThrows(
                CredenciaisInvalidasException.class,
                () -> authService.login(request)
        );

        verify(limiteTentativasLogin).registrarFalha("admin@beattrips.com");
        verify(limiteTentativasLogin, never()).registrarSucesso(any());
    }

    @Test
    void naoDeveContarFalhaQuandoHouverFalhaDeInfraestrutura() {

        LoginRequest request = new LoginRequest();
        request.setEmail("admin@beattrips.com");
        request.setSenha("senha123");

        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        )).thenThrow(
                new InternalAuthenticationServiceException("banco fora do ar")
        );

        assertThrows(
                CredenciaisInvalidasException.class,
                () -> authService.login(request)
        );

        verify(limiteTentativasLogin, never()).registrarFalha(any());
    }

    @Test
    void deveZerarAContagemQuandoOLoginForBemSucedido() {

        LoginRequest request = new LoginRequest();
        request.setEmail("admin@beattrips.com");
        request.setSenha("senha123");

        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        )).thenReturn(authentication);

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtService.gerarToken(userDetails)).thenReturn("jwt-token-gerado");

        authService.login(request);

        verify(limiteTentativasLogin).registrarSucesso("admin@beattrips.com");
        verify(limiteTentativasLogin, never()).registrarFalha(any());
    }

    @Test
    void deveBloquearSemAutenticarQuandoOLimiteEstiverExcedido() {

        LoginRequest request = new LoginRequest();
        request.setEmail("admin@beattrips.com");
        request.setSenha("senha123");

        when(limiteTentativasLogin.tempoRestanteDeBloqueio("admin@beattrips.com"))
                .thenReturn(Optional.of(Duration.ofMinutes(3)));

        LoginBloqueadoException exception = assertThrows(
                LoginBloqueadoException.class,
                () -> authService.login(request)
        );

        assertEquals(Duration.ofMinutes(3), exception.getTempoRestante());
        verifyNoInteractions(authenticationManager);
        verify(limiteTentativasLogin, never()).registrarFalha(any());
    }

    @Test
    void deveNormalizarOEmailAntesDeConsultarOLimite() {

        LoginRequest request = new LoginRequest();
        request.setEmail("  Admin@BeatTrips.com ");
        request.setSenha("senha-errada");

        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        )).thenThrow(new BadCredentialsException("credenciais invalidas"));

        assertThrows(
                CredenciaisInvalidasException.class,
                () -> authService.login(request)
        );

        verify(limiteTentativasLogin).tempoRestanteDeBloqueio("admin@beattrips.com");
        verify(limiteTentativasLogin).registrarFalha("admin@beattrips.com");
    }
}
