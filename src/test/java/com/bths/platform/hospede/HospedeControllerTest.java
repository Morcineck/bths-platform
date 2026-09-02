package com.bths.platform.hospede;

import com.bths.platform.exception.HospedeJaCadastradoException;
import com.bths.platform.exception.HospedeNaoEncontradoException;
import com.bths.platform.hospede.dto.HospedeResponse;
import com.bths.platform.hospede.enums.StatusCheckIn;
import com.bths.platform.security.JwtService;
import com.bths.platform.security.UsuarioDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.mockito.Mockito.never;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@WebMvcTest(HospedeController.class)
@AutoConfigureMockMvc(addFilters = false)
class HospedeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HospedeService hospedeService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UsuarioDetailsService usuarioDetailsService;

    @Test
    void deveCadastrarHospedeERetornar201() throws Exception {

        HospedeResponse response = new HospedeResponse();
        response.setId(1L);
        response.setNomeCompleto("Maria Oliveira");
        response.setCpf("98765432100");
        response.setStatusCheckIn(StatusCheckIn.PENDENTE);
        response.setViagemId(1L);
        response.setViagemNome("Tomorrowland Brasil 2027");

        when(hospedeService.cadastrarHospede(any()))
                .thenReturn(response);

        String json = """
            {
                "nomeCompleto": "Maria Oliveira",
                "cpf": "98765432100",
                "telefone": "11988888888",
                "email": "maria@email.com",
                "dataNascimento": "1998-03-15",
                "horarioPrevistoChegada": "2027-04-29T15:00:00",
                "statusCheckIn": "PENDENTE",
                "viagemId": 1
            }
            """;

        mockMvc.perform(
                        post("/api/hospedes")
                                .contentType("application/json")
                                .content(json)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nomeCompleto").value("Maria Oliveira"))
                .andExpect(jsonPath("$.cpf").value("98765432100"))
                .andExpect(jsonPath("$.statusCheckIn").value("PENDENTE"))
                .andExpect(jsonPath("$.viagemId").value(1));
    }

    @Test
    void deveBuscarHospedePorIdERetornar200() throws Exception {

        HospedeResponse response = new HospedeResponse();
        response.setId(1L);
        response.setNomeCompleto("Maria Oliveira");
        response.setCpf("98765432100");
        response.setStatusCheckIn(StatusCheckIn.PENDENTE);
        response.setViagemId(1L);
        response.setViagemNome("Tomorrowland Brasil 2027");

        when(hospedeService.buscarHospedePorId(1L))
                .thenReturn(response);

        mockMvc.perform(
                        get("/api/hospedes/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nomeCompleto").value("Maria Oliveira"))
                .andExpect(jsonPath("$.cpf").value("98765432100"))
                .andExpect(jsonPath("$.statusCheckIn").value("PENDENTE"))
                .andExpect(jsonPath("$.viagemId").value(1))
                .andExpect(jsonPath("$.viagemNome")
                        .value("Tomorrowland Brasil 2027"));
    }

    @Test
    void deveListarHospedesERetornar200() throws Exception {

        HospedeResponse hospede1 = new HospedeResponse();
        hospede1.setId(1L);
        hospede1.setNomeCompleto("João da Silva");
        hospede1.setCpf("12345678900");
        hospede1.setStatusCheckIn(StatusCheckIn.PENDENTE);
        hospede1.setViagemId(1L);

        HospedeResponse hospede2 = new HospedeResponse();
        hospede2.setId(2L);
        hospede2.setNomeCompleto("Maria Oliveira");
        hospede2.setCpf("98765432100");
        hospede2.setStatusCheckIn(StatusCheckIn.REALIZADO);
        hospede2.setViagemId(1L);

        when(hospedeService.listarHospedes())
                .thenReturn(List.of(hospede1, hospede2));

        mockMvc.perform(
                        get("/api/hospedes")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nomeCompleto").value("João da Silva"))
                .andExpect(jsonPath("$[0].statusCheckIn").value("PENDENTE"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].nomeCompleto").value("Maria Oliveira"))
                .andExpect(jsonPath("$[1].statusCheckIn").value("REALIZADO"));
    }

    @Test
    void deveAtualizarHospedeERetornar200() throws Exception {

        HospedeResponse response = new HospedeResponse();
        response.setId(1L);
        response.setNomeCompleto("Maria Oliveira Santos");
        response.setCpf("98765432100");
        response.setStatusCheckIn(StatusCheckIn.REALIZADO);
        response.setViagemId(1L);
        response.setViagemNome("Tomorrowland Brasil 2027");

        when(hospedeService.atualizarHospede(
                eq(1L),
                any()
        )).thenReturn(response);

        String json = """
            {
                "nomeCompleto": "Maria Oliveira Santos",
                "cpf": "98765432100",
                "telefone": "11977777777",
                "email": "maria.santos@email.com",
                "dataNascimento": "1998-03-15",
                "horarioPrevistoChegada": "2027-04-29T16:00:00",
                "statusCheckIn": "REALIZADO",
                "viagemId": 1
            }
            """;

        mockMvc.perform(
                        put("/api/hospedes/1")
                                .contentType("application/json")
                                .content(json)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nomeCompleto")
                        .value("Maria Oliveira Santos"))
                .andExpect(jsonPath("$.cpf")
                        .value("98765432100"))
                .andExpect(jsonPath("$.statusCheckIn")
                        .value("REALIZADO"))
                .andExpect(jsonPath("$.viagemId").value(1));
    }

    @Test
    void deveDeletarHospedeERetornar204() throws Exception {

        Long id = 1L;

        doNothing()
                .when(hospedeService)
                .deletarHospede(id);

        mockMvc.perform(
                        delete("/api/hospedes/1")
                )
                .andExpect(status().isNoContent());

        verify(hospedeService).deletarHospede(id);
    }

    @Test
    void deveRetornar404QuandoHospedeNaoForEncontrado() throws Exception {

        Long id = 999L;

        when(hospedeService.buscarHospedePorId(id))
                .thenThrow(
                        new HospedeNaoEncontradoException(
                                "Hóspede não encontrado!"
                        )
                );

        mockMvc.perform(
                        get("/api/hospedes/999")
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro")
                        .value("Not Found"))
                .andExpect(jsonPath("$.mensagem")
                        .value("Hóspede não encontrado!"))
                .andExpect(jsonPath("$.status")
                        .value(404));
    }

    @Test
    void deveRetornar409QuandoCpfJaEstiverCadastradoNaViagem() throws Exception {

        when(hospedeService.cadastrarHospede(any()))
                .thenThrow(
                        new HospedeJaCadastradoException(
                                "CPF já cadastrado nesta viagem!"
                        )
                );

        String json = """
            {
                "nomeCompleto": "Maria Oliveira",
                "cpf": "98765432100",
                "telefone": "11988888888",
                "email": "maria@email.com",
                "dataNascimento": "1998-03-15",
                "horarioPrevistoChegada": "2027-04-29T15:00:00",
                "statusCheckIn": "PENDENTE",
                "viagemId": 1
            }
            """;

        mockMvc.perform(
                        post("/api/hospedes")
                                .contentType("application/json")
                                .content(json)
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.erro")
                        .value("Conflict"))
                .andExpect(jsonPath("$.mensagem")
                        .value("CPF já cadastrado nesta viagem!"))
                .andExpect(jsonPath("$.status")
                        .value(409));
    }

    @Test
    void deveRetornar400QuandoNomeCompletoNaoForInformado() throws Exception {

        String json = """
            {
                "cpf": "98765432100",
                "telefone": "11988888888",
                "email": "maria@email.com",
                "dataNascimento": "1998-03-15",
                "horarioPrevistoChegada": "2027-04-29T15:00:00",
                "statusCheckIn": "PENDENTE",
                "viagemId": 1
            }
            """;

        mockMvc.perform(
                        post("/api/hospedes")
                                .contentType("application/json")
                                .content(json)
                )
                .andExpect(status().isBadRequest());

        verify(hospedeService, never())
                .cadastrarHospede(any());
    }

    @Test
    void deveRetornar400QuandoCpfForInvalido() throws Exception {

        String json = """
            {
                "nomeCompleto": "Maria Oliveira",
                "cpf": "12345",
                "telefone": "11988888888",
                "email": "maria@email.com",
                "dataNascimento": "1998-03-15",
                "horarioPrevistoChegada": "2027-04-29T15:00:00",
                "statusCheckIn": "PENDENTE",
                "viagemId": 1
            }
            """;

        mockMvc.perform(
                        post("/api/hospedes")
                                .contentType("application/json")
                                .content(json)
                )
                .andExpect(status().isBadRequest());

        verify(hospedeService, never())
                .cadastrarHospede(any());
    }

    @Test
    void deveRetornar400QuandoEmailForInvalido() throws Exception {

        String json = """
            {
                "nomeCompleto": "Maria Oliveira",
                "cpf": "98765432100",
                "telefone": "11988888888",
                "email": "email-invalido",
                "dataNascimento": "1998-03-15",
                "horarioPrevistoChegada": "2027-04-29T15:00:00",
                "statusCheckIn": "PENDENTE",
                "viagemId": 1
            }
            """;

        mockMvc.perform(
                        post("/api/hospedes")
                                .contentType("application/json")
                                .content(json)
                )
                .andExpect(status().isBadRequest());

        verify(hospedeService, never())
                .cadastrarHospede(any());
    }

    @Test
    void deveRetornar400QuandoStatusCheckInNaoForInformado() throws Exception {

        String json = """
            {
                "nomeCompleto": "Maria Oliveira",
                "cpf": "98765432100",
                "telefone": "11988888888",
                "email": "maria@email.com",
                "dataNascimento": "1998-03-15",
                "horarioPrevistoChegada": "2027-04-29T15:00:00",
                "viagemId": 1
            }
            """;

        mockMvc.perform(
                        post("/api/hospedes")
                                .contentType("application/json")
                                .content(json)
                )
                .andExpect(status().isBadRequest());

        verify(hospedeService, never())
                .cadastrarHospede(any());
    }

    @Test
    void deveRetornar400QuandoViagemIdNaoForInformado() throws Exception {

        String json = """
            {
                "nomeCompleto": "Maria Oliveira",
                "cpf": "98765432100",
                "telefone": "11988888888",
                "email": "maria@email.com",
                "dataNascimento": "1998-03-15",
                "horarioPrevistoChegada": "2027-04-29T15:00:00",
                "statusCheckIn": "PENDENTE"
            }
            """;

        mockMvc.perform(
                        post("/api/hospedes")
                                .contentType("application/json")
                                .content(json)
                )
                .andExpect(status().isBadRequest());

        verify(hospedeService, never())
                .cadastrarHospede(any());
    }


}