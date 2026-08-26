package com.bths.platform.checkin;

import com.bths.platform.checkin.dto.CheckInRequest;
import com.bths.platform.checkin.dto.CheckInResponse;
import com.bths.platform.exception.*;
import com.bths.platform.hospede.StatusCheckIn;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import com.bths.platform.exception.HospedeNaoEncontradoException;

@ExtendWith(MockitoExtension.class)
class CheckInControllerTest {

    @Mock
    private CheckInService checkInService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {

        CheckInController checkInController =
                new CheckInController(checkInService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(checkInController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    void deveRealizarCheckInComSucesso() throws Exception {

        // Arrange
        Long hospedeId = 1L;

        CheckInRequest request = new CheckInRequest();
        request.setResponsavel("Robson");
        request.setObservacao("Hóspede chegou normalmente");

        CheckInResponse response = new CheckInResponse();
        response.setHospedeId(hospedeId);
        response.setHospedeNome("João da Silva");
        response.setStatusCheckIn(StatusCheckIn.REALIZADO);
        response.setResponsavel("Robson");
        response.setObservacao("Hóspede chegou normalmente");
        response.setViagemId(1L);
        response.setViagemNome("Tomorrowland Brasil 2027");
        response.setQuartoId(2L);
        response.setQuartoNome("Suíte 01");

        when(checkInService.realizarCheckIn(
                eq(hospedeId),
                any(CheckInRequest.class)
        )).thenReturn(response);

        // Act + Assert
        mockMvc.perform(
                        post("/api/hospedes/{hospedeId}/check-in", hospedeId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hospedeId").value(1))
                .andExpect(jsonPath("$.hospedeNome").value("João da Silva"))
                .andExpect(jsonPath("$.statusCheckIn").value("REALIZADO"))
                .andExpect(jsonPath("$.responsavel").value("Robson"))
                .andExpect(jsonPath("$.observacao")
                        .value("Hóspede chegou normalmente"))
                .andExpect(jsonPath("$.viagemId").value(1))
                .andExpect(jsonPath("$.viagemNome")
                        .value("Tomorrowland Brasil 2027"))
                .andExpect(jsonPath("$.quartoId").value(2))
                .andExpect(jsonPath("$.quartoNome").value("Suíte 01"));

        verify(checkInService).realizarCheckIn(
                eq(hospedeId),
                any(CheckInRequest.class)
        );
    }

    @Test
    void deveRetornarBadRequestQuandoResponsavelEstiverVazio() throws Exception {

        // Arrange
        CheckInRequest request = new CheckInRequest();
        request.setResponsavel("");
        request.setObservacao("Teste de validação");

        // Act + Assert
        mockMvc.perform(
                        post("/api/hospedes/{hospedeId}/check-in", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());

        verify(checkInService, never())
                .realizarCheckIn(
                        any(Long.class),
                        any(CheckInRequest.class)
                );
    }

    @Test
    void deveConsultarCheckInComSucesso() throws Exception {

        // Arrange
        Long hospedeId = 1L;

        CheckInResponse response = new CheckInResponse();
        response.setHospedeId(hospedeId);
        response.setHospedeNome("João da Silva");
        response.setStatusCheckIn(StatusCheckIn.REALIZADO);
        response.setResponsavel("Robson");
        response.setObservacao("Hóspede chegou normalmente");
        response.setViagemId(1L);
        response.setViagemNome("Tomorrowland Brasil 2027");
        response.setQuartoId(2L);
        response.setQuartoNome("Suíte 01");

        when(checkInService.consultarCheckIn(hospedeId))
                .thenReturn(response);

        // Act + Assert
        mockMvc.perform(
                        get("/api/hospedes/{hospedeId}/check-in", hospedeId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hospedeId").value(1))
                .andExpect(jsonPath("$.hospedeNome").value("João da Silva"))
                .andExpect(jsonPath("$.statusCheckIn").value("REALIZADO"))
                .andExpect(jsonPath("$.responsavel").value("Robson"))
                .andExpect(jsonPath("$.observacao")
                        .value("Hóspede chegou normalmente"))
                .andExpect(jsonPath("$.viagemId").value(1))
                .andExpect(jsonPath("$.viagemNome")
                        .value("Tomorrowland Brasil 2027"))
                .andExpect(jsonPath("$.quartoId").value(2))
                .andExpect(jsonPath("$.quartoNome").value("Suíte 01"));

        verify(checkInService)
                .consultarCheckIn(hospedeId);
    }

    @Test
    void deveRetornarNotFoundAoConsultarHospedeInexistente() throws Exception {

        // Arrange
        Long hospedeId = 999L;

        when(checkInService.consultarCheckIn(hospedeId))
                .thenThrow(
                        new HospedeNaoEncontradoException(
                                "Hóspede não encontrado!"
                        )
                );

        // Act + Assert
        mockMvc.perform(
                        get("/api/hospedes/{hospedeId}/check-in", hospedeId)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("Not Found"))
                .andExpect(jsonPath("$.mensagem")
                        .value("Hóspede não encontrado!"))
                .andExpect(jsonPath("$.status").value(404));

        verify(checkInService)
                .consultarCheckIn(hospedeId);
    }


    @Test
    void deveRetornarConflictQuandoCheckInJaFoiRealizado() throws Exception {

        // Arrange
        Long hospedeId = 1L;

        CheckInRequest request = new CheckInRequest();
        request.setResponsavel("Robson");
        request.setObservacao("Tentativa de check-in duplicado");

        when(checkInService.realizarCheckIn(
                eq(hospedeId),
                any(CheckInRequest.class)
        )).thenThrow(
                new CheckInJaRealizadoException(
                        "Check-in já realizado para este hóspede!"
                )
        );

        // Act + Assert
        mockMvc.perform(
                        post("/api/hospedes/{hospedeId}/check-in", hospedeId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.erro").value("Conflict"))
                .andExpect(jsonPath("$.mensagem")
                        .value("Check-in já realizado para este hóspede!"))
                .andExpect(jsonPath("$.status").value(409));

        verify(checkInService).realizarCheckIn(
                eq(hospedeId),
                any(CheckInRequest.class)
        );
    }

    @Test
    void deveRetornarConflictQuandoHospedeNaoPossuirAlocacao() throws Exception {

        // Arrange
        Long hospedeId = 4L;

        CheckInRequest request = new CheckInRequest();
        request.setResponsavel("Robson");
        request.setObservacao("Teste de hóspede sem alocação");

        when(checkInService.realizarCheckIn(
                eq(hospedeId),
                any(CheckInRequest.class)
        )).thenThrow(
                new HospedeSemAlocacaoException(
                        "Hóspede não possui alocação de quarto!"
                )
        );

        // Act + Assert
        mockMvc.perform(
                        post("/api/hospedes/{hospedeId}/check-in", hospedeId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.erro").value("Conflict"))
                .andExpect(jsonPath("$.mensagem")
                        .value("Hóspede não possui alocação de quarto!"))
                .andExpect(jsonPath("$.status").value(409));

        verify(checkInService).realizarCheckIn(
                eq(hospedeId),
                any(CheckInRequest.class)
        );
    }

    @Test
    void deveConsultarCheckInPendenteSemAlocacao() throws Exception {

        // Arrange
        Long hospedeId = 4L;

        CheckInResponse response = new CheckInResponse();
        response.setHospedeId(hospedeId);
        response.setHospedeNome("Carlos Henrique");
        response.setStatusCheckIn(StatusCheckIn.PENDENTE);
        response.setViagemId(1L);
        response.setViagemNome("Tomorrowland Brasil 2027");

        when(checkInService.consultarCheckIn(hospedeId))
                .thenReturn(response);

        // Act + Assert
        mockMvc.perform(
                        get("/api/hospedes/{hospedeId}/check-in", hospedeId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hospedeId").value(4))
                .andExpect(jsonPath("$.hospedeNome").value("Carlos Henrique"))
                .andExpect(jsonPath("$.statusCheckIn").value("PENDENTE"))
                .andExpect(jsonPath("$.dataHoraCheckIn").doesNotExist())
                .andExpect(jsonPath("$.responsavel").doesNotExist())
                .andExpect(jsonPath("$.observacao").doesNotExist())
                .andExpect(jsonPath("$.quartoId").doesNotExist())
                .andExpect(jsonPath("$.quartoNome").doesNotExist())
                .andExpect(jsonPath("$.viagemId").value(1))
                .andExpect(jsonPath("$.viagemNome")
                        .value("Tomorrowland Brasil 2027"));

        verify(checkInService)
                .consultarCheckIn(hospedeId);
    }

}