package com.bths.platform.traslado.mapper;

import com.bths.platform.traslado.HistoricoStatusTraslado;
import com.bths.platform.traslado.dto.HistoricoStatusTrasladoResponse;
import org.springframework.stereotype.Component;

@Component
public class HistoricoStatusTrasladoMapper {

    public HistoricoStatusTrasladoResponse paraResponse(
            HistoricoStatusTraslado historico
    ) {

        HistoricoStatusTrasladoResponse response =
                new HistoricoStatusTrasladoResponse();

        response.setId(historico.getId());
        response.setTrasladoId(historico.getTraslado().getId());
        response.setStatusAnterior(historico.getStatusAterior());
        response.setNovoStatus(historico.getNovoStatus());
        response.setMotivo(historico.getMotivo());
        response.setDataHora(historico.getDataHora());

        return response;
    }
}
