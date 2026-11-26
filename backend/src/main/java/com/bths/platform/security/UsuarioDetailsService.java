package com.bths.platform.security;

import com.bths.platform.usuario.Usuario;
import com.bths.platform.usuario.UsuarioRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    public static final String AUTORIDADE_TROCA_SENHA_OBRIGATORIA = "TROCA_SENHA_OBRIGATORIA";

    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado!")
                );

        List<SimpleGrantedAuthority> autoridades = new ArrayList<>();
        autoridades.add(new SimpleGrantedAuthority("ROLE_" + usuario.getPerfil().name()));

        if (usuario.isTrocaSenhaObrigatoria()) {
            autoridades.add(new SimpleGrantedAuthority(AUTORIDADE_TROCA_SENHA_OBRIGATORIA));
        }

        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getSenha())
                .authorities(autoridades)
                .disabled(!usuario.isAtivo())
                .build();
    }
}
