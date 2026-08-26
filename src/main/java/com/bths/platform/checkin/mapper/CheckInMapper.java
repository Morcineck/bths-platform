package com.bths.platform.checkin.mapper;

import com.bths.platform.alocacao.AlocacaoQuarto;
import com.bths.platform.checkin.dto.CheckInResponse;
import com.bths.platform.hospede.Hospede;
import org.springframework.stereotype.Component;

@Component
public class CheckInMapper {

    public CheckInResponse paraResponse(
            Hospede hospede,
            AlocacaoQuarto alocacao
    ) {

        CheckInResponse response = new CheckInResponse();

        response.setHospedeId(hospede.getId());
        response.setHospedeNome(hospede.getNomeCompleto());
        response.setStatusCheckIn(hospede.getStatusCheckIn());
        response.setDataHoraCheckIn(hospede.getDataHoraCheckIn());
        response.setResponsavel(hospede.getResponsavelCheckIn());
        response.setObservacao(hospede.getObservacaoCheckIn());

        response.setViagemId(hospede.getViagem().getId());
        response.setViagemNome(hospede.getViagem().getNome());

        if (alocacao != null) {
            response.setQuartoId(alocacao.getQuarto().getId());
            response.setQuartoNome(alocacao.getQuarto().getNome());
        }

        return response;
    }

}
