package com.bths.platform.motorista;

import com.bths.platform.motorista.dto.MotoristaResponse;
import com.bths.platform.motorista.exception.MotoristaNaoEncontradoException;
import com.bths.platform.security.JwtService;
import com.bths.platform.security.UsuarioDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MotoristaController.class)
@AutoConfigureMockMvc(addFilters = false)
class MotoristaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MotoristaService motoristaService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UsuarioDetailsService usuarioDetailsService;

    @Test
    void deveCadastrarMotoristaERetornar201()
            throws Exception {

        MotoristaResponse response =
                new MotoristaResponse();

        response.setId(1L);
        response.setNomeCompleto("Carlos da Silva");
        response.setTelefone("21999999999");
        response.setObservacao("Motorista terceirizado");
        response.setAtivo(true);

        when(
                motoristaService.cadastrarMotorista(
                        any()
                )
        ).thenReturn(response);

        String json = """
                {
                  "nomeCompleto": "Carlos da Silva",
                  "telefone": "21999999999",
                  "observacao": "Motorista terceirizado"
                }
                """;

        mockMvc.perform(
                        post("/api/motoristas")
                                .contentType("application/json")
                                .content(json)
                )
                .andExpect(status().isCreated())
                .andExpect(
                        header().string(
                                "Location",
                                "/api/motoristas/1"
                        )
                )
                .andExpect(
                        jsonPath("$.id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.nomeCompleto")
                                .value("Carlos da Silva")
                )
                .andExpect(
                        jsonPath("$.telefone")
                                .value("21999999999")
                )
                .andExpect(
                        jsonPath("$.ativo")
                                .value(true)
                );
    }

    @Test
    void deveBuscarMotoristaPorIdERetornar200()
            throws Exception {

        MotoristaResponse response =
                new MotoristaResponse();

        response.setId(1L);
        response.setNomeCompleto("Carlos da Silva");
        response.setAtivo(true);

        when(
                motoristaService.buscarMotoristaPorId(
                        1L
                )
        ).thenReturn(response);

        mockMvc.perform(
                        get("/api/motoristas/1")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.nomeCompleto")
                                .value("Carlos da Silva")
                )
                .andExpect(
                        jsonPath("$.ativo")
                                .value(true)
                );
    }

    @Test
    void deveListarMotoristasERetornar200()
            throws Exception {

        MotoristaResponse motorista1 =
                new MotoristaResponse();

        motorista1.setId(1L);
        motorista1.setNomeCompleto("Carlos");
        motorista1.setAtivo(true);

        MotoristaResponse motorista2 =
                new MotoristaResponse();

        motorista2.setId(2L);
        motorista2.setNomeCompleto("João");
        motorista2.setAtivo(false);

        when(
                motoristaService.listarMotoristas()
        ).thenReturn(
                List.of(
                        motorista1,
                        motorista2
                )
        );

        mockMvc.perform(
                        get("/api/motoristas")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.length()")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$[0].nomeCompleto")
                                .value("Carlos")
                )
                .andExpect(
                        jsonPath("$[1].nomeCompleto")
                                .value("João")
                );
    }

    @Test
    void deveAtualizarMotoristaERetornar200()
            throws Exception {

        MotoristaResponse response =
                new MotoristaResponse();

        response.setId(1L);
        response.setNomeCompleto(
                "Carlos Atualizado"
        );

        response.setTelefone(
                "21988888888"
        );

        response.setAtivo(true);

        when(
                motoristaService.atualizarMotorista(
                        eq(1L),
                        any()
                )
        ).thenReturn(response);

        String json = """
                {
                  "nomeCompleto": "Carlos Atualizado",
                  "telefone": "21988888888",
                  "observacao": "Disponível no período noturno"
                }
                """;

        mockMvc.perform(
                        put("/api/motoristas/1")
                                .contentType("application/json")
                                .content(json)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.nomeCompleto")
                                .value("Carlos Atualizado")
                )
                .andExpect(
                        jsonPath("$.telefone")
                                .value("21988888888")
                );
    }

    @Test
    void deveInativarMotoristaERetornar200()
            throws Exception {

        MotoristaResponse response =
                new MotoristaResponse();

        response.setId(1L);
        response.setNomeCompleto("Carlos");
        response.setAtivo(false);

        when(
                motoristaService.inativarMotorista(
                        1L
                )
        ).thenReturn(response);

        mockMvc.perform(
                        patch(
                                "/api/motoristas/1/inativar"
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.ativo")
                                .value(false)
                );
    }

    @Test
    void deveAtivarMotoristaERetornar200()
            throws Exception {

        MotoristaResponse response =
                new MotoristaResponse();

        response.setId(1L);
        response.setNomeCompleto("Carlos");
        response.setAtivo(true);

        when(
                motoristaService.ativarMotorista(
                        1L
                )
        ).thenReturn(response);

        mockMvc.perform(
                        patch(
                                "/api/motoristas/1/ativar"
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.ativo")
                                .value(true)
                );
    }

    @Test
    void deveRetornar404QuandoMotoristaNaoExistir()
            throws Exception {

        when(
                motoristaService.buscarMotoristaPorId(
                        999L
                )
        ).thenThrow(
                new MotoristaNaoEncontradoException(
                        "Motorista não encontrado!"
                )
        );

        mockMvc.perform(
                        get("/api/motoristas/999")
                )
                .andExpect(
                        status().isNotFound()
                )
                .andExpect(
                        jsonPath("$.erro")
                                .value("Not Found")
                )
                .andExpect(
                        jsonPath("$.mensagem")
                                .value(
                                        "Motorista não encontrado!"
                                )
                )
                .andExpect(
                        jsonPath("$.status")
                                .value(404)
                );
    }

    @Test
    void deveRetornar400QuandoNomeNaoForInformado()
            throws Exception {

        String json = """
                {
                  "telefone": "21999999999",
                  "observacao": "Motorista terceirizado"
                }
                """;

        mockMvc.perform(
                        post("/api/motoristas")
                                .contentType(
                                        "application/json"
                                )
                                .content(json)
                )
                .andExpect(
                        status().isBadRequest()
                );

        verify(
                motoristaService,
                never()
        ).cadastrarMotorista(any());
    }
}