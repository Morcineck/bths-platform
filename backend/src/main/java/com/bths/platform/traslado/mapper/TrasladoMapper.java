package com.bths.platform.traslado.mapper;

import com.bths.platform.traslado.Traslado;
import com.bths.platform.traslado.dto.TrasladoResponse;
import org.springframework.stereotype.Component;

@Component
public class TrasladoMapper {

    public TrasladoResponse paraResponse(
            Traslado traslado
    ) {

        TrasladoResponse response =
                new TrasladoResponse();

        response.setId(
                traslado.getId()
        );

        response.setHospedeId(
                traslado.getHospede().getId()
        );

        response.setHospedeNome(
                traslado.getHospede()
                        .getNomeCompleto()
        );

        response.setViagemId(
                traslado.getViagem().getId()
        );

        response.setViagemNome(
                traslado.getViagem().getNome()
        );

        if (
                traslado.getOperacaoTraslado()
                        != null
        ) {
            response.setOperacaoTrasladoId(
                    traslado.getOperacaoTraslado()
                            .getId()
            );
        }

        response.setTipo(
                traslado.getTipo()
        );

        response.setAeroporto(
                traslado.getAeroporto()
        );

        response.setStatus(
                traslado.getStatus()
        );

        response.setDataHoraPrevista(
                traslado.getDataHoraPrevista()
        );

        response.setNumeroVoo(
                traslado.getNumeroVoo()
        );

        response.setCompanhiaAerea(
                traslado.getCompanhiaAerea()
        );

        response.setLocalOrigem(
                traslado.getLocalOrigem()
        );

        response.setLocalDestino(
                traslado.getLocalDestino()
        );

        response.setObservacoes(
                traslado.getObservacao()
        );

        if (
                traslado.getMotorista()
                        != null
        ) {
            response.setMotoristaId(
                    traslado.getMotorista()
                            .getId()
            );

            response.setMotoristaNome(
                    traslado.getMotorista()
                            .getNomeCompleto()
            );
        }

        if (
                traslado.getVeiculo()
                        != null
        ) {
            response.setVeiculoId(
                    traslado.getVeiculo()
                            .getId()
            );

            response.setVeiculoModelo(
                    traslado.getVeiculo()
                            .getModelo()
            );

            response.setVeiculoPlaca(
                    traslado.getVeiculo()
                            .getPlaca()
            );

            response.setVeiculoCapacidadePassageiros(
                    traslado.getVeiculo()
                            .getCapacidadePassageiros()
            );
        }

        return response;
    }
}