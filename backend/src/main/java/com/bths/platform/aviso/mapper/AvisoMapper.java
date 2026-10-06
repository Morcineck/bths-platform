package com.bths.platform.aviso.mapper;

import com.bths.platform.aviso.Aviso;
import com.bths.platform.aviso.dto.AvisoRequest;
import com.bths.platform.aviso.dto.AvisoResponse;
import org.springframework.stereotype.Component;

@Component
public class AvisoMapper {

    public AvisoResponse paraResponse(Aviso aviso

    ) {

        AvisoResponse response =
                new AvisoResponse();

        response.setId(
                aviso.getId()
        );

        response.setTitulo(
                aviso.getTitulo()
        );

        response.setMensagem(
                aviso.getMensagem()
        );

        response.setTipo(
                aviso.getTipo()
        );

        response.setDataPublicacao(
                aviso.getDataPublicacao()
        );

        response.setAtivo(
                aviso.getAtivo()
        );

        if (aviso.getViagem() != null) {
            response.setViagemId(
                    aviso.getViagem().getId()
            );
        }

        return response;

    }

    public void atualizarEntidade(Aviso aviso, AvisoRequest request

    ) {

        aviso.setTitulo(
                request.getTitulo()
        );

        aviso.setMensagem(
                request.getMensagem()

        );

        aviso.setTipo(
                request.getTipo()
        );

        aviso.setDataPublicacao(
                request.getDataPublicacao()
        );

        aviso.setAtivo(
                request.getAtivo()
        );

    }

}
