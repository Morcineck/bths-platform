package com.bths.platform.traslado;

import com.bths.platform.security.JwtService;
import com.bths.platform.security.UsuarioDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@WebMvcTest(TrasladoController.class)
@AutoConfigureMockMvc(addFilters = false)
class TrasladoValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TrasladoService trasladoService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UsuarioDetailsService usuarioDetailsService;

    @Test
    void deveRetornar400QuandoCamposObrigatoriosNaoForemInformados()
            throws Exception {

        String json = """
            {
            }
            """;

        mockMvc.perform(
                        post("/api/traslados")
                                .contentType("application/json")
                                .content(json)
                )
                .andExpect(status().isBadRequest());

        verify(trasladoService, never())
                .cadastrarTraslado(any());
    }

    @Test
    void deveRetornar400QuandoStatusNaoForInformado()
            throws Exception {

        String json = """
        {
        }
        """;

        mockMvc.perform(
                        patch("/api/traslados/1/status")
                                .contentType("application/json")
                                .content(json)
                )
                .andExpect(status().isBadRequest());

        verify(trasladoService, never())
                .atualizarStatusTraslado(any(), any());
    }

    @Test
    void deveRetornar400QuandoStatusDaCorrecaoNaoForInformado()
            throws Exception {

        String json = """
        {
            "motivo": "Correção operacional"
        }
        """;

        mockMvc.perform(
                        patch("/api/traslados/1/corrigir-status")
                                .contentType("application/json")
                                .content(json)
                )
                .andExpect(status().isBadRequest());

        verify(trasladoService, never())
                .corrigirStatusTraslado(any(), any());
    }

    @Test
    void deveRetornar400QuandoMotivoDaCorrecaoNaoForInformado()
            throws Exception {

        String json = """
        {
            "status": "EM_ANDAMENTO"
        }
        """;

        mockMvc.perform(
                        patch("/api/traslados/1/corrigir-status")
                                .contentType("application/json")
                                .content(json)
                )
                .andExpect(status().isBadRequest());

        verify(trasladoService, never())
                .corrigirStatusTraslado(any(), any());
    }

    @Test
    void deveRetornar400QuandoCamposObrigatoriosDaAtualizacaoNaoForemInformados()
            throws Exception {

        String json = """
        {
        }
        """;

        mockMvc.perform(
                        put("/api/traslados/1")
                                .contentType("application/json")
                                .content(json)
                )
                .andExpect(status().isBadRequest());

        verify(trasladoService, never())
                .atualizarTraslado(any(), any());
    }
}