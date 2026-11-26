package com.bths.platform.veiculo.mapper;

import com.bths.platform.veiculo.Veiculo;
import com.bths.platform.veiculo.dto.VeiculoRequest;
import com.bths.platform.veiculo.dto.VeiculoResponse;
import org.springframework.stereotype.Component;

@Component
public class VeiculoMapper {

    public Veiculo paraEntidade(
            VeiculoRequest request
    ) {

        Veiculo veiculo = new Veiculo();

        veiculo.setModelo(
                request.getModelo()
        );

        veiculo.setPlaca(
                request.getPlaca()
        );

        veiculo.setCapacidadePassageiros(
                request.getCapacidadePassageiros()
        );

        veiculo.setObservacao(
                request.getObservacao()
        );

        veiculo.setAtivo(true);

        return veiculo;
    }

    public VeiculoResponse paraResponse(
            Veiculo veiculo
    ) {

        VeiculoResponse response =
                new VeiculoResponse();

        response.setId(
                veiculo.getId()
        );

        response.setModelo(
                veiculo.getModelo()
        );

        response.setPlaca(
                veiculo.getPlaca()
        );

        response.setCapacidadePassageiros(
                veiculo.getCapacidadePassageiros()
        );

        response.setObservacao(
                veiculo.getObservacao()
        );

        response.setAtivo(
                veiculo.isAtivo());

        return response;
    }

    public void atualizarVeiculo(
            Veiculo veiculo,
            VeiculoRequest request
    ) {

        veiculo.setModelo(
                request.getModelo()
        );

        veiculo.setPlaca(
                request.getPlaca()
        );

        veiculo.setCapacidadePassageiros(
                request.getCapacidadePassageiros()
        );

        veiculo.setObservacao(
                request.getObservacao()
        );
    }

}
