package com.bths.platform.agenda.mapper;

import com.bths.platform.agenda.AgendaViagem;
import com.bths.platform.agenda.dto.AgendaViagemRequest;
import com.bths.platform.agenda.dto.AgendaViagemResponse;
import org.springframework.stereotype.Component;

@Component
public class AgendaViagemMapper {

    public AgendaViagemResponse paraResponse(
            AgendaViagem agenda
    ) {

        AgendaViagemResponse response =
                new AgendaViagemResponse();

        response.setId(
                agenda.getId()
        );

        response.setTitulo(
                agenda.getTitulo()
        );

        response.setDescricao(
                agenda.getDescricao()
        );

        response.setDataHoraInicio(
                agenda.getDataHoraInicio()
        );

        response.setDataHoraFim(
                agenda.getDataHoraFim()
        );

        response.setTipo(
                agenda.getTipo()
        );

        response.setOrdem(
                agenda.getOrdem()
        );

        response.setVisivelHospede(
                agenda.getVisivelHospede()
        );

        response.setAtivo(
                agenda.getAtivo()
        );

        if (agenda.getViagem() != null) {

            response.setViagemId(
                    agenda.getViagem().getId()
            );

            response.setViagemNome(
                    agenda.getViagem().getNome()
            );
        }

        return response;
    }

    public void atualizarEntidade(
            AgendaViagem agenda,
            AgendaViagemRequest request
    ) {

        agenda.setTitulo(
                request.getTitulo()
        );

        agenda.setDescricao(
                request.getDescricao()
        );

        agenda.setDataHoraInicio(
                request.getDataHoraInicio()
        );

        agenda.setDataHoraFim(
                request.getDataHoraFim()
        );

        agenda.setTipo(
                request.getTipo()
        );

        agenda.setOrdem(
                request.getOrdem()
        );

        agenda.setVisivelHospede(
                request.getVisivelHospede()
        );

        agenda.setAtivo(
                request.getAtivo()
        );
    }
}