package com.bths.platform.security;

import com.bths.platform.security.dto.UsuarioAutenticadoResponse;
import com.bths.platform.security.exception.CredenciaisInvalidasException;
import com.bths.platform.security.dto.LoginRequest;
import com.bths.platform.security.dto.LoginResponse;
import com.bths.platform.usuario.Usuario;
import com.bths.platform.usuario.UsuarioRepository;
import com.bths.platform.usuario.exception.UsuarioNaoEncontradoException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;

    public AuthService(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UsuarioRepository usuarioRepository
    ) {

        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
    }

    public LoginResponse login(LoginRequest request) {

        try {

        Authentication  authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getSenha()
                )
        );

        UserDetails usuario =
                (UserDetails) authentication.getPrincipal();

        String token = jwtService.gerarToken(usuario);

        return new LoginResponse(token);

    } catch (AuthenticationException exception) {
        throw new CredenciaisInvalidasException(
                "E-mail ou senha inválidos!"
        );
        }
    }

    public UsuarioAutenticadoResponse buscarUsuarioAutenticado(String email) {

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(()->
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
