package com.bths.platform.veiculo;

import com.bths.platform.security.JwtService;
import com.bths.platform.security.UsuarioDetailsService;
import com.bths.platform.veiculo.dto.VeiculoResponse;
import com.bths.platform.veiculo.exception.VeiculoJaCadastradoException;
import com.bths.platform.veiculo.exception.VeiculoNaoEncontradoException;
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

@WebMvcTest(VeiculoController.class)
@AutoConfigureMockMvc(addFilters = false)
class VeiculoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VeiculoService veiculoService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UsuarioDetailsService usuarioDetailsService;

    @Test
    void deveCadastrarVeiculoERetornar201() throws Exception {

        VeiculoResponse response = new VeiculoResponse();
        response.setId(1L);
        response.setModelo("Renault Duster");
        response.setPlaca("ABC1D23");
        response.setCapacidadePassageiros(4);
        response.setObservacao("Veículo executivo");
        response.setAtivo(true);

        when(veiculoService.cadastrarVeiculo(any()))
                .thenReturn(response);

        String json = """
                {
                    "modelo": "Renault Duster",
                    "placa": "ABC1D23",
                    "capacidadePassageiros": 4,
                    "observacao": "Veículo executivo"
                }
                """;

        mockMvc.perform(
                        post("/api/veiculos")
                                .contentType("application/json")
                                .content(json)
                )
                .andExpect(status().isCreated())
                .andExpect(header().string(
                        "Location",
                        "/api/veiculos/1"
                ))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.modelo")
                        .value("Renault Duster"))
                .andExpect(jsonPath("$.placa")
                        .value("ABC1D23"))
                .andExpect(jsonPath("$.capacidadePassageiros")
                        .value(4))
                .andExpect(jsonPath("$.observacao")
                        .value("Veículo executivo"))
                .andExpect(jsonPath("$.ativo")
                        .value(true));

        verify(veiculoService)
                .cadastrarVeiculo(any());
    }

    @Test
    void deveBuscarVeiculoPorIdERetornar200() throws Exception {

        VeiculoResponse response = new VeiculoResponse();
        response.setId(1L);
        response.setModelo("Renault Duster");
        response.setPlaca("ABC1D23");
        response.setCapacidadePassageiros(4);
        response.setAtivo(true);

        when(veiculoService.buscarVeiculoPorId(1L))
                .thenReturn(response);

        mockMvc.perform(
                        get("/api/veiculos/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.modelo")
                        .value("Renault Duster"))
                .andExpect(jsonPath("$.placa")
                        .value("ABC1D23"))
                .andExpect(jsonPath("$.capacidadePassageiros")
                        .value(4))
                .andExpect(jsonPath("$.ativo")
                        .value(true));

        verify(veiculoService)
                .buscarVeiculoPorId(1L);
    }

    @Test
    void deveListarVeiculosERetornar200() throws Exception {

        VeiculoResponse veiculo1 = new VeiculoResponse();
        veiculo1.setId(1L);
        veiculo1.setModelo("Renault Duster");
        veiculo1.setPlaca("ABC1D23");
        veiculo1.setCapacidadePassageiros(4);
        veiculo1.setAtivo(true);

        VeiculoResponse veiculo2 = new VeiculoResponse();
        veiculo2.setId(2L);
        veiculo2.setModelo("Fiat Ducato");
        veiculo2.setPlaca("DEF4G56");
        veiculo2.setCapacidadePassageiros(15);
        veiculo2.setAtivo(true);

        when(veiculoService.listarVeiculos())
                .thenReturn(List.of(
                        veiculo1,
                        veiculo2
                ));

        mockMvc.perform(
                        get("/api/veiculos")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()")
                        .value(2))
                .andExpect(jsonPath("$[0].id")
                        .value(1))
                .andExpect(jsonPath("$[0].modelo")
                        .value("Renault Duster"))
                .andExpect(jsonPath("$[0].placa")
                        .value("ABC1D23"))
                .andExpect(jsonPath("$[1].id")
                        .value(2))
                .andExpect(jsonPath("$[1].modelo")
                        .value("Fiat Ducato"))
                .andExpect(jsonPath("$[1].placa")
                        .value("DEF4G56"));

        verify(veiculoService)
                .listarVeiculos();
    }

    @Test
    void deveAtualizarVeiculoERetornar200() throws Exception {

        VeiculoResponse response = new VeiculoResponse();
        response.setId(1L);
        response.setModelo("Renault Duster Iconic");
        response.setPlaca("ABC1D23");
        response.setCapacidadePassageiros(4);
        response.setObservacao("Veículo atualizado");
        response.setAtivo(true);

        when(veiculoService.atualizarVeiculo(
                eq(1L),
                any()
        )).thenReturn(response);

        String json = """
                {
                    "modelo": "Renault Duster Iconic",
                    "placa": "ABC1D23",
                    "capacidadePassageiros": 4,
                    "observacao": "Veículo atualizado"
                }
                """;

        mockMvc.perform(
                        put("/api/veiculos/1")
                                .contentType("application/json")
                                .content(json)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(1))
                .andExpect(jsonPath("$.modelo")
                        .value("Renault Duster Iconic"))
                .andExpect(jsonPath("$.placa")
                        .value("ABC1D23"))
                .andExpect(jsonPath("$.capacidadePassageiros")
                        .value(4))
                .andExpect(jsonPath("$.observacao")
                        .value("Veículo atualizado"))
                .andExpect(jsonPath("$.ativo")
                        .value(true));

        verify(veiculoService)
                .atualizarVeiculo(
                        eq(1L),
                        any()
                );
    }

    @Test
    void deveInativarVeiculoERetornar200() throws Exception {

        VeiculoResponse response = new VeiculoResponse();
        response.setId(1L);
        response.setModelo("Renault Duster");
        response.setPlaca("ABC1D23");
        response.setCapacidadePassageiros(4);
        response.setAtivo(false);

        when(veiculoService.inativarVeiculo(1L))
                .thenReturn(response);

        mockMvc.perform(
                        patch("/api/veiculos/1/inativar")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(1))
                .andExpect(jsonPath("$.ativo")
                        .value(false));

        verify(veiculoService)
                .inativarVeiculo(1L);
    }

    @Test
    void deveAtivarVeiculoERetornar200() throws Exception {

        VeiculoResponse response = new VeiculoResponse();
        response.setId(1L);
        response.setModelo("Renault Duster");
        response.setPlaca("ABC1D23");
        response.setCapacidadePassageiros(4);
        response.setAtivo(true);

        when(veiculoService.ativarVeiculo(1L))
                .thenReturn(response);

        mockMvc.perform(
                        patch("/api/veiculos/1/ativar")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(1))
                .andExpect(jsonPath("$.ativo")
                        .value(true));

        verify(veiculoService)
                .ativarVeiculo(1L);
    }

    @Test
    void deveRetornar404QuandoVeiculoNaoForEncontrado() throws Exception {

        when(veiculoService.buscarVeiculoPorId(999L))
                .thenThrow(
                        new VeiculoNaoEncontradoException(
                                "Veículo não encontrado!"
                        )
                );

        mockMvc.perform(
                        get("/api/veiculos/999")
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro")
                        .value("Not Found"))
                .andExpect(jsonPath("$.mensagem")
                        .value("Veículo não encontrado!"))
                .andExpect(jsonPath("$.status")
                        .value(404));
    }

    @Test
    void deveRetornar409QuandoPlacaJaEstiverCadastrada() throws Exception {

        when(veiculoService.cadastrarVeiculo(any()))
                .thenThrow(
                        new VeiculoJaCadastradoException(
                                "Já existe um veículo cadastrado com essa placa!"
                        )
                );

        String json = """
                {
                    "modelo": "Renault Duster",
                    "placa": "ABC1D23",
                    "capacidadePassageiros": 4,
                    "observacao": "Veículo executivo"
                }
                """;

        mockMvc.perform(
                        post("/api/veiculos")
                                .contentType("application/json")
                                .content(json)
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.erro")
                        .value("Conflict"))
                .andExpect(jsonPath("$.mensagem")
                        .value("Já existe um veículo cadastrado com essa placa!"))
                .andExpect(jsonPath("$.status")
                        .value(409));
    }

    @Test
    void deveRetornar400QuandoModeloNaoForInformado() throws Exception {

        String json = """
                {
                    "placa": "ABC1D23",
                    "capacidadePassageiros": 4,
                    "observacao": "Veículo executivo"
                }
                """;

        mockMvc.perform(
                        post("/api/veiculos")
                                .contentType("application/json")
                                .content(json)
                )
                .andExpect(status().isBadRequest());

        verify(veiculoService, never())
                .cadastrarVeiculo(any());
    }

    @Test
    void deveRetornar400QuandoPlacaForInvalida() throws Exception {

        String json = """
                {
                    "modelo": "Renault Duster",
                    "placa": "INVALIDA",
                    "capacidadePassageiros": 4
                }
                """;

        mockMvc.perform(
                        post("/api/veiculos")
                                .contentType("application/json")
                                .content(json)
                )
                .andExpect(status().isBadRequest());

        verify(veiculoService, never())
                .cadastrarVeiculo(any());
    }

    @Test
    void deveAceitarPlacaNoFormatoAntigo() throws Exception {

        VeiculoResponse response = new VeiculoResponse();
        response.setId(1L);
        response.setModelo("Renault Duster");
        response.setPlaca("ABC1234");
        response.setCapacidadePassageiros(4);
        response.setAtivo(true);

        when(veiculoService.cadastrarVeiculo(any()))
                .thenReturn(response);

        String json = """
                {
                    "modelo": "Renault Duster",
                    "placa": "ABC1234",
                    "capacidadePassageiros": 4
                }
                """;

        mockMvc.perform(
                        post("/api/veiculos")
                                .contentType("application/json")
                                .content(json)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.placa")
                        .value("ABC1234"));

        verify(veiculoService)
                .cadastrarVeiculo(any());
    }

    @Test
    void deveRetornar400QuandoCapacidadeNaoForInformada() throws Exception {

        String json = """
                {
                    "modelo": "Renault Duster",
                    "placa": "ABC1D23"
                }
                """;

        mockMvc.perform(
                        post("/api/veiculos")
                                .contentType("application/json")
                                .content(json)
                )
                .andExpect(status().isBadRequest());

        verify(veiculoService, never())
                .cadastrarVeiculo(any());
    }

    @Test
    void deveRetornar400QuandoCapacidadeForMenorQueUm() throws Exception {

        String json = """
                {
                    "modelo": "Renault Duster",
                    "placa": "ABC1D23",
                    "capacidadePassageiros": 0
                }
                """;

        mockMvc.perform(
                        post("/api/veiculos")
                                .contentType("application/json")
                                .content(json)
                )
                .andExpect(status().isBadRequest());

        verify(veiculoService, never())
                .cadastrarVeiculo(any());
    }
}