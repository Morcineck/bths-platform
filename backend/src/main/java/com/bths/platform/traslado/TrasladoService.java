package com.bths.platform.traslado;

import com.bths.platform.alocacao.exception.ViagemIncompativelException;
import com.bths.platform.hospede.Hospede;
import com.bths.platform.hospede.HospedeRepository;
import com.bths.platform.hospede.exception.HospedeNaoEncontradoException;
import com.bths.platform.motorista.Motorista;
import com.bths.platform.motorista.MotoristaRepository;
import com.bths.platform.motorista.exception.MotoristaNaoEncontradoException;
import com.bths.platform.traslado.dto.*;
import com.bths.platform.traslado.enums.Aeroporto;
import com.bths.platform.traslado.enums.StatusTraslado;
import com.bths.platform.traslado.enums.TipoTraslado;
import com.bths.platform.traslado.exception.*;
import com.bths.platform.traslado.mapper.HistoricoStatusTrasladoMapper;
import com.bths.platform.traslado.mapper.TrasladoMapper;
import com.bths.platform.veiculo.Veiculo;
import com.bths.platform.veiculo.VeiculoRepository;
import com.bths.platform.veiculo.exception.VeiculoNaoEncontradoException;
import com.bths.platform.viagem.Viagem;
import com.bths.platform.viagem.ViagemRepository;
import com.bths.platform.viagem.exception.ViagemNaoEncontradaException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TrasladoService {

    private final TrasladoRepository trasladoRepository;
    private final HospedeRepository hospedeRepository;
    private final ViagemRepository viagemRepository;
    private final TrasladoMapper trasladoMapper;
    private final HistoricoStatusTrasladoRepository historicoStatusTrasladoRepository;
    private final HistoricoStatusTrasladoMapper historicoStatusTrasladoMapper;
    private final VeiculoRepository veiculoRepository;
    private final MotoristaRepository motoristaRepository;

    public TrasladoService(
            TrasladoRepository trasladoRepository,
            HospedeRepository hospedeRepository,
            ViagemRepository viagemRepository,
            TrasladoMapper trasladoMapper,
            HistoricoStatusTrasladoRepository historicoStatusTrasladoRepository,
            HistoricoStatusTrasladoMapper historicoStatusTrasladoMapper,
            MotoristaRepository motoristaRepository,
            VeiculoRepository veiculoRepository
    ) {

        this.trasladoRepository = trasladoRepository;
        this.hospedeRepository = hospedeRepository;
        this.viagemRepository = viagemRepository;
        this.trasladoMapper = trasladoMapper;
        this.historicoStatusTrasladoRepository = historicoStatusTrasladoRepository;
        this.historicoStatusTrasladoMapper = historicoStatusTrasladoMapper;
        this.motoristaRepository = motoristaRepository;
        this.veiculoRepository = veiculoRepository;
    }

    public TrasladoResponse cadastrarTraslado(TrasladoRequest request) {

        Hospede hospede = hospedeRepository.findById(request.getHospedeId())
                .orElseThrow(() -> new HospedeNaoEncontradoException(
                        "Hóspede não encontrado!"
                ));

        Viagem viagem = viagemRepository.findById(request.getViagemId())
                .orElseThrow(() -> new ViagemNaoEncontradaException(
                        "Viagem não encontrada!"
                ));

        if (!hospede.getViagem().getId().equals(viagem.getId())) {
            throw new ViagemIncompativelException(
                    "O hóspede não pertence à viagem informada!"
            );
        }


        validarAeroporto(request.getTipo(),
                request.getAeroporto());

        Traslado traslado = new Traslado();

        traslado.setHospede(hospede);
        traslado.setViagem(viagem);
        traslado.setTipo(request.getTipo());
        traslado.setAeroporto(request.getAeroporto());
        traslado.setStatus(StatusTraslado.AGUARDANDO);
        traslado.setDataHoraPrevista(request.getDataHoraPrevista());
        traslado.setNumeroVoo(request.getNumeroVoo());
        traslado.setCompanhiaAerea(request.getCompanhiaAerea());
        traslado.setLocalOrigem(request.getLocalOrigem());
        traslado.setLocalDestino(request.getLocalDestino());
        traslado.setObservacao(request.getObservacoes());

        Traslado salvo = trasladoRepository.save(traslado);

        return trasladoMapper.paraResponse(salvo);
    }

    private void validarAeroporto(TipoTraslado tipo, Aeroporto aeroporto) {

        boolean envolveAeroporto =
                tipo == TipoTraslado.AEROPORTO_PARA_HOSPEDAGEM
                        || tipo == TipoTraslado.HOSPEDAGEM_PARA_AEROPORTO;

        if (envolveAeroporto && aeroporto == null) {
            throw new AeroportoObrigatorioException(
                    "O aeroporto é obrigatório para este tipo de traslado!"
            );
        }
    }

    public TrasladoResponse buscarTrasladoPorId(Long id) {

        Traslado traslado = trasladoRepository.findById(id)
                .orElseThrow(() -> new TrasladoNaoEncontradoException(
                        "Traslado não encontrado!"
                ));

        return trasladoMapper.paraResponse(traslado);
    }

    public List<TrasladoResponse> listarTrasladosPorViagem(Long viagemId) {

        Viagem viagem = viagemRepository.findById(viagemId)
                .orElseThrow(() -> new ViagemNaoEncontradaException(
                        "Viagem não encontrada!"
                ));

        return trasladoRepository.findByViagemId(viagem.getId())
                .stream()
                .map(trasladoMapper::paraResponse)
                .toList();
    }

    public List<TrasladoResponse> listarTrasladosPorHospede(Long hospedeId) {

        Hospede hospede = hospedeRepository.findById(hospedeId)
                .orElseThrow(() -> new HospedeNaoEncontradoException(
                        "Hóspede não encontrado!"
                ));

        return trasladoRepository.findByHospedeId(hospede.getId())
                .stream()
                .map(trasladoMapper::paraResponse)
                .toList();
    }

    public TrasladoResponse atualizarTraslado(
            Long id,
            TrasladoUpdateRequest request
    ) {

        Traslado traslado = trasladoRepository.findById(id)
                .orElseThrow(() -> new TrasladoNaoEncontradoException(
                        "Traslado não encontrado!"
                ));

        validarAeroporto(
                request.getTipo()
                , request.getAeroporto()
        );

        validarCompatibilidadeComOperacaoVinculada(
                traslado,
                request
        );

        traslado.setTipo(request.getTipo());
        traslado.setAeroporto(request.getAeroporto());
        traslado.setDataHoraPrevista(request.getDataHoraPrevista());
        traslado.setNumeroVoo(request.getNumeroVoo());
        traslado.setCompanhiaAerea(request.getCompanhiaAerea());
        traslado.setLocalOrigem(request.getLocalOrigem());
        traslado.setLocalDestino(request.getLocalDestino());
        traslado.setObservacao(request.getObservacoes());

        Traslado atualizado =
                trasladoRepository.save(traslado);

        return trasladoMapper.paraResponse(atualizado);

    }

    public void validarCompatibilidadeComOperacaoVinculada(
            Traslado traslado,
            TrasladoUpdateRequest request
    ) {

        if (traslado.getOperacaoTraslado() == null) {
            return;
        }

        if (
                traslado.getOperacaoTraslado()
                        .getTipo()
                        != request.getTipo()
        ) {

            throw new ViagemIncompativelException(
                    "Não é possível alterar o tipo do traslado porque ele está vinculado a uma operação compartilhada incompatível."
            );
        }
        if (
                request.getTipo()
                        != TipoTraslado.OUTRO
                        &&
                        traslado.getOperacaoTraslado()
                                .getAeroporto()
                                != request.getAeroporto()
        ) {

            throw new ViagemIncompativelException(
                    "Não é possível alterar o aeroporto porque ele está vinculado a uma operação compartilhada incompatível."
            );
        }
    }

    @Transactional
    public TrasladoResponse corrigirStatusTraslado(
            Long id,
            TrasladoCorrecaoStatusRequest request
    ) {

        Traslado traslado = trasladoRepository.findById(id)
                .orElseThrow(() -> new TrasladoNaoEncontradoException(
                        "Traslado não encontrado!"
                ));

        validarCorrecaoStatus(
                traslado.getStatus(),
                request.getStatus(),
                request.getMotivo()
        );

        StatusTraslado statusAnterior = traslado.getStatus();

        registrarHistoricoStatusTraslado(
                traslado,
                statusAnterior,
                request.getStatus(),
                request.getMotivo()
        );


        traslado.setStatus(request.getStatus());

        Traslado atualizado = trasladoRepository.save(traslado);

        return trasladoMapper.paraResponse(atualizado);

    }

    @Transactional
    public TrasladoResponse atualizarStatusTraslado(
            Long id,
            TrasladoStatusRequest request
    ) {

        Traslado traslado = trasladoRepository.findById(id)
                .orElseThrow(() -> new TrasladoNaoEncontradoException(
                        "Traslado não encontrado!"
                ));

        validarStatusTransicao(
                traslado.getStatus(),
                request.getStatus()
        );

        StatusTraslado statusAnterior = traslado.getStatus();

        registrarHistoricoStatusTraslado(
                traslado,
                statusAnterior,
                request.getStatus(),
                "Alteração normal de status"
        );

        traslado.setStatus(request.getStatus());

        Traslado atualizado = trasladoRepository.save(traslado);

        return trasladoMapper.paraResponse(atualizado);

    }

    private void validarStatusTransicao(
            StatusTraslado statusAtual,
            StatusTraslado novoStatus
    ) {

        boolean transicaoValida = switch (statusAtual) {

            case AGUARDANDO -> novoStatus == StatusTraslado.EM_ANDAMENTO
                    || novoStatus == StatusTraslado.CANCELADO;

            case EM_ANDAMENTO -> novoStatus == StatusTraslado.CONCLUIDO
                    || novoStatus == StatusTraslado.CANCELADO;


            case CONCLUIDO, CANCELADO -> false;
        };

        if (!transicaoValida) {
            throw new TransicaoStatusTrasladoInvalidaException(
                    "Não é possível alterar o status de "
                            + statusAtual
                            + " para "
                            + novoStatus
                            + "!"
            );
        }
    }

    private void validarCorrecaoStatus(
            StatusTraslado statusAtual,
            StatusTraslado novoStatus,
            String motivo
    ) {

        if (motivo == null || motivo.isBlank()) {
            throw new MotivoCorrecaoObrigatorioException(
                    "O motivo da correção é obrigatório!"
            );
        }

        boolean correcaoValida =
                (statusAtual == StatusTraslado.CONCLUIDO
                        && novoStatus == StatusTraslado.EM_ANDAMENTO)
                        ||
                        (statusAtual == StatusTraslado.CANCELADO
                                && novoStatus == StatusTraslado.AGUARDANDO);

        if (!correcaoValida) {
            throw new TransicaoStatusTrasladoInvalidaException(
                    "Não é possível alterar o status de "
                            + statusAtual
                            + " para "
                            + novoStatus
                            + "!"
            );
        }
    }

    public List<HistoricoStatusTrasladoResponse> listarHistoricoStatusTraslado(
            Long trasladoId
    ) {

        Traslado traslado = trasladoRepository.findById(trasladoId)
                .orElseThrow(() -> new TrasladoNaoEncontradoException(
                        "Traslado não encontrado!"
                ));

        return historicoStatusTrasladoRepository
                .findByTrasladoId(traslado.getId())
                .stream()
                .map(historicoStatusTrasladoMapper::paraResponse)
                .toList();
    }

    private void registrarHistoricoStatusTraslado(
            Traslado traslado,
            StatusTraslado statusAnterior,
            StatusTraslado novoStatus,
            String motivo
    ) {

        HistoricoStatusTraslado historico = new HistoricoStatusTraslado();

        historico.setTraslado(traslado);
        historico.setStatusAnterior(statusAnterior);
        historico.setNovoStatus(novoStatus);
        historico.setMotivo(motivo);
        historico.setDataHora(LocalDateTime.now());

        historicoStatusTrasladoRepository.save(historico);
    }

    public TrasladoResponse associarOperacao(
            Long id,
            TrasladoOperacaoRequest request
    ) {

        Traslado traslado =
                trasladoRepository.findById(id)
                        .orElseThrow(() -> new TrasladoNaoEncontradoException(
                                        "Tralado nao encontrado!"
                                )
                        );

        Motorista motorista =
                motoristaRepository.findById(
                        request.getMotoristaId()
                ).orElseThrow(() ->
                        new MotoristaNaoEncontradoException(
                                "Motorista não encontrado!"
                        )
                );

        Veiculo veiculo =
                veiculoRepository.findById(
                        request.getVeiculoId()
                ).orElseThrow(() ->
                        new VeiculoNaoEncontradoException(
                                "Veículo não encontrado!"
                        )
                );

        if (!motorista.isAtivo()) {
            throw new MotoristaInativoException(
                    "Não é possível associar motorista inativo!"
            );
        }

        if (!veiculo.isAtivo()) {
            throw new VeiculoInativoException(
                    "Não é possível associar veiculo inativo!"
            );
        }

        traslado.setMotorista(motorista);
        traslado.setVeiculo(veiculo);

        Traslado atualizado =
                trasladoRepository.save(traslado);

        return trasladoMapper.paraResponse(atualizado);
    }
}
