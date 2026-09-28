package com.bths.platform.hospede;

import com.bths.platform.hospede.dto.HospedeAcessoBthsResponse;
import com.bths.platform.hospede.dto.HospedeCriarAcessoBthsRequest;
import com.bths.platform.hospede.dto.HospedeRequest;
import com.bths.platform.hospede.dto.HospedeResponse;
import com.bths.platform.hospede.exception.HospedeJaVinculadoException;
import com.bths.platform.hospede.mapper.HospedeMapper;
import com.bths.platform.usuario.Usuario;
import com.bths.platform.usuario.UsuarioRepository;
import com.bths.platform.usuario.enums.PerfilUsuario;
import com.bths.platform.usuario.exception.UsuarioNaoEncontradoException;
import com.bths.platform.viagem.Viagem;
import com.bths.platform.viagem.ViagemRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.bths.platform.viagem.exception.ViagemNaoEncontradaException;
import com.bths.platform.hospede.exception.HospedeJaCadastradoException;
import com.bths.platform.hospede.exception.HospedeNaoEncontradoException;

import java.util.List;
import java.util.UUID;

@Service
public class HospedeService {

    private final HospedeRepository hospedeRepository;
    private final ViagemRepository viagemRepository;
    private final HospedeMapper hospedeMapper;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public HospedeService(
            HospedeRepository hospedeRepository,
            ViagemRepository viagemRepository,
            HospedeMapper hospedeMapper,
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.hospedeRepository = hospedeRepository;
        this.viagemRepository = viagemRepository;
        this.hospedeMapper = hospedeMapper;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;

    }

    public HospedeResponse cadastrarHospede(HospedeRequest request) {

        Viagem viagem = viagemRepository.findById(request.getViagemId())
                .orElseThrow(() ->
                        new ViagemNaoEncontradaException("Viagem não encontrada!")
                );
        if (hospedeRepository.existsByCpfAndViagemId(
                request.getCpf(),
                request.getViagemId()
        )) {
            throw new HospedeJaCadastradoException(
                    "CPF já cadastrado nesta viagem!"
            );
        }

        Hospede hospede = new Hospede();
        hospedeMapper.atualizarEntidade(hospede, request);

        hospede.setViagem(viagem);

        hospede.setCodigoCheckIn(
                UUID.randomUUID().toString()
        );

        Hospede hospedeSalvo =
                hospedeRepository.save(hospede);

        return hospedeMapper.paraResponse(hospedeSalvo);
    }

    public HospedeResponse buscarHospedePorId(Long id) {

        Hospede hospede = hospedeRepository.findById(id)
                .orElseThrow(() -> new HospedeNaoEncontradoException(
                                "Hóspede não encontrado!"
                        )
                );
        return hospedeMapper.paraResponse(hospede);
    }

    public List<HospedeResponse> listarHospedes() {

        return hospedeRepository.findAll()
                .stream()
                .map(hospedeMapper::paraResponse)
                .toList();
    }

    public HospedeResponse atualizarHospede(Long id,
                                            HospedeRequest request
    ) {

        Hospede hospede = hospedeRepository.findById(id)
                .orElseThrow(() -> new HospedeNaoEncontradoException(
                                "Hóspede não encontrado!"
                        )
                );

        Viagem viagem = viagemRepository.findById(request.getViagemId())
                .orElseThrow(() ->
                        new ViagemNaoEncontradaException(
                                "Viagem não encontrada!"
                        )
                );

        boolean cpfJaCadastrado =
                hospedeRepository.existsByCpfAndViagemIdAndIdNot(
                        request.getCpf(),
                        request.getViagemId(),
                        id
                );

        if (cpfJaCadastrado) {
            throw new HospedeJaCadastradoException(
                    "CPF já cadastrado nesta viagem!"
            );
        }

        hospedeMapper.atualizarEntidade(hospede, request);

        hospede.setViagem(viagem);

        Hospede hospedeAtualizado = hospedeRepository.save(hospede);

        return hospedeMapper.paraResponse(hospedeAtualizado);
    }

    public void deletarHospede(Long id) {

        Hospede hospede = hospedeRepository.findById(id)
                .orElseThrow(() -> new HospedeNaoEncontradoException(
                                "Hóspede não encontrado!"
                        )
                );
        hospedeRepository.delete(hospede);
    }

    public List<HospedeResponse> listarHospedesPorViagem(
            Long viagemId

    ) {

        if (!viagemRepository.existsById(viagemId)) {
            throw new HospedeNaoEncontradoException(
                    "Viagem não encontrada!"
            );
        }

        return hospedeRepository.findByViagemId(viagemId)
                .stream()
                .map(hospedeMapper::paraResponse)
                .toList();
    }

    public HospedeResponse vincularUsuarioAoHospede(
            Long hospedeId,
            UUID usuarioId
    ) {

        Hospede hospede =
                hospedeRepository
                        .findById(hospedeId)
                        .orElseThrow(() -> new HospedeNaoEncontradoException(
                                        "Hóspede não encontrado!"
                                )
                        );
        if (hospede.getUsuario() != null) {
            throw new HospedeJaVinculadoException(
                    "Esse hóspede já possui uma conta BTHS vinculada."
            );
        }

        Usuario usuario =
                usuarioRepository
                        .findById(usuarioId)
                        .orElseThrow(() ->
                                new UsuarioNaoEncontradoException(
                                        "Usuário não encontrado!"
                                )
                        );

        if (
                usuario.getPerfil()
                        != PerfilUsuario.HOSPEDE
        ) {

            throw new IllegalArgumentException(
                    "Somente usuários com perfil HOSPEDE podem ser vinculados a um hóspede."
            );
        }

        hospede.setUsuario(
                usuario
        );

        Hospede atualizado =
                hospedeRepository.save(
                        hospede
                );

        return hospedeMapper.paraResponse(atualizado);
    }

    public List<HospedeResponse> buscarHospedesDoUsuario(
            String email
    ) {

        Usuario usuario =
                usuarioRepository
                        .findByEmail(email)
                        .orElseThrow(() ->
                                new UsuarioNaoEncontradoException(
                                        "Usuário não encontrado!"
                                )
                        );

        if (
                usuario.getPerfil()
                        != PerfilUsuario.HOSPEDE
        ) {
            throw new IllegalArgumentException(
                    "A consulta é permitida apenas para usuários com perfil HOSPEDE."
            );
        }

        return hospedeRepository
                .findByUsuarioId(
                        usuario.getId()
                )
                .stream()
                .map(
                        hospedeMapper::paraResponse
                )
                .toList();

    }

    public HospedeAcessoBthsResponse buscarAcessoBths(
            Long hospedeId
    ) {

        Hospede hospede =
                hospedeRepository
                        .findById(hospedeId)
                        .orElseThrow(() ->
                                new HospedeNaoEncontradoException(
                                        "Hóspede não encontrado!"
                                )
                        );

        HospedeAcessoBthsResponse response =
                new HospedeAcessoBthsResponse();

        if (hospede.getUsuario() == null) {

            response.setVinculado(false);
            response.setUsuarioId(null);
            response.setEmail(null);
            response.setAtivo(false);

            return response;
        }

        Usuario usuario =
                hospede.getUsuario();

        response.setVinculado(true);

        response.setUsuarioId(
                usuario.getId()
        );

        response.setEmail(
                usuario.getEmail()
        );

        response.setAtivo(
                usuario.isAtivo()
        );

        return response;
    }

    @Transactional
    public HospedeAcessoBthsResponse criarOuVincularAcessoBths(
            Long hospedeId,
            HospedeCriarAcessoBthsRequest request
    ) {

        Hospede hospede =
                hospedeRepository
                        .findById(hospedeId)
                        .orElseThrow(() ->
                                new HospedeNaoEncontradoException(
                                        "Hóspede não encontrado!"
                                )
                        );
        if (hospede.getUsuario() != null) {
            throw new HospedeJaVinculadoException(
                    "Esse hóspede já possui uma conta BTHS vinculada."
            );
        }

        Usuario usuario =
                usuarioRepository
                        .findByEmail(
                                request.getEmail()
                        )
                        .orElse(null);

        if (usuario == null) {

            usuario =
                    new Usuario();

            usuario.setNome(
                    hospede.getNomeCompleto()
            );

            usuario.setEmail(
                    request.getEmail()
            );

            usuario.setSenha(
                    passwordEncoder.encode(
                            request.getSenhaTemporaria()
                    )
            );

            usuario.setPerfil(
                    PerfilUsuario.HOSPEDE
            );

            usuario.setAtivo(
                    true
            );

            usuario =
                    usuarioRepository.save(
                            usuario
                    );

        } else if (
                usuario.getPerfil()
                        != PerfilUsuario.HOSPEDE
        ) {

            throw new IllegalArgumentException(
                    "O e-mail informado pertence a um usuário que não possui perfil HOSPEDE."
            );
        }

        hospede.setUsuario(
                usuario
        );

        hospedeRepository.save(
                hospede
        );

        HospedeAcessoBthsResponse response =
                new HospedeAcessoBthsResponse();

        response.setVinculado(
                true
        );

        response.setUsuarioId(
                usuario.getId()
        );

        response.setEmail(
                usuario.getEmail()
        );

        response.setAtivo(
                usuario.isAtivo()
        );

        return response;
    }
}


