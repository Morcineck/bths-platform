package com.bths.platform.operacaoTraslado;

import com.bths.platform.alocacao.exception.ViagemIncompativelException;
import com.bths.platform.motorista.Motorista;
import com.bths.platform.motorista.MotoristaRepository;
import com.bths.platform.motorista.exception.MotoristaNaoEncontradoException;
import com.bths.platform.operacaoTraslado.dto.*;
import com.bths.platform.operacaoTraslado.execepion.CapacidadeVeiculoExcedidaException;
import com.bths.platform.operacaoTraslado.execepion.OperacaoTrasladoComPassageirosException;
import com.bths.platform.operacaoTraslado.execepion.OperacaoTrasladoNaoEncontradaException;
import com.bths.platform.operacaoTraslado.mapper.HistoricoStatusOperacaoTrasladoMapper;
import com.bths.platform.operacaoTraslado.mapper.OperacaoTrasladoMapper;
import com.bths.platform.traslado.Traslado;
import com.bths.platform.traslado.TrasladoRepository;
import com.bths.platform.traslado.enums.Aeroporto;
import com.bths.platform.traslado.enums.StatusTraslado;
import com.bths.platform.traslado.enums.TipoTraslado;
import com.bths.platform.traslado.exception.MotoristaInativoException;
import com.bths.platform.traslado.exception.TransicaoStatusTrasladoInvalidaException;
import com.bths.platform.traslado.exception.TrasladoNaoEncontradoException;
import com.bths.platform.traslado.exception.VeiculoInativoException;
import com.bths.platform.veiculo.Veiculo;
import com.bths.platform.veiculo.VeiculoRepository;
import com.bths.platform.veiculo.exception.VeiculoNaoEncontradoException;
import com.bths.platform.viagem.Viagem;
import com.bths.platform.viagem.ViagemRepository;
import com.bths.platform.viagem.exception.ViagemNaoEncontradaException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OperacaoTrasladoService {

    private final OperacaoTrasladoRepository operacaoTrasladoRepository;
    private final TrasladoRepository trasladoRepository;
    private final ViagemRepository viagemRepository;
    private final MotoristaRepository motoristaRepository;
    private final VeiculoRepository veiculoRepository;
    private final OperacaoTrasladoMapper operacaoTrasladoMapper;
    private final HistoricoStatusOperacaoTrasladoRepository historicoStatusOperacaoTrasladoRepository;
    private final HistoricoStatusOperacaoTrasladoMapper historicoStatusOperacaoTrasladoMapper;

    public OperacaoTrasladoService(
            OperacaoTrasladoRepository operacaoTrasladoRepository,
            TrasladoRepository trasladoRepository,
            ViagemRepository viagemRepository,
            MotoristaRepository motoristaRepository,
            VeiculoRepository veiculoRepository,
            OperacaoTrasladoMapper operacaoTrasladoMapper,
            HistoricoStatusOperacaoTrasladoRepository historicoStatusOperacaoTrasladoRepository,
            HistoricoStatusOperacaoTrasladoMapper historicoStatusOperacaoTrasladoMapper
    ) {

        this.operacaoTrasladoRepository =
                operacaoTrasladoRepository;

        this.trasladoRepository =
                trasladoRepository;

        this.viagemRepository =
                viagemRepository;

        this.motoristaRepository =
                motoristaRepository;

        this.veiculoRepository =
                veiculoRepository;

        this.operacaoTrasladoMapper =
                operacaoTrasladoMapper;

        this.historicoStatusOperacaoTrasladoRepository =
                historicoStatusOperacaoTrasladoRepository;

        this.historicoStatusOperacaoTrasladoMapper =
                historicoStatusOperacaoTrasladoMapper;
    }

    public OperacaoTrasladoResponse criarOperacao(
            OperacaoTrasladoRequest request
    ) {

        Viagem viagem =
                viagemRepository.findById(
                        request.getViagemId()
                ).orElseThrow(() ->
                        new ViagemNaoEncontradaException(
                                "Viagem não encontrada!"
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
                    "Não é possível criar uma operação com motorista inativo!"
            );
        }

        if (!veiculo.isAtivo()) {
            throw new VeiculoInativoException(
                    "Não é possível criar uma operação com veículo inativo!"
            );
        }

        OperacaoTraslado operacao =
                new OperacaoTraslado();

        operacao.setViagem(
                viagem
        );

        aplicarTrajetoConsistente(
                operacao,
                request.getTipo(),
                request.getAeroporto(),
                request.getLocalOrigem(),
                request.getLocalDestino()
        );

        operacao.setDataHoraPrevista(
                request.getDataHoraPrevista()
        );

        operacao.setMotorista(
                motorista
        );

        operacao.setVeiculo(
                veiculo
        );

        operacao.setObservacao(
                request.getObservacao()
        );

        OperacaoTraslado salva =
                operacaoTrasladoRepository.save(
                        operacao
                );

        return operacaoTrasladoMapper
                .paraResponse(
                        salva
                );
    }

    public OperacaoTrasladoResponse vincularTraslado(
            Long operacaoId,
            Long trasladoId
    ) {

        OperacaoTraslado operacao =
                operacaoTrasladoRepository.findById(
                        operacaoId
                ).orElseThrow(() ->
                        new OperacaoTrasladoNaoEncontradaException(
                                "Operação de traslado não encontrada!"
                        )
                );

        Traslado traslado =
                trasladoRepository.findById(
                        trasladoId
                ).orElseThrow(() ->
                        new TrasladoNaoEncontradoException(
                                "Traslado não encontrado!"
                        )
                );

        if (!traslado.getViagem()
                .getId()
                .equals(
                        operacao.getViagem()
                                .getId()
                )) {

            throw new ViagemIncompativelException(
                    "O traslado não pertence à viagem da operação!"
            );
        }

        if (
                traslado.getTipo()
                        != operacao.getTipo()
        ) {

            throw new ViagemIncompativelException(
                    "O tipo do traslado é incompatível com a operação!"
            );
        }

        if (
                traslado.getTipo()
                        != TipoTraslado.OUTRO
                        &&
                        traslado.getAeroporto()
                                != operacao.getAeroporto()
        ) {

            throw new ViagemIncompativelException(
                    "O aeroporto do traslado é incompatível com a operação!"
            );
        }

        if (
                traslado.getOperacaoTraslado()
                        != null
                        &&
                        traslado.getOperacaoTraslado()
                                .getId()
                                .equals(
                                        operacao.getId()
                                )
        ) {

            return operacaoTrasladoMapper
                    .paraResponse(
                            operacao
                    );
        }

        long quantidadePassageiros =
                trasladoRepository
                        .countByOperacaoTrasladoId(
                                operacao.getId()
                        );

        int capacidade =
                operacao.getVeiculo()
                        .getCapacidadePassageiros();

        if (
                quantidadePassageiros >=
                        capacidade
        ) {

            throw new CapacidadeVeiculoExcedidaException(
                    "Não há vagas disponíveis no veículo desta operação!"
            );
        }

        traslado.setOperacaoTraslado(
                operacao
        );

        trasladoRepository.save(
                traslado
        );

        return operacaoTrasladoMapper
                .paraResponse(
                        operacao
                );
    }

    public OperacaoTrasladoResponse alterarVeiculo(
            Long operacaoId,
            Long veiculoId
    ) {

        OperacaoTraslado operacao =
                operacaoTrasladoRepository.findById(
                        operacaoId
                ).orElseThrow(() ->
                        new OperacaoTrasladoNaoEncontradaException(
                                "Operação de traslado não encontrada!"
                        )
                );

        Veiculo veiculo =
                veiculoRepository.findById(
                        veiculoId
                ).orElseThrow(() ->
                        new VeiculoNaoEncontradoException(
                                "Veículo não encontrado!"
                        )
                );

        if (!veiculo.isAtivo()) {
            throw new VeiculoInativoException(
                    "Não é possível associar um veículo inativo!"
            );
        }

        long quantidadePassageiros =
                trasladoRepository
                        .countByOperacaoTrasladoId(
                                operacao.getId()
                        );

        if (
                quantidadePassageiros >
                        veiculo.getCapacidadePassageiros()
        ) {

            throw new CapacidadeVeiculoExcedidaException(
                    "A capacidade do novo veículo é menor que a quantidade de passageiros da operação!"
            );
        }

        operacao.setVeiculo(
                veiculo
        );

        OperacaoTraslado atualizada =
                operacaoTrasladoRepository.save(
                        operacao
                );

        return operacaoTrasladoMapper
                .paraResponse(
                        atualizada
                );
    }

    public List<OperacaoTrasladoResponse> listarPorViagem(
            Long viagemId
    ) {

        Viagem viagem =
                viagemRepository.findById(
                        viagemId
                ).orElseThrow(() ->
                        new ViagemNaoEncontradaException(
                                "Viagem não encontrada!"
                        )
                );

        return operacaoTrasladoRepository
                .findByViagemId(
                        viagem.getId()
                )
                .stream()
                .map(
                        operacaoTrasladoMapper::paraResponse
                )
                .toList();
    }

    public OperacaoTrasladoResponse atualizarOperacao(
            Long operacaoId,
            OperacaoTrasladoUpdateRequest request
    ) {

        OperacaoTraslado operacao =
                operacaoTrasladoRepository.findById(
                        operacaoId
                ).orElseThrow(() ->
                        new OperacaoTrasladoNaoEncontradaException(
                                "Operação de traslado não encontrada!"
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
                    "Não é possível atualizar a operação com motorista inativo!"
            );
        }

        if (!veiculo.isAtivo()) {
            throw new VeiculoInativoException(
                    "Não é possível atualizar a operação com veículo inativo!"
            );
        }

        long quantidadePassageiros =
                trasladoRepository
                        .countByOperacaoTrasladoId(
                                operacaoId
                        );

        if (
                veiculo.getCapacidadePassageiros()
                        < quantidadePassageiros
        ) {

            throw new CapacidadeVeiculoExcedidaException(
                    "A capacidade do veículo é menor que a quantidade de passageiros da operação!"
            );
        }

        validarCompatibilidadeComTrasladoVinculados(
                operacaoId,
                request.getTipo(),
                request.getAeroporto()
        );

        aplicarTrajetoConsistente(
                operacao,
                request.getTipo(),
                request.getAeroporto(),
                request.getLocalOrigem(),
                request.getLocalDestino()
        );

        operacao.setDataHoraPrevista(
                request.getDataHoraPrevista()
        );

        operacao.setMotorista(
                motorista
        );

        operacao.setVeiculo(
                veiculo
        );

        operacao.setObservacao(
                request.getObservacao()
        );

        OperacaoTraslado salva =
                operacaoTrasladoRepository.save(
                        operacao
                );

        return operacaoTrasladoMapper
                .paraResponse(
                        salva
                );
    }

    public OperacaoTrasladoResponse atualizarStatus(
            Long operacaoId,
            OperacaoTrasladoStatusRequest request
    ) {

        OperacaoTraslado operacao =
                operacaoTrasladoRepository.findById(
                        operacaoId
                ).orElseThrow(() ->
                        new OperacaoTrasladoNaoEncontradaException(
                                "Operação de traslado não encontrada!"
                        )
                );

        validarTransicaoStatus(
                operacao.getStatus(),
                request.getStatus()
        );

        StatusTraslado statusAnterior =
                operacao.getStatus();

        registarHistoricoStatus(
                operacao,
                statusAnterior,
                request.getStatus(),
                "Alteração normal de status"
        );

        operacao.setStatus(
                request.getStatus()
        );

        OperacaoTraslado atualizada =
                operacaoTrasladoRepository.save(
                        operacao
                );

        return operacaoTrasladoMapper
                .paraResponse(
                        atualizada
                );
    }

    public List<HistoricoStatusOperacaoTrasladoResponse> listarHistoricoStatus(
            Long operacaoId
    ) {

        OperacaoTraslado operacao =
                operacaoTrasladoRepository.findById(
                        operacaoId
                ).orElseThrow(() ->
                        new OperacaoTrasladoNaoEncontradaException(
                                "Operação de traslado não encontrada!"
                        )
                );

        return historicoStatusOperacaoTrasladoRepository
                .findByOperacaoTrasladoId(
                        operacao.getId()
                )
                .stream()
                .map(
                        historicoStatusOperacaoTrasladoMapper::paraResponse
                )
                .toList();
    }

    public OperacaoTrasladoResponse buscarPorId(
            Long id
    ) {

        OperacaoTraslado operacao =
                operacaoTrasladoRepository
                        .findById(
                                id
                        )
                        .orElseThrow(() ->
                                new OperacaoTrasladoNaoEncontradaException(
                                        "Operação de traslado não encontrada!"
                                )
                        );

        return operacaoTrasladoMapper
                .paraResponse(
                        operacao
                );
    }

    public void excluirOperacao(
            Long operacaoId
    ) {

        OperacaoTraslado operacao =
                operacaoTrasladoRepository
                        .findById(
                                operacaoId
                        )
                        .orElseThrow(() ->
                                new OperacaoTrasladoNaoEncontradaException(
                                        "Operação de traslado não encontrada!"
                                )
                        );

        long quantidadePassageiros =
                trasladoRepository
                        .countByOperacaoTrasladoId(
                                operacaoId
                        );

        if (
                quantidadePassageiros > 0
        ) {

            throw new OperacaoTrasladoComPassageirosException(
                    "Não é possível excluir uma operação que possui passageiros vinculados. Cancele a operação."
            );
        }

        operacaoTrasladoRepository.delete(
                operacao
        );
    }

    public List<OperacaoTrasladoPassageiroResponse> listarPassageiros(
            Long operacaoId
    ) {

        operacaoTrasladoRepository
                .findById(
                        operacaoId
                )
                .orElseThrow(() ->
                        new OperacaoTrasladoNaoEncontradaException(
                                "Operação de traslado não encontrada!"
                        )
                );

        return trasladoRepository
                .findByOperacaoTrasladoId(
                        operacaoId
                )
                .stream()
                .map(
                        traslado -> {

                            OperacaoTrasladoPassageiroResponse response =
                                    new OperacaoTrasladoPassageiroResponse();

                            response.setTrasladoId(
                                    traslado.getId()
                            );

                            response.setHospedeId(
                                    traslado.getHospede()
                                            .getId()
                            );

                            response.setHospedeNome(
                                    traslado.getHospede()
                                            .getNomeCompleto()
                            );

                            return response;
                        }
                )
                .toList();
    }

    private void validarCompatibilidadeComTrasladoVinculados(
            Long operacaoId,
            TipoTraslado novoTipo,
            Aeroporto novoAeroporto
    ) {

        List<Traslado> trasladosVinculados =
                trasladoRepository
                        .findByOperacaoTrasladoId(
                                operacaoId
                        );
        for (Traslado traslado : trasladosVinculados) {

            if (
                    traslado.getTipo()
                            != novoTipo
            ) {

                throw new ViagemIncompativelException(
                        "Não é possível alterar o tipo da operação porque existem traslados incompatíveis vinculados."
                );
            }

            if (
                    novoTipo != TipoTraslado.OUTRO
                            &&
                            traslado.getAeroporto()
                                    != novoAeroporto
            ) {
                throw new ViagemIncompativelException(
                        "Não é possível alterar o aeroporto da operação porque existem traslados incompatíveis vinculados."
                );
            }
        }
    }

    private void validarTransicaoStatus(
            StatusTraslado statusAtual,
            StatusTraslado novoStatus
    ) {

        boolean transicaoValida =
                switch (statusAtual) {

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
                            + " ! "
            );
        }
    }

    private void aplicarTrajetoConsistente(
            OperacaoTraslado operacao,
            TipoTraslado tipo,
            Aeroporto aeroporto,
            String localOrigem,
            String localDestino
    ) {

        if (tipo == null) {
            throw new IllegalArgumentException(
                    "O tipo do traslado é obrigatório!"
            );
        }

        operacao.setTipo(
                tipo
        );

        if (
                tipo == TipoTraslado.OUTRO
        ) {

            operacao.setAeroporto(
                    null
            );

            operacao.setLocalOrigem(
                    localOrigem
            );

            operacao.setLocalDestino(
                    localDestino
            );

            return;
        }

        if (aeroporto == null) {
            throw new IllegalArgumentException(
                    "O aeroporto é obrigatório para este tipo de traslado!"
            );
        }

        String localAeroporto =
                obterLocalAeroporto(
                        aeroporto
                );

        operacao.setAeroporto(
                aeroporto
        );

        if (
                tipo ==
                        TipoTraslado.AEROPORTO_PARA_HOSPEDAGEM
        ) {

            operacao.setLocalOrigem(
                    localAeroporto
            );

            operacao.setLocalDestino(
                    localDestino
            );

            return;
        }

        if (
                tipo ==
                        TipoTraslado.HOSPEDAGEM_PARA_AEROPORTO
        ) {

            operacao.setLocalOrigem(
                    localOrigem
            );

            operacao.setLocalDestino(
                    localAeroporto
            );
        }
    }

    private String obterLocalAeroporto(
            Aeroporto aeroporto
    ) {

        return switch (aeroporto) {

            case GRU -> "Aeroporto de Guarulhos";

            case CGH -> "Aeroporto de Congonhas";

            case VCP -> "Aeroporto de Viracopos";
        };
    }

    private void registarHistoricoStatus(
            OperacaoTraslado operacao,
            StatusTraslado statusAnterior,
            StatusTraslado novoStatus,
            String motivo
    ) {

        HistoricoStatusOperacaoTraslado historico =
                new HistoricoStatusOperacaoTraslado();

        historico.setOperacaoTraslado(
                operacao
        );

        historico.setStatusAnterior(
                statusAnterior
        );

        historico.setNovoStatus(
                novoStatus
        );

        historico.setMotivo(
                motivo
        );

        historico.setDataHora(
                LocalDateTime.now()
        );

        historicoStatusOperacaoTrasladoRepository
                .save(historico);
    }
}