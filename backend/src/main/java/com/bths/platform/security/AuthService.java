package com.bths.platform.security;

import com.bths.platform.security.dto.UsuarioAutenticadoResponse;
import com.bths.platform.security.exception.CredenciaisInvalidasException;
import com.bths.platform.security.dto.LoginRequest;
import com.bths.platform.security.dto.LoginResponse;
import com.bths.platform.security.dto.TrocarSenhaRequest;
import com.bths.platform.security.exception.TrocaSenhaInvalidaException;
import com.bths.platform.security.exception.LoginBloqueadoException;
import com.bths.platform.usuario.Usuario;
import com.bths.platform.usuario.UsuarioRepository;
import com.bths.platform.usuario.exception.UsuarioNaoEncontradoException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private static final Logger log =
            LoggerFactory.getLogger(AuthService.class);

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;
    private final LimiteTentativasLogin limiteTentativasLogin;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UsuarioRepository usuarioRepository,
            LimiteTentativasLogin limiteTentativasLogin,
            PasswordEncoder passwordEncoder
    ) {

        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
        this.limiteTentativasLogin = limiteTentativasLogin;
        this.passwordEncoder = passwordEncoder;
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
            limiteTentativasLogin.registrarFalha(chave).ifPresent(bloqueio ->
                    log.warn(
                            "Login bloqueado por excesso de tentativas falhas: conta={}, bloqueioSegundos={}.",
                            mascarar(chave),
                            bloqueio.toSeconds()
                    )
            );
            throw new CredenciaisInvalidasException(
                    "E-mail ou senha inválidos!"
            );
        }
    }

    private String mascarar(String chave) {
        int arroba = chave.indexOf('@');
        if (arroba < 1) {
            return "***";
        }
        return chave.charAt(0) + "***" + chave.substring(arroba);
    }

    public UsuarioAutenticadoResponse buscarUsuarioAutenticado(String email) {

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsuarioNaoEncontradoException("Usuário não encontrado!")
                );

        UsuarioAutenticadoResponse response = new UsuarioAutenticadoResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getPerfil()
        );
        response.setTrocaSenhaObrigatoria(usuario.isTrocaSenhaObrigatoria());

        return response;
    }

    @Transactional
    public void trocarSenha(String email, TrocarSenhaRequest request) {

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsuarioNaoEncontradoException("Usuário não encontrado!")
                );

        if (!passwordEncoder.matches(request.getSenhaAtual(), usuario.getSenha())) {
            throw new TrocaSenhaInvalidaException("A senha atual está incorreta!");
        }

        if (passwordEncoder.matches(request.getNovaSenha(), usuario.getSenha())) {
            throw new TrocaSenhaInvalidaException("A nova senha deve ser diferente da senha atual!");
        }

        usuario.setSenha(passwordEncoder.encode(request.getNovaSenha()));
        usuario.setTrocaSenhaObrigatoria(false);

        usuarioRepository.save(usuario);
    }
}
