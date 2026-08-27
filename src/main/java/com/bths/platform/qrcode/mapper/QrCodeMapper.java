package com.bths.platform.qrcode.mapper;

import com.bths.platform.alocacao.AlocacaoQuarto;
import com.bths.platform.hospede.Hospede;
import com.bths.platform.hospede.HospedeRepository;
import com.bths.platform.qrcode.dto.QrCodeCheckInResponse;
import org.springframework.stereotype.Component;

@Component
public class QrCodeMapper {

    public QrCodeCheckInResponse paraResponse(
            Hospede hospede,
            AlocacaoQuarto alocacao
    ) {

        QrCodeCheckInResponse response =
                new QrCodeCheckInResponse();

        response.setHospedeId(hospede.getId());
        response.setHospedeNome(hospede.getNomeCompleto());
        response.setStatusCheckIn(hospede.getStatusCheckIn());

        response.setViagemId(
                hospede.getViagem().getId()
        );

        response.setViagemNome(
                hospede.getViagem().getNome()
        );


        if (alocacao != null) {
            response.setQuartoId(
                    alocacao.getQuarto().getId()
            );

            response.setQuartoNome(
                    alocacao.getQuarto().getNome()
            );
        }

        return response;
    }
}
