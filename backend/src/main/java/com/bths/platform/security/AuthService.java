package com.bths.platform.security;

import com.bths.platform.security.dto.UsuarioAutenticadoResponse;
import com.bths.platform.security.exception.CredenciaisInvalidasException;
import com.bths.platform.security.dto.LoginRequest;
import com.bths.platform.security.dto.LoginResponse;
import com.bths.platform.security.exception.LoginBloqueadoException;
import com.bths.platform.usuario.Usuario;
import com.bths.platform.usuario.UsuarioRepository;
import com.bths.platform.usuario.exception.UsuarioNaoEncontradoException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final Logger log =
            LoggerFactory.getLogger(AuthService.class);

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;
    private final LimiteTentativasLogin limiteTentativasLogin;

    public AuthService(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UsuarioRepository usuarioRepository,
            LimiteTentativasLogin limiteTentativasLogin
    ) {

        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
        this.limiteTentativasLogin = limiteTentativasLogin;
    }

    public LoginResponse login(LoginRequest request) {

        String chave = LimiteTentativasLogin.normalizarChave(request.getEmail());

        limiteTentativasLogin.tempoRestanteDeBloqueio(chave)
                .ifPresent(tempoRestante -> {
                    throw new LoginBloqueadoException(tempoRestante);
                });

        try {

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getSenha()
                    )
            );

            UserDetails usuario =
                    (UserDetails) authentication.getPrincipal();

            String token = jwtService.gerarToken(usuario);

            limiteTentativasLogin.registrarSucesso(chave);

            return new LoginResponse(token);

        } catch (InternalAuthenticationServiceException exception) {
            log.error("Falha interna ao autenticar o usuário.", exception);
            throw new CredenciaisInvalidasException(
                    "E-mail ou senha inválidos!"
            );

        } catch (AuthenticationException exception) {
            limiteTentativasLogin.registrarFalha(chave);
            throw new CredenciaisInvalidasException(
                    "E-mail ou senha inválidos!"
            );
        }
    }

    public UsuarioAutenticadoResponse buscarUsuarioAutenticado(String email) {

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsuarioNaoEncontradoException("Usuário não encontrado!")
                );

        return new UsuarioAutenticadoResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getPerfil()
        );
    }
}
