package com.bths.platform.operacaoTraslado.mapper;

import com.bths.platform.operacaoTraslado.HistoricoStatusOperacaoTraslado;
import com.bths.platform.operacaoTraslado.dto.HistoricoStatusOperacaoTrasladoResponse;
import org.springframework.stereotype.Component;

@Component
public class HistoricoStatusOperacaoTrasladoMapper {

    public HistoricoStatusOperacaoTrasladoResponse paraResponse(
            HistoricoStatusOperacaoTraslado historico
    ) {

        HistoricoStatusOperacaoTrasladoResponse response =
                new HistoricoStatusOperacaoTrasladoResponse();

        response.setId(
                historico.getId()
        );

        response.setOperacaoTrasladoId(
                historico.getOperacaoTraslado()
                        .getId()
        );

        response.setStatusAnterior(
                historico.getStatusAnterior()
        );

        response.setNovoStatus(
                historico.getNovoStatus()
        );

        response.setMotivo(
                historico.getMotivo()
        );

        response.setDataHora(
                historico.getDataHora()
        );

        return response;
    }
}
