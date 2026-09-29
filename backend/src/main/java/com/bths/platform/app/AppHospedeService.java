package com.bths.platform.app;

import com.bths.platform.alocacao.AlocacaoQuartoRepository;
import com.bths.platform.app.dto.MeuCheckInResponse;
import com.bths.platform.app.dto.MeuTrasladoResponse;
import com.bths.platform.app.dto.MinhaViagemResponse;
import com.bths.platform.hospede.Hospede;
import com.bths.platform.hospede.HospedeRepository;
import com.bths.platform.operacaoTraslado.OperacaoTraslado;
import com.bths.platform.qrcode.QrCodeGeradorService;
import com.bths.platform.traslado.Traslado;
import com.bths.platform.traslado.TrasladoRepository;
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
    private final TrasladoRepository trasladoRepository;
    private final QrCodeGeradorService qrCodeGeradorService;

    public AppHospedeService(
            UsuarioRepository usuarioRepository,
            HospedeRepository hospedeRepository,
            AlocacaoQuartoRepository alocacaoQuartoRepository,
            TrasladoRepository trasladoRepository,
            QrCodeGeradorService qrCodeGeradorService
    ) {

        this.usuarioRepository = usuarioRepository;
        this.hospedeRepository = hospedeRepository;
        this.alocacaoQuartoRepository = alocacaoQuartoRepository;
        this.trasladoRepository = trasladoRepository;
        this.qrCodeGeradorService = qrCodeGeradorService;

    }

    public MinhaViagemResponse buscarMinhaViagem(
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

    public List<MeuTrasladoResponse> buscarMeusTraslados(
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
            return List.of();
        }

        Hospede hospede = hospedes.get(0);

        return trasladoRepository
                .findByHospedeId(hospede.getId())
                .stream()
                .map(this::paraMeuTrasladoResponse)
                .toList();
    }

    private MeuTrasladoResponse paraMeuTrasladoResponse(
            Traslado traslado
    ) {

        MeuTrasladoResponse response =
                new MeuTrasladoResponse();

        response.setId(traslado.getId());

        response.setNumeroVoo(
                traslado.getNumeroVoo()
        );

        response.setCompanhiaAerea(
                traslado.getCompanhiaAerea()
        );

        OperacaoTraslado operacao =
                traslado.getOperacaoTraslado();

        if (operacao != null) {

            response.setTipo(
                    operacao.getTipo()
            );

            response.setDataHoraPrevista(
                    operacao.getDataHoraPrevista()
            );

            response.setLocalOrigem(
                    operacao.getLocalOrigem()
            );

            response.setLocalDestino(
                    operacao.getLocalDestino()
            );

            response.setAeroporto(
                    operacao.getAeroporto()
            );

            response.setStatus(
                    operacao.getStatus()
            );

            if (operacao.getMotorista() != null) {
                response.setMotoristaNome(
                        operacao.getMotorista()
                                .getNomeCompleto()
                );
            }

            if (operacao.getVeiculo() != null) {
                response.setVeiculoModelo(
                        operacao.getVeiculo()
                                .getModelo()
                );

                response.setVeiculoPlaca(
                        operacao.getVeiculo()
                                .getPlaca()
                );
            }

            response.setOrientacaoHospede(
                    operacao.getOrientacaoHospede()
            );

        } else {

            response.setTipo(
                    traslado.getTipo()
            );

            response.setDataHoraPrevista(
                    traslado.getDataHoraPrevista()
            );

            response.setLocalOrigem(
                    traslado.getLocalOrigem()
            );

            response.setLocalDestino(
                    traslado.getLocalDestino()
            );

            response.setAeroporto(
                    traslado.getAeroporto()
            );

            response.setStatus(
                    traslado.getStatus()
            );

            if (traslado.getMotorista() != null) {
                response.setMotoristaNome(
                        traslado.getMotorista()
                                .getNomeCompleto()
                );
            }

            if (traslado.getVeiculo() != null) {
                response.setVeiculoModelo(
                        traslado.getVeiculo()
                                .getModelo()
                );

                response.setVeiculoPlaca(
                        traslado.getVeiculo()
                                .getPlaca()
                );
            }

            response.setOrientacaoHospede(
                    traslado.getOrientacaoHospede()
            );
        }

        return response;
    }

    public MeuCheckInResponse buscarMeuCheckIn(
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

        MeuCheckInResponse response =
                new MeuCheckInResponse();

        response.setHospedeNome(
                hospede.getNomeCompleto()
        );

        response.setStatusCheckIn(
                hospede.getStatusCheckIn()
        );

        response.setDataHoraCheckIn(
                hospede.getDataHoraCheckIn()
        );

        return response;
    }

    public byte[] buscarMeuQrCode(
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

        if (hospede.getCodigoCheckIn() == null
                || hospede.getCodigoCheckIn().isBlank()) {
            return null;
        }

        return qrCodeGeradorService.gerarQRCode(
                hospede.getCodigoCheckIn()
        );
    }

}
