package com.bths.platform.quarto.mapper;

import com.bths.platform.quarto.Quarto;
import com.bths.platform.quarto.dto.QuartoRequest;
import com.bths.platform.quarto.dto.QuartoResponse;
import org.springframework.stereotype.Component;

@Component
public class QuartoMapper {

    public QuartoResponse paraResponse(Quarto quarto) {

        QuartoResponse response = new QuartoResponse();

        response.setId(quarto.getId());
        response.setNome(quarto.getNome());
        response.setTipo(quarto.getTipo());
        response.setCapacidade(quarto.getCapacidade());
        response.setStatus(quarto.getStatus());

        response.setViagemId(quarto.getViagem().getId());
        response.setViagemNome(quarto.getViagem().getNome());

        return response;
    }

    public void atualizarEntidade(
            Quarto quarto,
            QuartoRequest request
    ) {

        quarto.setNome(request.getNome());
        quarto.setTipo(request.getTipo());
        quarto.setCapacidade(request.getCapacidade());
        quarto.setStatus(request.getStatus());
    }
}
