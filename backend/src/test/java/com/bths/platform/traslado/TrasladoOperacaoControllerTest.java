package com.bths.platform.traslado;

import com.bths.platform.security.JwtService;
import com.bths.platform.security.UsuarioDetailsService;
import com.bths.platform.traslado.dto.TrasladoOperacaoRequest;
import com.bths.platform.traslado.dto.TrasladoResponse;
import com.bths.platform.traslado.exception.MotoristaInativoException;
import com.bths.platform.traslado.exception.VeiculoInativoException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TrasladoController.class)
@AutoConfigureMockMvc(addFilters = false)
class TrasladoOperacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TrasladoService trasladoService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UsuarioDetailsService usuarioDetailsService;

    @Test
    void deveAssociarOperacaoERetornar200() throws Exception {

        TrasladoResponse response = new TrasladoResponse();
        response.setId(1L);
        response.setMotoristaId(10L);
        response.setMotoristaNome("João da Silva");
        response.setVeiculoId(20L);
        response.setVeiculoModelo("Renault Duster");
        response.setVeiculoPlaca("ABC1D23");
        response.setVeiculoCapacidadePassageiros(4);

        when(
                trasladoService.associarOperacao(
                        eq(1L),
                        any(TrasladoOperacaoRequest.class)
                )
        ).thenReturn(response);

        String json = """
                {
                    "motoristaId": 10,
                    "veiculoId": 20
                }
                """;

        mockMvc.perform(
                        patch("/api/traslados/1/operacao")
                                .contentType("application/json")
                                .content(json)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.motoristaId").value(10))
                .andExpect(jsonPath("$.motoristaNome")
                        .value("João da Silva"))
                .andExpect(jsonPath("$.veiculoId").value(20))
                .andExpect(jsonPath("$.veiculoModelo")
                        .value("Renault Duster"))
                .andExpect(jsonPath("$.veiculoPlaca")
                        .value("ABC1D23"))
                .andExpect(jsonPath("$.veiculoCapacidadePassageiros")
                        .value(4));

        verify(trasladoService)
                .associarOperacao(
                        eq(1L),
                        any(TrasladoOperacaoRequest.class)
                );
    }

    @Test
    void deveRetornar400QuandoMotoristaNaoForInformado()
            throws Exception {

        String json = """
                {
                    "veiculoId": 20
                }
                """;

        mockMvc.perform(
                        patch("/api/traslados/1/operacao")
                                .contentType("application/json")
                                .content(json)
                )
                .andExpect(status().isBadRequest());

        verify(trasladoService, never())
                .associarOperacao(
                        eq(1L),
                        any(TrasladoOperacaoRequest.class)
                );
    }

    @Test
    void deveRetornar400QuandoVeiculoNaoForInformado()
            throws Exception {

        String json = """
                {
                    "motoristaId": 10
                }
                """;

        mockMvc.perform(
                        patch("/api/traslados/1/operacao")
                                .contentType("application/json")
                                .content(json)
                )
                .andExpect(status().isBadRequest());

        verify(trasladoService, never())
                .associarOperacao(
                        eq(1L),
                        any(TrasladoOperacaoRequest.class)
                );
    }

    @Test
    void deveRetornar409QuandoMotoristaEstiverInativo()
            throws Exception {

        when(
                trasladoService.associarOperacao(
                        eq(1L),
                        any(TrasladoOperacaoRequest.class)
                )
        ).thenThrow(
                new MotoristaInativoException(
                        "Não é possível associar um motorista inativo!"
                )
        );

        String json = """
                {
                    "motoristaId": 10,
                    "veiculoId": 20
                }
                """;

        mockMvc.perform(
                        patch("/api/traslados/1/operacao")
                                .contentType("application/json")
                                .content(json)
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.erro")
                        .value("Conflict"))
                .andExpect(jsonPath("$.mensagem")
                        .value(
                                "Não é possível associar um motorista inativo!"
                        ))
                .andExpect(jsonPath("$.status")
                        .value(409));
    }

    @Test
    void deveRetornar409QuandoVeiculoEstiverInativo()
            throws Exception {

        when(
                trasladoService.associarOperacao(
                        eq(1L),
                        any(TrasladoOperacaoRequest.class)
                )
        ).thenThrow(
                new VeiculoInativoException(
                        "Não é possível associar um veículo inativo!"
                )
        );

        String json = """
                {
                    "motoristaId": 10,
                    "veiculoId": 20
                }
                """;

        mockMvc.perform(
                        patch("/api/traslados/1/operacao")
                                .contentType("application/json")
                                .content(json)
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.erro")
                        .value("Conflict"))
                .andExpect(jsonPath("$.mensagem")
                        .value(
                                "Não é possível associar um veículo inativo!"
                        ))
                .andExpect(jsonPath("$.status")
                        .value(409));
    }
}