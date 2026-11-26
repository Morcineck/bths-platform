package com.bths.platform.usuario;

import com.bths.platform.usuario.dto.UsuarioAtualizarRequest;
import com.bths.platform.usuario.dto.UsuarioRedefinirSenhaRequest;
import com.bths.platform.usuario.dto.UsuarioRequest;
import com.bths.platform.usuario.dto.UsuarioResponse;
import com.bths.platform.usuario.enums.PerfilUsuario;
import com.bths.platform.usuario.exception.EmailUsuarioJaCadastradoException;
import com.bths.platform.usuario.exception.OperacaoUsuarioNaoPermitidaException;
import com.bths.platform.usuario.exception.PerfilUsuarioInvalidoException;
import com.bths.platform.usuario.exception.UsuarioNaoEncontradoException;
import com.bths.platform.usuario.mapper.UsuarioMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;
    private static final List<PerfilUsuario> PERFIS_EQUIPE =
            List.of(PerfilUsuario.ADMIN, PerfilUsuario.STAFF);

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
        validarPerfilDaEquipe(request.getPerfil());
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
                                "Usuário não encontrado!"
                        )
                );
        return usuarioMapper.paraResponse(usuario);
    }

    public List<UsuarioResponse> listarEquipe() {
        return usuarioRepository.findByPerfilInOrderByNomeAsc(PERFIS_EQUIPE)
                .stream()
                .map(usuarioMapper::paraResponse)
                .toList();
    }

    public UsuarioResponse atualizarUsuario(UUID id, UsuarioAtualizarRequest request) {
        Usuario usuario = buscarUsuarioDaEquipe(id);

        validarPerfilDaEquipe(request.perfil());

        if (usuarioRepository.existsByEmailAndIdNot(request.email(), id)) {
            throw new EmailUsuarioJaCadastradoException(
                    "Já existe um usuário cadastrado com esse e-mail!"
            );
        }

        boolean deixaDeSerAdmin =
                usuario.getPerfil() == PerfilUsuario.ADMIN
                        && request.perfil() != PerfilUsuario.ADMIN;

        if (deixaDeSerAdmin && usuario.isAtivo()) {
            validarQueNaoEhUltimoAdminAtivo();
        }

        usuario.setNome(request.nome());
        usuario.setEmail(request.email());
        usuario.setPerfil(request.perfil());

        return usuarioMapper.paraResponse(usuarioRepository.save(usuario));
    }

    public UsuarioResponse desativarUsuario(UUID id, String emailUsuarioLogado) {
        Usuario usuario = buscarUsuarioDaEquipe(id);

        if (usuario.getEmail().equalsIgnoreCase(emailUsuarioLogado)) {
            throw new OperacaoUsuarioNaoPermitidaException(
                    "Você não pode desativar a sua própria conta."
            );
        }

        usuario.setAtivo(false);

        return usuarioMapper.paraResponse(usuarioRepository.save(usuario));
    }

    public UsuarioResponse reativarUsuario(UUID id) {
        Usuario usuario = buscarUsuarioDaEquipe(id);

        usuario.setAtivo(true);
        return usuarioMapper.paraResponse(usuarioRepository.save(usuario));
    }

    public void redefinirSenha(UUID id, UsuarioRedefinirSenhaRequest request) {
        Usuario usuario = buscarUsuarioDaEquipe(id);

        usuario.setSenha(passwordEncoder.encode(request.novaSenha()));

        usuarioRepository.save(usuario);
    }

    private Usuario buscarUsuarioDaEquipe(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(
                        "Usuário não encontrado!"
                ));

        validarPerfilDaEquipe(usuario.getPerfil());

        return usuario;
    }

    private void validarQueNaoEhUltimoAdminAtivo() {
        if (usuarioRepository.countByPerfilAndAtivoTrue(PerfilUsuario.ADMIN) <= 1) {
            throw new OperacaoUsuarioNaoPermitidaException(
                    "O sistema precisa manter pelo menos um ADMIN ativo."
            );
        }
    }

    private void validarPerfilDaEquipe(PerfilUsuario perfil) {
        if (!PERFIS_EQUIPE.contains(perfil)) {
            throw new PerfilUsuarioInvalidoException(
                    "Contas de hóspede são gerenciadas pelo acesso BTHS no detalhe do hóspede."
            );
        }
    }
}
