package com.bths.platform.usuario;

import com.bths.platform.usuario.exception.EmailUsuarioJaCadastradoException;
import com.bths.platform.usuario.exception.UsuarioNaoEncontradoException;
import com.bths.platform.usuario.dto.UsuarioRequest;
import com.bths.platform.usuario.dto.UsuarioResponse;
import com.bths.platform.usuario.mapper.UsuarioMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            UsuarioMapper usuarioMapper,
            PasswordEncoder passwordEncoder
    ) {

        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public UsuarioResponse cadastrarUsuario(UsuarioRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new EmailUsuarioJaCadastradoException(
                    "Já existe um usuário cadastrado com esse e-mail!"
            );
        }

        Usuario usuario = new Usuario();

        usuario.setNome(request.getNome());
        usuario.setEmail(request.getEmail());
        usuario.setSenha(passwordEncoder.encode(request.getSenha()));
        usuario.setPerfil(request.getPerfil());
        usuario.setAtivo(true);

        Usuario salvo = usuarioRepository.save(usuario);

        return usuarioMapper.paraResponse(salvo);
    }

    public UsuarioResponse buscarUsuarioPorId(UUID id) {

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(
                                "Usuario não encontrado!"
                        )
                );
        return usuarioMapper.paraResponse(usuario);
    }
}
