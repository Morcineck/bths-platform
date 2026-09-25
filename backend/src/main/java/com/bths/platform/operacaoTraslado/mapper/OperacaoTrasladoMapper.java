package com.bths.platform.operacaoTraslado.mapper;

import com.bths.platform.operacaoTraslado.OperacaoTraslado;
import com.bths.platform.operacaoTraslado.dto.OperacaoTrasladoResponse;
import com.bths.platform.traslado.TrasladoRepository;
import org.springframework.stereotype.Component;

@Component
public class OperacaoTrasladoMapper {

    private final TrasladoRepository trasladoRepository;

    public OperacaoTrasladoMapper(
            TrasladoRepository trasladoRepository
    ) {
        this.trasladoRepository = trasladoRepository;
    }

    public OperacaoTrasladoResponse paraResponse(
            OperacaoTraslado operacao
    ) {

        OperacaoTrasladoResponse response =
                new OperacaoTrasladoResponse();

        response.setId(
                operacao.getId()
        );

        response.setViagemId(
                operacao.getViagem().getId()
        );

        response.setViagemNome(
                operacao.getViagem().getNome()
        );

        response.setTipo(
                operacao.getTipo()
        );

        response.setAeroporto(
                operacao.getAeroporto()
        );

        response.setStatus(
                operacao.getStatus()
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

        if (operacao.getMotorista() != null) {

            response.setMotoristaId(
                    operacao.getMotorista().getId()
            );

            response.setMotoristaNome(
                    operacao.getMotorista()
                            .getNomeCompleto()
            );
        }

        if (operacao.getVeiculo() != null) {

            response.setVeiculoId(
                    operacao.getVeiculo().getId()
            );

            response.setVeiculoModelo(
                    operacao.getVeiculo().getModelo()
            );

            response.setVeiculoPlaca(
                    operacao.getVeiculo().getPlaca()
            );

            response.setCapacidadePassageiros(
                    operacao.getVeiculo()
                            .getCapacidadePassageiros()
            );
        }

        long quantidadePassageiros =
                trasladoRepository
                        .countByOperacaoTrasladoId(
                                operacao.getId()
                        );

        response.setQuantidadePassageiros(
                quantidadePassageiros
        );

        if (operacao.getVeiculo() != null) {

            long vagasDisponiveis =
                    operacao.getVeiculo()
                            .getCapacidadePassageiros()
                            - quantidadePassageiros;

            response.setVagasDisponiveis(
                    Math.max(
                            vagasDisponiveis,
                            0
                    )
            );
        }

        response.setObservacao(
                operacao.getObservacao()
        );

        return response;
    }
}