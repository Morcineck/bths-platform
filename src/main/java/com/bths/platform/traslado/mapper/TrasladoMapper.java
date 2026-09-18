package com.bths.platform.traslado.mapper;

import com.bths.platform.traslado.Traslado;
import com.bths.platform.traslado.dto.TrasladoResponse;
import org.springframework.stereotype.Component;

@Component
public class TrasladoMapper {

    public TrasladoResponse paraResponse(Traslado  traslado){

        TrasladoResponse response = new TrasladoResponse();

        response.setId( traslado.getId() );

        response.setHospedeId(traslado.getHospede().getId());
        response.setHospedeNome(traslado.getHospede().getNomeCompleto());

        response.setViagemId(traslado.getViagem().getId());
        response.setViagemNome(traslado.getViagem().getNome());

        response.setTipo(traslado.getTipo());
        response.setAeroporto(traslado.getAeroporto());
        response.setStatus(traslado.getStatus());

        response.setDataHoraPrevista(traslado.getDataHoraPrevista());

        response.setNumeroVoo(traslado.getNumeroVoo());
        response.setCompanhiaAerea(traslado.getCompanhiaAerea());

        response.setLocalOrigem(traslado.getLocalOrigem());
        response.setLocalDestino(traslado.getLocalDestino());

        response.setObservacoes(traslado.getObservacao());

        return  response;
    }


}
