package com.bths.platform.viagem.mapper;

import com.bths.platform.viagem.Viagem;
import com.bths.platform.viagem.dto.ViagemRequest;
import com.bths.platform.viagem.dto.ViagemResponse;
import org.springframework.stereotype.Component;

@Component
public class ViagemMapper {

    public ViagemResponse paraResponse(Viagem viagem){

        ViagemResponse response =  new ViagemResponse();

        response.setId(viagem.getId());
        response.setNome(viagem.getNome());
        response.setEvento(viagem.getEvento());
        response.setDataInicio(viagem.getDataInicio());
        response.setDataFim(viagem.getDataFim());
        response.setEndereco(viagem.getEndereco());
        response.setCidade(viagem.getCidade());
        response.setEstado(viagem.getEstado());
        response.setStatus(viagem.getStatus());

        return response;
    }

    public Viagem paraEntidade(ViagemRequest request){

        Viagem viagem = new Viagem();

        viagem.setNome(request.getNome());
        viagem.setEvento(request.getEvento());
        viagem.setDataInicio(request.getDataInicio());
        viagem.setDataFim(request.getDataFim());
        viagem.setEndereco(request.getEndereco());
        viagem.setCidade(request.getCidade());
        viagem.setEstado(request.getEstado());
        viagem.setStatus(request.getStatus());

        return viagem;
    }

    public void atualizaEntidade(ViagemRequest request, Viagem viagem){

        viagem.setNome(request.getNome());
        viagem.setEvento(request.getEvento());
        viagem.setDataInicio(request.getDataInicio());
        viagem.setDataFim(request.getDataFim());
        viagem.setEndereco(request.getEndereco());
        viagem.setCidade(request.getCidade());
        viagem.setEstado(request.getEstado());
        viagem.setStatus(request.getStatus());
    }
}
