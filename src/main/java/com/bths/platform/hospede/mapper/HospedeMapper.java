package com.bths.platform.hospede.mapper;

import com.bths.platform.hospede.Hospede;
import com.bths.platform.hospede.dto.HospedeRequest;
import com.bths.platform.hospede.dto.HospedeResponse;
import org.springframework.stereotype.Component;

@Component
public class HospedeMapper {

    public HospedeResponse paraResponse(Hospede hospede) {

        HospedeResponse response = new HospedeResponse();

        response.setId(hospede.getId());
        response.setNomeCompleto(hospede.getNomeCompleto());
        response.setCpf(hospede.getCpf());
        response.setTelefone(hospede.getTelefone());
        response.setEmail(hospede.getEmail());
        response.setDataNascimento(hospede.getDataNascimento());
        response.setHorarioPrevistoChegada(hospede.getHorarioPrevistoChegada());

        response.setStatusCheckIn(hospede.getStatusCheckIn());

        response.setViagemId(hospede.getViagem().getId());
        response.setViagemNome(hospede.getViagem().getNome());

        return response;
    }

    public void atualizarEntidade(
            Hospede hospede,
            HospedeRequest request
    ) {

        hospede.setNomeCompleto(request.getNomeCompleto());
        hospede.setCpf(request.getCpf());
        hospede.setTelefone(request.getTelefone());
        hospede.setEmail(request.getEmail());
        hospede.setDataNascimento(request.getDataNascimento());
        hospede.setHorarioPrevistoChegada(request.getHorarioPrevistoChegada());
        hospede.setStatusCheckIn(request.getStatusCheckIn());
    }
}
