package com.bths.platform.viagem;

import com.bths.platform.exception.ViagemNaoEncontradaException;
import com.bths.platform.viagem.dto.ViagemResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@WebMvcTest(ViagemController.class)
class ViagemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ViagemService viagemService;


    @Test
    void deveCadastrarViagemERetornar201() throws Exception {

        ViagemResponse response = new ViagemResponse();

        response.setId(1L);
        response.setNome("Tomorrowland Brasil 2027");
        response.setEvento("Tomorrowland Brasil");
        response.setDataInicio(LocalDate.of(2027, 4, 29));
        response.setDataFim(LocalDate.of(2027, 5, 3));
        response.setCidade("Alumínio");
        response.setEstado("SP");
        response.setStatus(StatusViagem.PLANEJADA);

        when(viagemService.cadastrarViagem(any()))
                .thenReturn(response);

        String json = """
                {
                    "nome": "Tomorrowland Brasil 2027",
                    "evento": "Tomorrowland Brasil",
                    "dataInicio": "2027-04-29",
                    "dataFim": "2027-05-03",
                    "endereco": "Estrada dos Engenheiros",
                    "cidade": "Alumínio",
                    "estado": "SP",
                    "status": "PLANEJADA"
                }
                """;

        mockMvc.perform(
                        post("/api/viagens")
                                .contentType("application/json")
                                .content(json)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome")
                        .value("Tomorrowland Brasil 2027"))
                .andExpect(jsonPath("$.status")
                        .value("PLANEJADA"));
    }

    @Test
    void deveRetornar400QuandoDadosObrigatoriosForemInvalidos() throws Exception {

        String json = """
            {
                "nome": "",
                "evento": "",
                "dataInicio": null,
                "dataFim": null,
                "cidade": "Alumínio",
                "estado": "SP",
                "status": null
            }
            """;

        mockMvc.perform(
                        post("/api/viagens")
                                .contentType("application/json")
                                .content(json)
                )
                .andExpect(status().isBadRequest());

        verify(viagemService, never())
                .cadastrarViagem(any());
    }

    @Test
    void deveListarViagensERetornar200() throws Exception {

        ViagemResponse viagem1 = new ViagemResponse();
        viagem1.setId(1L);
        viagem1.setNome("Tomorrowland Brasil 2027");
        viagem1.setEvento("Tomorrowland Brasil");
        viagem1.setDataInicio(LocalDate.of(2027, 4, 29));
        viagem1.setDataFim(LocalDate.of(2027, 5, 3));
        viagem1.setStatus(StatusViagem.PLANEJADA);

        ViagemResponse viagem2 = new ViagemResponse();
        viagem2.setId(2L);
        viagem2.setNome("Tomorrowland Brasil 2028");
        viagem2.setEvento("Tomorrowland Brasil");
        viagem2.setDataInicio(LocalDate.of(2028, 4, 27));
        viagem2.setDataFim(LocalDate.of(2028, 5, 1));
        viagem2.setStatus(StatusViagem.PLANEJADA);

        when(viagemService.listarViagens())
                .thenReturn(List.of(viagem1, viagem2));

        mockMvc.perform(get("/api/viagens"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nome")
                        .value("Tomorrowland Brasil 2027"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].nome")
                        .value("Tomorrowland Brasil 2028"));
    }

    @Test
    void deveBuscarViagemPorIdERetornar200() throws Exception {

        Long id = 1L;

        ViagemResponse response = new ViagemResponse();
        response.setId(id);
        response.setNome("Tomorrowland Brasil 2027");
        response.setEvento("Tomorrowland Brasil");
        response.setDataInicio(LocalDate.of(2027, 4, 29));
        response.setDataFim(LocalDate.of(2027, 5, 3));
        response.setCidade("Alumínio");
        response.setEstado("SP");
        response.setStatus(StatusViagem.PLANEJADA);

        when(viagemService.buscarViagemPorId(id))
                .thenReturn(response);

        mockMvc.perform(get("/api/viagens/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome")
                        .value("Tomorrowland Brasil 2027"))
                .andExpect(jsonPath("$.evento")
                        .value("Tomorrowland Brasil"))
                .andExpect(jsonPath("$.status")
                        .value("PLANEJADA"));

        verify(viagemService).buscarViagemPorId(id);
    }

    @Test
    void deveRetornar404QuandoViagemNaoForEncontrada() throws Exception {

        Long id = 999L;

        when(viagemService.buscarViagemPorId(id))
                .thenThrow(new ViagemNaoEncontradaException("Viagem não encontrada!"));

        mockMvc.perform(get("/api/viagens/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro")
                        .value("Not Found"))
                .andExpect(jsonPath("$.mensagem")
                        .value("Viagem não encontrada!"))
                .andExpect(jsonPath("$.status")
                        .value(404));

        verify(viagemService).buscarViagemPorId(id);
    }

    @Test
    void deveAtualizarViagemERetornar200() throws Exception {

        Long id = 1L;

        ViagemResponse response = new ViagemResponse();
        response.setId(id);
        response.setNome("Tomorrowland Brasil 2027 - Atualizado");
        response.setEvento("Tomorrowland Brasil");
        response.setDataInicio(LocalDate.of(2027, 4, 29));
        response.setDataFim(LocalDate.of(2027, 5, 4));
        response.setCidade("Alumínio");
        response.setEstado("SP");
        response.setStatus(StatusViagem.PLANEJADA);

        when(viagemService.atualizarViagem(eq(id), any()))
                .thenReturn(response);

        String json = """
            {
                "nome": "Tomorrowland Brasil 2027 - Atualizado",
                "evento": "Tomorrowland Brasil",
                "dataInicio": "2027-04-29",
                "dataFim": "2027-05-04",
                "endereco": "Estrada dos Engenheiros",
                "cidade": "Alumínio",
                "estado": "SP",
                "status": "PLANEJADA"
            }
            """;

        mockMvc.perform(
                        put("/api/viagens/{id}", id)
                                .contentType("application/json")
                                .content(json)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome")
                        .value("Tomorrowland Brasil 2027 - Atualizado"))
                .andExpect(jsonPath("$.dataFim")
                        .value("2027-05-04"))
                .andExpect(jsonPath("$.status")
                        .value("PLANEJADA"));

        verify(viagemService)
                .atualizarViagem(eq(id), any());
    }

    @Test
    void deveRetornar404AoAtualizarViagemInexistente() throws Exception {

        Long id = 999L;

        when(viagemService.atualizarViagem(eq(id), any()))
                .thenThrow(
                        new ViagemNaoEncontradaException("Viagem não encontrada!")
                );

        String json = """
            {
                "nome": "Tomorrowland Brasil 2027",
                "evento": "Tomorrowland Brasil",
                "dataInicio": "2027-04-29",
                "dataFim": "2027-05-04",
                "endereco": "Estrada dos Engenheiros",
                "cidade": "Alumínio",
                "estado": "SP",
                "status": "PLANEJADA"
            }
            """;

        mockMvc.perform(
                        put("/api/viagens/{id}", id)
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

        verify(viagemService)
                .atualizarViagem(eq(id), any());
    }

    @Test
    void deveDeletarViagemERetornar204() throws Exception {

        Long id = 1L;

        mockMvc.perform(
                        delete("/api/viagens/{id}", id)
                )
                .andExpect(status().isNoContent());

        verify(viagemService).deletarViagem(id);
    }

    @Test
    void deveRetornar404AoDeletarViagemInexistente() throws Exception {

        Long id = 999L;

        doThrow(
                new ViagemNaoEncontradaException("Viagem não encontrada!")
        )
                .when(viagemService)
                .deletarViagem(id);

        mockMvc.perform(
                        delete("/api/viagens/{id}", id)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro")
                        .value("Not Found"))
                .andExpect(jsonPath("$.mensagem")
                        .value("Viagem não encontrada!"))
                .andExpect(jsonPath("$.status")
                        .value(404));

        verify(viagemService).deletarViagem(id);
    }

}

