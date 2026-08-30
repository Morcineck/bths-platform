package com.bths.platform.quarto;

import com.bths.platform.exception.QuartoNaoEncontradoException;
import com.bths.platform.exception.ViagemNaoEncontradaException;
import com.bths.platform.quarto.dto.QuartoResponse;
import com.bths.platform.quarto.enums.StatusQuarto;
import com.bths.platform.quarto.enums.TipoQuarto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.mockito.Mockito.never;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(QuartoController.class)
class QuartoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private QuartoService quartoService;


    @Test
    void deveCadastrarQuartoERetornar201() throws Exception {

        QuartoResponse response = new QuartoResponse();
        response.setId(1L);
        response.setNome("Suíte 01");
        response.setTipo(TipoQuarto.SUITE);
        response.setCapacidade(6);
        response.setStatus(StatusQuarto.DISPONIVEL);
        response.setViagemId(1L);
        response.setViagemNome("Tomorrowland Brasil 2027");

        when(quartoService.cadastrarQuarto(any()))
                .thenReturn(response);

        String json = """
                {
                    "nome": "Suíte 01",
                    "tipo": "SUITE",
                    "capacidade": 6,
                    "status": "DISPONIVEL",
                    "viagemId": 1
                }
                """;

        mockMvc.perform(
                        post("/api/quartos")
                                .contentType("application/json")
                                .content(json)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Suíte 01"))
                .andExpect(jsonPath("$.tipo").value("SUITE"))
                .andExpect(jsonPath("$.capacidade").value(6))
                .andExpect(jsonPath("$.status").value("DISPONIVEL"))
                .andExpect(jsonPath("$.viagemId").value(1))
                .andExpect(jsonPath("$.viagemNome")
                        .value("Tomorrowland Brasil 2027"));
    }

    @Test
    void deveBuscarQuartoPorIdERetornar200() throws Exception {

        QuartoResponse response = new QuartoResponse();
        response.setId(1L);
        response.setNome("Suíte 01");
        response.setTipo(TipoQuarto.SUITE);
        response.setCapacidade(6);
        response.setStatus(StatusQuarto.DISPONIVEL);
        response.setViagemId(1L);
        response.setViagemNome("Tomorrowland Brasil 2027");

        when(quartoService.buscarQuartoPorId(1L))
                .thenReturn(response);

        mockMvc.perform(
                        get("/api/quartos/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Suíte 01"))
                .andExpect(jsonPath("$.tipo").value("SUITE"))
                .andExpect(jsonPath("$.capacidade").value(6))
                .andExpect(jsonPath("$.status").value("DISPONIVEL"))
                .andExpect(jsonPath("$.viagemId").value(1))
                .andExpect(jsonPath("$.viagemNome")
                        .value("Tomorrowland Brasil 2027"));
    }

    @Test
    void deveListarQuartosERetornar200() throws Exception {

        QuartoResponse quarto1 = new QuartoResponse();
        quarto1.setId(1L);
        quarto1.setNome("Suíte 01");
        quarto1.setTipo(TipoQuarto.SUITE);
        quarto1.setCapacidade(6);
        quarto1.setStatus(StatusQuarto.DISPONIVEL);
        quarto1.setViagemId(1L);

        QuartoResponse quarto2 = new QuartoResponse();
        quarto2.setId(2L);
        quarto2.setNome("Alojamento 01");
        quarto2.setTipo(TipoQuarto.ALOJAMENTO);
        quarto2.setCapacidade(18);
        quarto2.setStatus(StatusQuarto.DISPONIVEL);
        quarto2.setViagemId(1L);

        when(quartoService.listarQuartos())
                .thenReturn(List.of(quarto1, quarto2));

        mockMvc.perform(
                        get("/api/quartos")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))

                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nome").value("Suíte 01"))
                .andExpect(jsonPath("$[0].tipo").value("SUITE"))
                .andExpect(jsonPath("$[0].capacidade").value(6))

                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].nome").value("Alojamento 01"))
                .andExpect(jsonPath("$[1].tipo").value("ALOJAMENTO"))
                .andExpect(jsonPath("$[1].capacidade").value(18));
    }

    @Test
    void deveAtualizarQuartoERetornar200() throws Exception {

        QuartoResponse response = new QuartoResponse();
        response.setId(1L);
        response.setNome("Suíte 01 Premium");
        response.setTipo(TipoQuarto.SUITE);
        response.setCapacidade(8);
        response.setStatus(StatusQuarto.DISPONIVEL);
        response.setViagemId(1L);
        response.setViagemNome("Tomorrowland Brasil 2027");

        when(quartoService.atualizarQuarto(
                eq(1L),
                any()
        )).thenReturn(response);

        String json = """
            {
                "nome": "Suíte 01 Premium",
                "tipo": "SUITE",
                "capacidade": 8,
                "status": "DISPONIVEL",
                "viagemId": 1
            }
            """;

        mockMvc.perform(
                        put("/api/quartos/1")
                                .contentType("application/json")
                                .content(json)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome")
                        .value("Suíte 01 Premium"))
                .andExpect(jsonPath("$.tipo").value("SUITE"))
                .andExpect(jsonPath("$.capacidade").value(8))
                .andExpect(jsonPath("$.status").value("DISPONIVEL"))
                .andExpect(jsonPath("$.viagemId").value(1))
                .andExpect(jsonPath("$.viagemNome")
                        .value("Tomorrowland Brasil 2027"));
    }

    @Test
    void deveDeletarQuartoERetornar204() throws Exception {

        Long id = 1L;

        doNothing()
                .when(quartoService)
                .deletarQuarto(id);

        mockMvc.perform(
                        delete("/api/quartos/1")
                )
                .andExpect(status().isNoContent());

        verify(quartoService).deletarQuarto(id);
    }

    @Test
    void deveRetornar404QuandoQuartoNaoForEncontrado() throws Exception {

        Long id = 999L;

        when(quartoService.buscarQuartoPorId(id))
                .thenThrow(
                        new QuartoNaoEncontradoException(
                                "Quarto não encontrado!"
                        )
                );

        mockMvc.perform(
                        get("/api/quartos/999")
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
    void deveRetornar404AoCadastrarQuartoComViagemInexistente() throws Exception {

        when(quartoService.cadastrarQuarto(any()))
                .thenThrow(
                        new ViagemNaoEncontradaException(
                                "Viagem não encontrada!"
                        )
                );

        String json = """
            {
                "nome": "Suíte 01",
                "tipo": "SUITE",
                "capacidade": 6,
                "status": "DISPONIVEL",
                "viagemId": 999
            }
            """;

        mockMvc.perform(
                        post("/api/quartos")
                                .contentType("application/json")
                                .content(json)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro")
                        .value("Not Found"))
                .andExpect(jsonPath("$.mensagem")
                        .value("Viagem não encontrada!"))
                .andExpect(jsonPath("$.status")
                        .value(404));
    }

    @Test
    void deveRetornar400QuandoCapacidadeForInvalida() throws Exception {

        String json = """
            {
                "nome": "Suíte 01",
                "tipo": "SUITE",
                "capacidade": 0,
                "status": "DISPONIVEL",
                "viagemId": 1
            }
            """;

        mockMvc.perform(
                        post("/api/quartos")
                                .contentType("application/json")
                                .content(json)
                )
                .andExpect(status().isBadRequest());

        verify(quartoService, never())
                .cadastrarQuarto(any());
    }

    @Test
    void deveRetornar400QuandoNomeNaoForInformado() throws Exception {

        String json = """
            {
                "nome": "",
                "tipo": "SUITE",
                "capacidade": 6,
                "status": "DISPONIVEL",
                "viagemId": 1
            }
            """;

        mockMvc.perform(
                        post("/api/quartos")
                                .contentType("application/json")
                                .content(json)
                )
                .andExpect(status().isBadRequest());

        verify(quartoService, never())
                .cadastrarQuarto(any());
    }

    @Test
    void deveRetornar400QuandoViagemIdNaoForInformado() throws Exception {

        String json = """
            {
                "nome": "Suíte 01",
                "tipo": "SUITE",
                "capacidade": 6,
                "status": "DISPONIVEL"
            }
            """;

        mockMvc.perform(
                        post("/api/quartos")
                                .contentType("application/json")
                                .content(json)
                )
                .andExpect(status().isBadRequest());

        verify(quartoService, never())
                .cadastrarQuarto(any());
    }

    @Test
    void deveRetornar400QuandoTipoNaoForInformado() throws Exception {

        String json = """
            {
                "nome": "Suíte 01",
                "capacidade": 6,
                "status": "DISPONIVEL",
                "viagemId": 1
            }
            """;

        mockMvc.perform(
                        post("/api/quartos")
                                .contentType("application/json")
                                .content(json)
                )
                .andExpect(status().isBadRequest());

        verify(quartoService, never())
                .cadastrarQuarto(any());
    }

    @Test
    void deveRetornar400QuandoStatusNaoForInformado() throws Exception {

        String json = """
            {
                "nome": "Suíte 01",
                "tipo": "SUITE",
                "capacidade": 6,
                "viagemId": 1
            }
            """;

        mockMvc.perform(
                        post("/api/quartos")
                                .contentType("application/json")
                                .content(json)
                )
                .andExpect(status().isBadRequest());

        verify(quartoService, never())
                .cadastrarQuarto(any());
    }
}