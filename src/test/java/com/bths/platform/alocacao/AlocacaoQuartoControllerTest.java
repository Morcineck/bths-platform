package com.bths.platform.alocacao;


import com.bths.platform.alocacao.dto.AlocacaoQuartoResponse;

import com.bths.platform.alocacao.dto.OcupacaoQuartoResponse;
import com.bths.platform.exception.*;
import com.bths.platform.security.JwtService;
import com.bths.platform.security.UsuarioDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.never;

@WebMvcTest(AlocacaoQuartoController.class)
@AutoConfigureMockMvc(addFilters = false)
class AlocacaoQuartoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AlocacaoService alocacaoService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UsuarioDetailsService usuarioDetailsService;

    @Test
    void deveAlocarHospedeComSucesso() throws Exception {

        AlocacaoQuartoResponse response =
                new AlocacaoQuartoResponse();

        response.setId(1L);
        response.setHospedeId(1L);
        response.setHospedeNome("João da Silva");
        response.setQuartoId(2L);
        response.setQuartoNome("Suíte 01");
        response.setViagemId(1L);
        response.setViagemNome("Tomorrowland Brasil 2027");

        when(alocacaoService.alocarHospede(any()))
                .thenReturn(response);

        String requestJson = """
            {
                "hospedeId": 1,
                "quartoId": 2
            }
            """;

        mockMvc.perform(
                        post("/api/alocacoes-quartos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.hospedeId").value(1))
                .andExpect(jsonPath("$.hospedeNome").value("João da Silva"))
                .andExpect(jsonPath("$.quartoId").value(2))
                .andExpect(jsonPath("$.quartoNome").value("Suíte 01"))
                .andExpect(jsonPath("$.viagemId").value(1))
                .andExpect(jsonPath("$.viagemNome")
                        .value("Tomorrowland Brasil 2027"));
    }

    @Test
    void deveBuscarAlocacaoPorIdComSucesso() throws Exception {

        AlocacaoQuartoResponse response =
                new AlocacaoQuartoResponse();

        response.setId(1L);
        response.setHospedeId(1L);
        response.setHospedeNome("João da Silva");
        response.setQuartoId(2L);
        response.setQuartoNome("Suíte 01");
        response.setViagemId(1L);
        response.setViagemNome("Tomorrowland Brasil 2027");

        when(alocacaoService.buscarAlocacaoPorId(1L))
                .thenReturn(response);

        mockMvc.perform(
                        get("/api/alocacoes-quartos/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.hospedeId").value(1))
                .andExpect(jsonPath("$.hospedeNome")
                        .value("João da Silva"))
                .andExpect(jsonPath("$.quartoId").value(2))
                .andExpect(jsonPath("$.quartoNome")
                        .value("Suíte 01"))
                .andExpect(jsonPath("$.viagemId").value(1))
                .andExpect(jsonPath("$.viagemNome")
                        .value("Tomorrowland Brasil 2027"));
    }

    @Test
    void deveListarAlocacoesComSucesso() throws Exception {

        AlocacaoQuartoResponse response1 =
                new AlocacaoQuartoResponse();

        response1.setId(1L);
        response1.setHospedeId(1L);
        response1.setHospedeNome("João da Silva");
        response1.setQuartoId(2L);
        response1.setQuartoNome("Suíte 01");

        AlocacaoQuartoResponse response2 =
                new AlocacaoQuartoResponse();

        response2.setId(2L);
        response2.setHospedeId(2L);
        response2.setHospedeNome("Maria Oliveira Santos");
        response2.setQuartoId(3L);
        response2.setQuartoNome("Suíte 02");

        when(alocacaoService.listarAlocacoes())
                .thenReturn(List.of(response1, response2));

        mockMvc.perform(
                        get("/api/alocacoes-quartos")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].hospedeNome")
                        .value("João da Silva"))
                .andExpect(jsonPath("$[0].quartoNome")
                        .value("Suíte 01"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].hospedeNome")
                        .value("Maria Oliveira Santos"))
                .andExpect(jsonPath("$[1].quartoNome")
                        .value("Suíte 02"));
    }

    @Test
    void deveListarAlocacoesPorQuartoComSucesso() throws Exception {

        Long quartoId = 2L;

        AlocacaoQuartoResponse response1 =
                new AlocacaoQuartoResponse();

        response1.setId(1L);
        response1.setHospedeId(1L);
        response1.setHospedeNome("João da Silva");
        response1.setQuartoId(quartoId);
        response1.setQuartoNome("Suíte 01");

        AlocacaoQuartoResponse response2 =
                new AlocacaoQuartoResponse();

        response2.setId(2L);
        response2.setHospedeId(2L);
        response2.setHospedeNome("Maria Oliveira Santos");
        response2.setQuartoId(quartoId);
        response2.setQuartoNome("Suíte 01");

        when(alocacaoService.listarAlocacoesPorQuarto(quartoId))
                .thenReturn(List.of(response1, response2));

        mockMvc.perform(
                        get("/api/alocacoes-quartos/quarto/2")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].hospedeId").value(1))
                .andExpect(jsonPath("$[0].hospedeNome")
                        .value("João da Silva"))
                .andExpect(jsonPath("$[0].quartoId").value(2))
                .andExpect(jsonPath("$[1].hospedeId").value(2))
                .andExpect(jsonPath("$[1].hospedeNome")
                        .value("Maria Oliveira Santos"))
                .andExpect(jsonPath("$[1].quartoId").value(2));
    }

    @Test
    void deveTrocarQuartoComSucesso() throws Exception {

        AlocacaoQuartoResponse response =
                new AlocacaoQuartoResponse();

        response.setId(1L);
        response.setHospedeId(1L);
        response.setHospedeNome("João da Silva");
        response.setQuartoId(3L);
        response.setQuartoNome("Suíte 02");
        response.setViagemId(1L);
        response.setViagemNome("Tomorrowland Brasil 2027");

        when(alocacaoService.trocarQuarto(1L, 3L))
                .thenReturn(response);

        String requestJson = """
            {
                "novoQuartoId": 3
            }
            """;

        mockMvc.perform(
                        put("/api/alocacoes-quartos/1/quarto")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.hospedeId").value(1))
                .andExpect(jsonPath("$.hospedeNome")
                        .value("João da Silva"))
                .andExpect(jsonPath("$.quartoId").value(3))
                .andExpect(jsonPath("$.quartoNome")
                        .value("Suíte 02"))
                .andExpect(jsonPath("$.viagemId").value(1))
                .andExpect(jsonPath("$.viagemNome")
                        .value("Tomorrowland Brasil 2027"));
    }

    @Test
    void deveRemoverAlocacaoComSucesso() throws Exception {

        Long alocacaoId = 1L;

        mockMvc.perform(
                        delete("/api/alocacoes-quartos/{id}", alocacaoId)
                )
                .andExpect(status().isNoContent());

        verify(alocacaoService)
                .removerAlocacao(alocacaoId);
    }

    @Test
    void deveRetornar404QuandoAlocacaoNaoForEncontrada() throws Exception {

        Long alocacaoId = 999L;

        when(alocacaoService.buscarAlocacaoPorId(alocacaoId))
                .thenThrow(
                        new AlocacaoNaoEncontradaException(
                                "Alocação não encontrada!"
                        )
                );

        mockMvc.perform(
                        get("/api/alocacoes-quartos/{id}", alocacaoId)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro")
                        .value("Not Found"))
                .andExpect(jsonPath("$.mensagem")
                        .value("Alocação não encontrada!"))
                .andExpect(jsonPath("$.status")
                        .value(404));
    }

    @Test
    void deveRetornar404QuandoHospedeNaoForEncontradoAoAlocar() throws Exception {

        when(alocacaoService.alocarHospede(any()))
                .thenThrow(
                        new HospedeNaoEncontradoException(
                                "Hóspede não encontrado!"
                        )
                );

        String requestJson = """
            {
                "hospedeId": 999,
                "quartoId": 2
            }
            """;

        mockMvc.perform(
                        post("/api/alocacoes-quartos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
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
    void deveRetornar404QuandoQuartoNaoForEncontradoAoAlocar() throws Exception {

        when(alocacaoService.alocarHospede(any()))
                .thenThrow(
                        new QuartoNaoEncontradoException(
                                "Quarto não encontrado!"
                        )
                );

        String requestJson = """
            {
                "hospedeId": 1,
                "quartoId": 999
            }
            """;

        mockMvc.perform(
                        post("/api/alocacoes-quartos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro")
                        .value("Not Found"))
                .andExpect(jsonPath("$.mensagem")
                        .value("Quarto não encontrado!"))
                .andExpect(jsonPath("$.status")
                        .value(404));
    }

    @Test
    void deveRetornar409QuandoHospedeJaEstiverAlocado() throws Exception {

        when(alocacaoService.alocarHospede(any()))
                .thenThrow(
                        new HospedeJaAlocadoException(
                                "Hóspede já está alocado nesta viagem!"
                        )
                );

        String requestJson = """
            {
                "hospedeId": 1,
                "quartoId": 2
            }
            """;

        mockMvc.perform(
                        post("/api/alocacoes-quartos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.erro")
                        .value("Conflict"))
                .andExpect(jsonPath("$.mensagem")
                        .value("Hóspede já está alocado nesta viagem!"))
                .andExpect(jsonPath("$.status")
                        .value(409));
    }

    @Test
    void deveRetornar409QuandoQuartoEstiverIndisponivel() throws Exception {

        when(alocacaoService.alocarHospede(any()))
                .thenThrow(
                        new QuartoIndisponivelException(
                                "Quarto indisponível para alocação!"
                        )
                );

        String requestJson = """
            {
                "hospedeId": 1,
                "quartoId": 2
            }
            """;

        mockMvc.perform(
                        post("/api/alocacoes-quartos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.erro")
                        .value("Conflict"))
                .andExpect(jsonPath("$.mensagem")
                        .value("Quarto indisponível para alocação!"))
                .andExpect(jsonPath("$.status")
                        .value(409));
    }

    @Test
    void deveRetornar409QuandoQuartoEstiverLotado() throws Exception {

        when(alocacaoService.alocarHospede(any()))
                .thenThrow(
                        new QuartoLotadoException(
                                "Quarto lotado!"
                        )
                );

        String requestJson = """
            {
                "hospedeId": 1,
                "quartoId": 2
            }
            """;

        mockMvc.perform(
                        post("/api/alocacoes-quartos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.erro")
                        .value("Conflict"))
                .andExpect(jsonPath("$.mensagem")
                        .value("Quarto lotado!"))
                .andExpect(jsonPath("$.status")
                        .value(409));
    }

    @Test
    void deveRetornar409QuandoViagemForIncompativel() throws Exception {

        when(alocacaoService.alocarHospede(any()))
                .thenThrow(
                        new ViagemIncompativelException(
                                "Hóspede e quarto pertencem a viagens diferentes!"
                        )
                );

        String requestJson = """
            {
                "hospedeId": 1,
                "quartoId": 5
            }
            """;

        mockMvc.perform(
                        post("/api/alocacoes-quartos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.erro")
                        .value("Conflict"))
                .andExpect(jsonPath("$.mensagem")
                        .value("Hóspede e quarto pertencem a viagens diferentes!"))
                .andExpect(jsonPath("$.status")
                        .value(409));
    }

    @Test
    void deveRetornar400QuandoHospedeIdNaoForInformado() throws Exception {

        String requestJson = """
            {
                "quartoId": 2
            }
            """;

        mockMvc.perform(
                        post("/api/alocacoes-quartos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isBadRequest());

        verify(alocacaoService, never())
                .alocarHospede(any());
    }

    @Test
    void deveRetornar400QuandoQuartoIdNaoForInformado() throws Exception {

        String requestJson = """
            {
                "hospedeId": 1
            }
            """;

        mockMvc.perform(
                        post("/api/alocacoes-quartos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isBadRequest());

        verify(alocacaoService, never())
                .alocarHospede(any());
    }

    @Test
    void deveRetornar400QuandoNovoQuartoIdNaoForInformado() throws Exception {

        String requestJson = """
            {
            }
            """;

        mockMvc.perform(
                        put("/api/alocacoes-quartos/1/quarto")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isBadRequest());

        verify(alocacaoService, never())
                .trocarQuarto(anyLong(), anyLong());
    }

    @Test
    void deveRetornar404AoTrocarQuartoDeAlocacaoInexistente() throws Exception {

        Long alocacaoId = 999L;

        when(alocacaoService.trocarQuarto(alocacaoId, 3L))
                .thenThrow(
                        new AlocacaoNaoEncontradaException(
                                "Alocação não encontrada!"
                        )
                );

        String requestJson = """
            {
                "novoQuartoId": 3
            }
            """;

        mockMvc.perform(
                        put("/api/alocacoes-quartos/{id}/quarto", alocacaoId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro")
                        .value("Not Found"))
                .andExpect(jsonPath("$.mensagem")
                        .value("Alocação não encontrada!"))
                .andExpect(jsonPath("$.status")
                        .value(404));

        verify(alocacaoService)
                .trocarQuarto(alocacaoId, 3L);
    }

    @Test
    void deveRetornar404AoTrocarParaQuartoInexistente() throws Exception {

        Long alocacaoId = 1L;
        Long novoQuartoId = 999L;

        when(alocacaoService.trocarQuarto(alocacaoId, novoQuartoId))
                .thenThrow(
                        new QuartoNaoEncontradoException(
                                "Quarto não encontrado!"
                        )
                );

        String requestJson = """
            {
                "novoQuartoId": 999
            }
            """;

        mockMvc.perform(
                        put("/api/alocacoes-quartos/{id}/quarto", alocacaoId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro")
                        .value("Not Found"))
                .andExpect(jsonPath("$.mensagem")
                        .value("Quarto não encontrado!"))
                .andExpect(jsonPath("$.status")
                        .value(404));

        verify(alocacaoService)
                .trocarQuarto(alocacaoId, novoQuartoId);
    }

    @Test
    void deveRetornar409AoTrocarParaQuartoDeOutraViagem() throws Exception {

        Long alocacaoId = 1L;
        Long novoQuartoId = 5L;

        when(alocacaoService.trocarQuarto(alocacaoId, novoQuartoId))
                .thenThrow(
                        new ViagemIncompativelException(
                                "Hóspede e quarto pertencem a viagens diferentes!"
                        )
                );

        String requestJson = """
            {
                "novoQuartoId": 5
            }
            """;

        mockMvc.perform(
                        put("/api/alocacoes-quartos/{id}/quarto", alocacaoId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.erro")
                        .value("Conflict"))
                .andExpect(jsonPath("$.mensagem")
                        .value("Hóspede e quarto pertencem a viagens diferentes!"))
                .andExpect(jsonPath("$.status")
                        .value(409));

        verify(alocacaoService)
                .trocarQuarto(alocacaoId, novoQuartoId);
    }

    @Test
    void deveRetornar409AoTrocarParaQuartoIndisponivel() throws Exception {

        Long alocacaoId = 1L;
        Long novoQuartoId = 3L;

        when(alocacaoService.trocarQuarto(alocacaoId, novoQuartoId))
                .thenThrow(
                        new QuartoIndisponivelException(
                                "Quarto indisponível para alocação!"
                        )
                );

        String requestJson = """
            {
                "novoQuartoId": 3
            }
            """;

        mockMvc.perform(
                        put("/api/alocacoes-quartos/{id}/quarto", alocacaoId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.erro")
                        .value("Conflict"))
                .andExpect(jsonPath("$.mensagem")
                        .value("Quarto indisponível para alocação!"))
                .andExpect(jsonPath("$.status")
                        .value(409));

        verify(alocacaoService)
                .trocarQuarto(alocacaoId, novoQuartoId);
    }

    @Test
    void deveRetornar409AoTrocarParaQuartoLotado() throws Exception {

        Long alocacaoId = 1L;
        Long novoQuartoId = 3L;

        when(alocacaoService.trocarQuarto(alocacaoId, novoQuartoId))
                .thenThrow(
                        new QuartoLotadoException(
                                "Quarto lotado!"
                        )
                );

        String requestJson = """
            {
                "novoQuartoId": 3
            }
            """;

        mockMvc.perform(
                        put("/api/alocacoes-quartos/{id}/quarto", alocacaoId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.erro")
                        .value("Conflict"))
                .andExpect(jsonPath("$.mensagem")
                        .value("Quarto lotado!"))
                .andExpect(jsonPath("$.status")
                        .value(409));

        verify(alocacaoService)
                .trocarQuarto(alocacaoId, novoQuartoId);
    }

    @Test
    void deveBuscarOcupacaoDoQuartoComSucesso() throws Exception {

        Long quartoId = 2L;

        OcupacaoQuartoResponse response =
                new OcupacaoQuartoResponse();

        response.setQuartoId(quartoId);
        response.setQuartoNome("Suíte 01");
        response.setCapacidade(6);
        response.setOcupacao(4L);
        response.setVagasDisponiveis(2L);

        when(alocacaoService.buscarOcupacaoPorQuarto(quartoId))
                .thenReturn(response);

        mockMvc.perform(
                        get(
                                "/api/alocacoes-quartos/quarto/{quartoId}/ocupacao",
                                quartoId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quartoId").value(2))
                .andExpect(jsonPath("$.quartoNome").value("Suíte 01"))
                .andExpect(jsonPath("$.capacidade").value(6))
                .andExpect(jsonPath("$.ocupacao").value(4))
                .andExpect(jsonPath("$.vagasDisponiveis").value(2));

        verify(alocacaoService)
                .buscarOcupacaoPorQuarto(quartoId);
    }

    @Test
    void deveRetornar404AoBuscarOcupacaoDeQuartoInexistente() throws Exception {

        Long quartoId = 999L;

        when(alocacaoService.buscarOcupacaoPorQuarto(quartoId))
                .thenThrow(
                        new QuartoNaoEncontradoException(
                                "Quarto não encontrado!"
                        )
                );

        mockMvc.perform(
                        get(
                                "/api/alocacoes-quartos/quarto/{quartoId}/ocupacao",
                                quartoId
                        )
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro")
                        .value("Not Found"))
                .andExpect(jsonPath("$.mensagem")
                        .value("Quarto não encontrado!"))
                .andExpect(jsonPath("$.status")
                        .value(404));

        verify(alocacaoService)
                .buscarOcupacaoPorQuarto(quartoId);
    }
}