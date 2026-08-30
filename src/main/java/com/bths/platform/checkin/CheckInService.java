package com.bths.platform.checkin;

import com.bths.platform.alocacao.AlocacaoQuarto;
import com.bths.platform.alocacao.AlocacaoQuartoRepository;
import com.bths.platform.checkin.dto.CheckInRequest;
import com.bths.platform.checkin.dto.CheckInResponse;
import com.bths.platform.checkin.mapper.CheckInMapper;
import com.bths.platform.exception.CheckInJaRealizadoException;
import com.bths.platform.exception.HospedeNaoEncontradoException;
import com.bths.platform.exception.HospedeSemAlocacaoException;
import com.bths.platform.hospede.Hospede;
import com.bths.platform.hospede.HospedeRepository;
import com.bths.platform.hospede.enums.StatusCheckIn;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CheckInService {

    private final HospedeRepository hospedeRepository;
    private final AlocacaoQuartoRepository alocacaoRepository;
    private final CheckInMapper checkInMapper;

    public CheckInService(
            HospedeRepository hospedeRepository,
            AlocacaoQuartoRepository alocacaoRepository,
            CheckInMapper checkInMapper
    ) {
        this.hospedeRepository = hospedeRepository;
        this.alocacaoRepository = alocacaoRepository;
        this.checkInMapper = checkInMapper;

    }

    public CheckInResponse realizarCheckIn(
            Long hospedeId,
            CheckInRequest request
    ) {

        Hospede hospede = hospedeRepository
                .findById(hospedeId)
                .orElseThrow(() ->
                        new HospedeNaoEncontradoException(
                                "Hóspede não encontrado!"
                        )
                );

        if (hospede.getStatusCheckIn() == StatusCheckIn.REALIZADO) {
            throw new CheckInJaRealizadoException(
                    "Check-in já realizado para este hóspede!"
            );
        }

        Long viagemId = hospede.getViagem().getId();

        AlocacaoQuarto alocacao = alocacaoRepository
                .findByHospedeIdAndViagemId(
                        hospede.getId(),
                        viagemId
                )
                .orElseThrow(() -> new HospedeSemAlocacaoException(
                                "Hóspede não possui alocação de quarto!"
                        )
                );

        hospede.setStatusCheckIn(StatusCheckIn.REALIZADO);
        hospede.setDataHoraCheckIn(LocalDateTime.now());
        hospede.setResponsavelCheckIn(request.getResponsavel());
        hospede.setObservacaoCheckIn(request.getObservacao());

        Hospede hospedeAtualizado = hospedeRepository.save(hospede);

        return checkInMapper.paraResponse(
                hospedeAtualizado,
                alocacao
        );
    }

    public CheckInResponse consultarCheckIn(Long hospedeId) {

        Hospede hospede = hospedeRepository
                .findById(hospedeId)
                .orElseThrow(() -> new HospedeNaoEncontradoException(
                                "Hóspede não encontrado!"
                        )
                );

        Long viagemId = hospede.getViagem().getId();

        AlocacaoQuarto alocacao = alocacaoRepository
                .findByHospedeIdAndViagemId(
                        hospede.getId(),
                        viagemId
                )
                .orElse(null);

        return checkInMapper.paraResponse(
                hospede,
                alocacao
        );

    }

}
