package com.bths.platform.alocacao.mapper;

import com.bths.platform.alocacao.AlocacaoQuarto;
import com.bths.platform.alocacao.dto.AlocacaoQuartoResponse;
import org.springframework.stereotype.Component;

@Component
public class AlocacaoQuartoMapper {

    public AlocacaoQuartoResponse paraResponse(
            AlocacaoQuarto alocacao
    ) {

        AlocacaoQuartoResponse response = new AlocacaoQuartoResponse();

        response.setId(alocacao.getId());

        response.setHospedeId(alocacao.getHospede().getId());
        response.setHospedeNome(alocacao.getHospede().getNomeCompleto());
        response.setQuartoId(alocacao.getQuarto().getId());
        response.setQuartoNome(alocacao.getQuarto().getNome());
        response.setViagemId(alocacao.getViagem().getId());
        response.setViagemNome(alocacao.getViagem().getNome());
        response.setDataAlocacao(alocacao.getDataAlocacao());

        return response;
    }

}
