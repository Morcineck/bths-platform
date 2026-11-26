package com.bths.platform.checkin;

import com.bths.platform.alocacao.AlocacaoQuarto;
import com.bths.platform.alocacao.AlocacaoQuartoRepository;
import com.bths.platform.checkin.dto.CheckInRequest;
import com.bths.platform.checkin.dto.CheckInResponse;
import com.bths.platform.checkin.dto.NaoComparecimentoRequest;
import com.bths.platform.checkin.exception.HospedeJaRealizouCheckInException;
import com.bths.platform.checkin.exception.NaoComparecimentoJaRegistradoException;
import com.bths.platform.checkin.mapper.CheckInMapper;
import com.bths.platform.checkin.exception.CheckInJaRealizadoException;
import com.bths.platform.hospede.exception.HospedeNaoEncontradoException;
import com.bths.platform.checkin.exception.HospedeSemAlocacaoException;
import com.bths.platform.hospede.Hospede;
import com.bths.platform.hospede.HospedeRepository;
import com.bths.platform.hospede.enums.StatusCheckIn;
import com.bths.platform.usuario.Usuario;
import com.bths.platform.usuario.UsuarioRepository;
import com.bths.platform.usuario.exception.UsuarioNaoEncontradoException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CheckInService {

    private final HospedeRepository hospedeRepository;
    private final AlocacaoQuartoRepository alocacaoRepository;
    private final CheckInMapper checkInMapper;
    private final UsuarioRepository usuarioRepository;

    public CheckInService(
            HospedeRepository hospedeRepository,
            AlocacaoQuartoRepository alocacaoRepository,
            CheckInMapper checkInMapper,
            UsuarioRepository usuarioRepository

    ) {
        this.hospedeRepository = hospedeRepository;
        this.alocacaoRepository = alocacaoRepository;
        this.checkInMapper = checkInMapper;
        this.usuarioRepository = usuarioRepository;

    }

    public CheckInResponse realizarCheckIn(
            Long hospedeId,
            CheckInRequest request,
            Authentication authentication
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

        Usuario usuario = usuarioRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new UsuarioNaoEncontradoException(
                                "Usuário autenticado não encontrado!"
                        )
                );

        hospede.setResponsavelCheckIn(
                usuario.getNome()
        );

        hospede.setObservacaoCheckIn(
                request.getObservacao());

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

    public CheckInResponse registrarNaoComparecimento(
            Long hospedeId,
            NaoComparecimentoRequest request,
            Authentication authentication
    ) {

        Hospede hospede = hospedeRepository
                .findById(hospedeId)
                .orElseThrow(() -> new HospedeNaoEncontradoException(
                                "Hóspede não encontrado!"
                        )
                );

        if (hospede.getStatusCheckIn() == StatusCheckIn.REALIZADO) {
            throw new HospedeJaRealizouCheckInException(
                    "O hóspede já realizou o check-in!"
            );
        }

        if (hospede.getStatusCheckIn() == StatusCheckIn.NAO_COMPARECEU) {
            throw new NaoComparecimentoJaRegistradoException(
                    "O não comparecimento já foi registrado para esse hóspede!"
            );
        }


        Usuario usuario = usuarioRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() -> new UsuarioNaoEncontradoException(
                                "Usuário autenticado não encontrado!"
                        )
                );

        hospede.setStatusCheckIn(
                StatusCheckIn.NAO_COMPARECEU
        );

        hospede.setDataHoraNaoComparecimento(
                LocalDateTime.now()
        );

        hospede.setResponsavelNaoComparecimento(
                usuario.getNome()
        );

        hospede.setMotivoNaoComparecimento(
                request.getMotivo()
        );

        Hospede hospedeAtualizado =
                hospedeRepository.save(hospede);

        Long viagemId =
                hospedeAtualizado
                        .getViagem()
                        .getId();

        AlocacaoQuarto alocacao =
                alocacaoRepository
                        .findByHospedeIdAndViagemId(
                                hospedeId,
                                viagemId
                        )
                        .orElse(null);

        return checkInMapper.paraResponse(
                hospedeAtualizado,
                alocacao
        );


    }
}

