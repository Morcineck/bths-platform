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
import com.bths.platform.hospede.StatusCheckIn;
import com.bths.platform.quarto.Quarto;
import com.bths.platform.viagem.Viagem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckInServiceTest {

    @Mock
    private HospedeRepository hospedeRepository;

    @Mock
    private AlocacaoQuartoRepository alocacaoRepository;

    @Mock
    private CheckInMapper checkInMapper;

    private CheckInService checkInService;

    @BeforeEach
    void setUp() {

        checkInService = new CheckInService(
                hospedeRepository,
                alocacaoRepository,
                checkInMapper
        );
    }

    @Test
    void deveRealizarCheckInComSucesso() {

        // Arrange
        Long hospedeId = 1L;

        Viagem viagem = new Viagem();
        viagem.setId(1L);

        Quarto quarto = new Quarto();
        quarto.setId(2L);

        Hospede hospede = new Hospede();
        hospede.setId(hospedeId);
        hospede.setNomeCompleto("João da Silva");
        hospede.setStatusCheckIn(StatusCheckIn.PENDENTE);
        hospede.setViagem(viagem);

        AlocacaoQuarto alocacao = new AlocacaoQuarto();
        alocacao.setHospede(hospede);
        alocacao.setQuarto(quarto);
        alocacao.setViagem(viagem);

        CheckInRequest request = new CheckInRequest();
        request.setResponsavel("Robson");
        request.setObservacao("Hóspede chegou normalmente");

        CheckInResponse responseEsperada = new CheckInResponse();
        responseEsperada.setHospedeId(hospedeId);
        responseEsperada.setStatusCheckIn(StatusCheckIn.REALIZADO);

        when(hospedeRepository.findById(hospedeId))
                .thenReturn(Optional.of(hospede));

        when(alocacaoRepository.findByHospedeIdAndViagemId(
                hospedeId,
                viagem.getId()
        )).thenReturn(Optional.of(alocacao));

        when(hospedeRepository.save(hospede))
                .thenReturn(hospede);

        when(checkInMapper.paraResponse(hospede, alocacao))
                .thenReturn(responseEsperada);

        // Act
        CheckInResponse response =
                checkInService.realizarCheckIn(
                        hospedeId,
                        request
                );

        // Assert
        assertNotNull(response);
        assertEquals(hospedeId, response.getHospedeId());
        assertEquals(
                StatusCheckIn.REALIZADO,
                response.getStatusCheckIn()
        );

        assertEquals(
                StatusCheckIn.REALIZADO,
                hospede.getStatusCheckIn()
        );

        assertNotNull(hospede.getDataHoraCheckIn());
        assertEquals(
                "Robson",
                hospede.getResponsavelCheckIn()
        );
        assertEquals(
                "Hóspede chegou normalmente",
                hospede.getObservacaoCheckIn()
        );

        verify(hospedeRepository).findById(hospedeId);

        verify(alocacaoRepository)
                .findByHospedeIdAndViagemId(
                        hospedeId,
                        viagem.getId()
                );

        verify(hospedeRepository).save(hospede);

        verify(checkInMapper)
                .paraResponse(hospede, alocacao);
    }

    @Test
    void deveLancarExceptionQuandoHospedeNaoExistir() {

        // Arrange
        Long hospedeId = 999L;

        CheckInRequest request = new CheckInRequest();
        request.setResponsavel("Robson");

        when(hospedeRepository.findById(hospedeId))
                .thenReturn(Optional.empty());

        // Act + Assert
        HospedeNaoEncontradoException exception =
                assertThrows(
                        HospedeNaoEncontradoException.class,
                        () -> checkInService.realizarCheckIn(
                                hospedeId,
                                request
                        )
                );

        assertEquals(
                "Hóspede não encontrado!",
                exception.getMessage()
        );

        verify(hospedeRepository).findById(hospedeId);

        verifyNoInteractions(
                alocacaoRepository,
                checkInMapper
        );

        verify(hospedeRepository, never())
                .save(any(Hospede.class));
    }

    @Test
    void deveLancarExceptionQuandoCheckInJaFoiRealizado() {

        // Arrange
        Long hospedeId = 1L;

        Hospede hospede = new Hospede();
        hospede.setId(hospedeId);
        hospede.setStatusCheckIn(StatusCheckIn.REALIZADO);

        CheckInRequest request = new CheckInRequest();
        request.setResponsavel("Robson");

        when(hospedeRepository.findById(hospedeId))
                .thenReturn(Optional.of(hospede));

        // Act + Assert
        CheckInJaRealizadoException exception =
                assertThrows(
                        CheckInJaRealizadoException.class,
                        () -> checkInService.realizarCheckIn(
                                hospedeId,
                                request
                        )
                );

        assertEquals(
                "Check-in já realizado para este hóspede!",
                exception.getMessage()
        );

        verify(hospedeRepository).findById(hospedeId);

        verify(hospedeRepository, never())
                .save(any(Hospede.class));

        verifyNoInteractions(
                alocacaoRepository,
                checkInMapper
        );
    }

    @Test
    void deveLancarExceptionQuandoHospedeNaoPossuirAlocacao() {

        // Arrange
        Long hospedeId = 4L;
        Long viagemId = 1L;

        Viagem viagem = new Viagem();
        viagem.setId(viagemId);

        Hospede hospede = new Hospede();
        hospede.setId(hospedeId);
        hospede.setStatusCheckIn(StatusCheckIn.PENDENTE);
        hospede.setViagem(viagem);

        CheckInRequest request = new CheckInRequest();
        request.setResponsavel("Robson");

        when(hospedeRepository.findById(hospedeId))
                .thenReturn(Optional.of(hospede));

        when(alocacaoRepository.findByHospedeIdAndViagemId(
                hospedeId,
                viagemId
        )).thenReturn(Optional.empty());

        // Act + Assert
        HospedeSemAlocacaoException exception =
                assertThrows(
                        HospedeSemAlocacaoException.class,
                        () -> checkInService.realizarCheckIn(
                                hospedeId,
                                request
                        )
                );

        assertEquals(
                "Hóspede não possui alocação de quarto!",
                exception.getMessage()
        );

        verify(hospedeRepository).findById(hospedeId);

        verify(alocacaoRepository)
                .findByHospedeIdAndViagemId(
                        hospedeId,
                        viagemId
                );

        verify(hospedeRepository, never())
                .save(any(Hospede.class));

        verifyNoInteractions(checkInMapper);
    }

    @Test
    void deveConsultarCheckInRealizadoComSucesso() {

        // Arrange
        Long hospedeId = 1L;
        Long viagemId = 1L;

        Viagem viagem = new Viagem();
        viagem.setId(viagemId);

        Quarto quarto = new Quarto();
        quarto.setId(2L);
        quarto.setNome("Suíte 01");

        Hospede hospede = new Hospede();
        hospede.setId(hospedeId);
        hospede.setNomeCompleto("João da Silva");
        hospede.setStatusCheckIn(StatusCheckIn.REALIZADO);
        hospede.setViagem(viagem);

        AlocacaoQuarto alocacao = new AlocacaoQuarto();
        alocacao.setHospede(hospede);
        alocacao.setQuarto(quarto);
        alocacao.setViagem(viagem);

        CheckInResponse responseEsperada = new CheckInResponse();
        responseEsperada.setHospedeId(hospedeId);
        responseEsperada.setHospedeNome("João da Silva");
        responseEsperada.setStatusCheckIn(StatusCheckIn.REALIZADO);
        responseEsperada.setQuartoId(2L);
        responseEsperada.setQuartoNome("Suíte 01");

        when(hospedeRepository.findById(hospedeId))
                .thenReturn(Optional.of(hospede));

        when(alocacaoRepository.findByHospedeIdAndViagemId(
                hospedeId,
                viagemId
        )).thenReturn(Optional.of(alocacao));

        when(checkInMapper.paraResponse(hospede, alocacao))
                .thenReturn(responseEsperada);

        // Act
        CheckInResponse response =
                checkInService.consultarCheckIn(hospedeId);

        // Assert
        assertNotNull(response);
        assertEquals(hospedeId, response.getHospedeId());
        assertEquals("João da Silva", response.getHospedeNome());
        assertEquals(
                StatusCheckIn.REALIZADO,
                response.getStatusCheckIn()
        );
        assertEquals(2L, response.getQuartoId());
        assertEquals("Suíte 01", response.getQuartoNome());

        verify(hospedeRepository).findById(hospedeId);

        verify(alocacaoRepository)
                .findByHospedeIdAndViagemId(
                        hospedeId,
                        viagemId
                );

        verify(checkInMapper)
                .paraResponse(hospede, alocacao);

        verify(hospedeRepository, never())
                .save(any(Hospede.class));
    }

    @Test
    void deveConsultarCheckInPendenteSemAlocacao() {

        // Arrange
        Long hospedeId = 4L;
        Long viagemId = 1L;

        Viagem viagem = new Viagem();
        viagem.setId(viagemId);

        Hospede hospede = new Hospede();
        hospede.setId(hospedeId);
        hospede.setNomeCompleto("Carlos Henrique");
        hospede.setStatusCheckIn(StatusCheckIn.PENDENTE);
        hospede.setViagem(viagem);

        CheckInResponse responseEsperada = new CheckInResponse();
        responseEsperada.setHospedeId(hospedeId);
        responseEsperada.setHospedeNome("Carlos Henrique");
        responseEsperada.setStatusCheckIn(StatusCheckIn.PENDENTE);

        when(hospedeRepository.findById(hospedeId))
                .thenReturn(Optional.of(hospede));

        when(alocacaoRepository.findByHospedeIdAndViagemId(
                hospedeId,
                viagemId
        )).thenReturn(Optional.empty());

        when(checkInMapper.paraResponse(hospede, null))
                .thenReturn(responseEsperada);

        // Act
        CheckInResponse response =
                checkInService.consultarCheckIn(hospedeId);

        // Assert
        assertNotNull(response);

        assertEquals(
                hospedeId,
                response.getHospedeId()
        );

        assertEquals(
                "Carlos Henrique",
                response.getHospedeNome()
        );

        assertEquals(
                StatusCheckIn.PENDENTE,
                response.getStatusCheckIn()
        );

        assertNull(response.getQuartoId());
        assertNull(response.getQuartoNome());

        verify(hospedeRepository)
                .findById(hospedeId);

        verify(alocacaoRepository)
                .findByHospedeIdAndViagemId(
                        hospedeId,
                        viagemId
                );

        verify(checkInMapper)
                .paraResponse(hospede, null);

        verify(hospedeRepository, never())
                .save(any(Hospede.class));
    }

    @Test
    void deveLancarExceptionAoConsultarHospedeInexistente() {

        // Arrange
        Long hospedeId = 999L;

        when(hospedeRepository.findById(hospedeId))
                .thenReturn(Optional.empty());

        // Act + Assert
        HospedeNaoEncontradoException exception =
                assertThrows(
                        HospedeNaoEncontradoException.class,
                        () -> checkInService.consultarCheckIn(hospedeId)
                );

        assertEquals(
                "Hóspede não encontrado!",
                exception.getMessage()
        );

        verify(hospedeRepository)
                .findById(hospedeId);

        verifyNoInteractions(
                alocacaoRepository,
                checkInMapper
        );

        verify(hospedeRepository, never())
                .save(any(Hospede.class));
    }
}