package com.bths.platform.motorista.mapper;

import com.bths.platform.motorista.Motorista;
import com.bths.platform.motorista.dto.MotoristaRequest;
import com.bths.platform.motorista.dto.MotoristaResponse;
import org.springframework.stereotype.Component;

@Component
public class MotoristaMapper {

    public Motorista paraEntidade(
            MotoristaRequest request
    ) {

        Motorista motorista = new Motorista();

        motorista.setNomeCompleto(
                request.getNomeCompleto()
        );

        motorista.setTelefone(
                request.getTelefone()
        );

        motorista.setObservacao(
                request.getObservacao()
        );

        motorista.setAtivo(true);

        return motorista;
    }

    public MotoristaResponse paraResponse(
            Motorista motorista
    ) {

        MotoristaResponse response =
                new MotoristaResponse();

        response.setId(
                motorista.getId()

        );

        response.setNomeCompleto(
                motorista.getNomeCompleto()

        );

        response.setTelefone(
                motorista.getTelefone()
        );

        response.setObservacao(
                motorista.getObservacao()
        );

        response.setAtivo(
                motorista.isAtivo()
        );

        return response;
    }

    public void atualizarEntidade(
            Motorista motorista,
            MotoristaRequest request
    ) {

        motorista.setNomeCompleto(
                request.getNomeCompleto()
        );

        motorista.setTelefone(
                request.getTelefone()
        );

        motorista.setObservacao(
                request.getObservacao()
        );
    }
}
