package com.bths.platform.app;

import com.bths.platform.alocacao.AlocacaoQuartoRepository;
import com.bths.platform.app.dto.MinhaViagemResponse;
import com.bths.platform.hospede.Hospede;
import com.bths.platform.hospede.HospedeRepository;
import com.bths.platform.usuario.Usuario;
import com.bths.platform.usuario.UsuarioRepository;
import com.bths.platform.usuario.enums.PerfilUsuario;
import com.bths.platform.usuario.exception.UsuarioNaoEncontradoException;
import com.bths.platform.viagem.Viagem;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppHospedeService {

    private final UsuarioRepository usuarioRepository;
    private final HospedeRepository hospedeRepository;
    private final AlocacaoQuartoRepository alocacaoQuartoRepository;

    public AppHospedeService(
            UsuarioRepository usuarioRepository,
            HospedeRepository hospedeRepository,
            AlocacaoQuartoRepository alocacaoQuartoRepository
    ) {

        this.usuarioRepository = usuarioRepository;
        this.hospedeRepository = hospedeRepository;
        this.alocacaoQuartoRepository = alocacaoQuartoRepository;

    }

    public MinhaViagemResponse buscarMinhaViagem(
            String email
    ) {

        Usuario usuario =
                usuarioRepository
                        .findByEmail(email)
                        .orElseThrow(()->
                                new UsuarioNaoEncontradoException(
                                        "Usuário não encontrado!"
                                )
                        );

        if (usuario.getPerfil() != PerfilUsuario.HOSPEDE) {
            throw new IllegalArgumentException(
                    "A consulta é permitida apenas para usuários com perfil HOSPEDE."
            );
        }

        List<Hospede> hospedes =
                hospedeRepository.findByUsuarioId(
                        usuario.getId()
                );

        if (hospedes.isEmpty()) {
            return null;
        }

        Hospede hospede = hospedes.get(0);
        Viagem viagem = hospede.getViagem();

        MinhaViagemResponse response =
                new MinhaViagemResponse();

        response.setHospedeId(
                hospede.getId()
        );

        response.setHospedeNome(
                hospede.getNomeCompleto()
        );

        response.setViagemId(
                viagem.getId()
        );

        response.setViagemNome(
                viagem.getNome()
        );

        response.setEvento(
                viagem.getEvento()
        );

        response.setDataInicio(
                viagem.getDataInicio()
        );

        response.setDataFim(
                viagem.getDataFim()
        );

        response.setEndereco(
                viagem.getEndereco()
        );

        response.setCidade(
                viagem.getCidade()
        );

        response.setEstado(
                viagem.getEstado()
        );

        response.setStatus(
                viagem.getStatus()
        );

        alocacaoQuartoRepository
                .findByHospedeIdAndViagemId(
                        hospede.getId(),
                        viagem.getId()
                )
                .ifPresent(alocacao -> {

                    response.setQuartoId(
                            alocacao.getQuarto().getId()
                    );

                    response.setQuartoNome(
                            alocacao.getQuarto().getNome()
                    );
                });

        return response;
    }

}
