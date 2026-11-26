package com.bths.platform.agenda;

import com.bths.platform.agenda.dto.AgendaViagemRequest;
import com.bths.platform.agenda.dto.AgendaViagemResponse;
import com.bths.platform.agenda.exception.AgendaViagemNaoEncontradaException;
import com.bths.platform.agenda.mapper.AgendaViagemMapper;
import com.bths.platform.viagem.Viagem;
import com.bths.platform.viagem.ViagemRepository;
import com.bths.platform.viagem.exception.ViagemNaoEncontradaException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgendaViagemService {

    private final AgendaViagemRepository agendaViagemRepository;
    private final ViagemRepository viagemRepository;
    private final AgendaViagemMapper agendaViagemMapper;

    public AgendaViagemService(
            AgendaViagemRepository agendaViagemRepository,
            ViagemRepository viagemRepository,
            AgendaViagemMapper agendaViagemMapper
    ) {
        this.agendaViagemRepository =
                agendaViagemRepository;

        this.viagemRepository =
                viagemRepository;

        this.agendaViagemMapper =
                agendaViagemMapper;
    }

    public AgendaViagemResponse cadastrar(
            AgendaViagemRequest request
    ) {

        Viagem viagem =
                viagemRepository
                        .findById(
                                request.getViagemId()
                        )
                        .orElseThrow(() ->
                                new ViagemNaoEncontradaException(
                                        "Viagem não encontrada!"
                                )
                        );

        AgendaViagem agenda =
                new AgendaViagem();

        agendaViagemMapper.atualizarEntidade(
                agenda,
                request
        );

        agenda.setViagem(
                viagem
        );

        AgendaViagem salva =
                agendaViagemRepository.save(
                        agenda
                );

        return agendaViagemMapper.paraResponse(
                salva
        );
    }

    public AgendaViagemResponse buscarPorId(
            Long id
    ) {

        AgendaViagem agenda =
                agendaViagemRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new AgendaViagemNaoEncontradaException(
                                        "Item da agenda não encontrado!"
                                )
                        );

        return agendaViagemMapper.paraResponse(
                agenda
        );
    }

    public List<AgendaViagemResponse> listarPorViagem(
            Long viagemId
    ) {

        viagemRepository
                .findById(viagemId)
                .orElseThrow(() ->
                        new ViagemNaoEncontradaException(
                                "Viagem não encontrada!"
                        )
                );

        return agendaViagemRepository
                .findByViagemIdOrderByOrdemAscDataHoraInicioAsc(
                        viagemId
                )
                .stream()
                .map(
                        agendaViagemMapper::paraResponse
                )
                .toList();
    }

    public AgendaViagemResponse atualizar(
            Long id,
            AgendaViagemRequest request
    ) {

        AgendaViagem agenda =
                agendaViagemRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new AgendaViagemNaoEncontradaException(
                                        "Item da agenda não encontrado!"
                                )
                        );

        Viagem viagem =
                viagemRepository
                        .findById(
                                request.getViagemId()
                        )
                        .orElseThrow(() ->
                                new ViagemNaoEncontradaException(
                                        "Viagem não encontrada!"
                                )
                        );

        agendaViagemMapper.atualizarEntidade(
                agenda,
                request
        );

        agenda.setViagem(
                viagem
        );

        AgendaViagem atualizada =
                agendaViagemRepository.save(
                        agenda
                );

        return agendaViagemMapper.paraResponse(
                atualizada
        );
    }

    public void deletar(
            Long id
    ) {

        AgendaViagem agenda =
                agendaViagemRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new AgendaViagemNaoEncontradaException(
                                        "Item da agenda não encontrado!"
                                )
                        );

        agendaViagemRepository.delete(
                agenda
        );
    }
}