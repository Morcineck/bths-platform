package com.bths.platform.operacaoTraslado;

import com.bths.platform.operacaoTraslado.dto.*;
import com.bths.platform.operacaoTraslado.execepion.CapacidadeVeiculoExcedidaException;
import com.bths.platform.operacaoTraslado.execepion.OperacaoTrasladoComPassageirosException;
import com.bths.platform.operacaoTraslado.execepion.OperacaoTrasladoNaoEncontradaException;
import com.bths.platform.security.JwtService;
import com.bths.platform.security.UsuarioDetailsService;
import com.bths.platform.traslado.enums.Aeroporto;
import com.bths.platform.traslado.enums.StatusTraslado;
import com.bths.platform.traslado.enums.TipoTraslado;
import com.bths.platform.traslado.exception.MotoristaInativoException;
import com.bths.platform.traslado.exception.TransicaoStatusTrasladoInvalidaException;
import com.bths.platform.traslado.exception.VeiculoInativoException;
import com.bths.platform.veiculo.exception.VeiculoNaoEncontradoException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OperacaoTrasladoController.class)
@AutoConfigureMockMvc(addFilters = false)
class OperacaoTrasladoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OperacaoTrasladoService operacaoTrasladoService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UsuarioDetailsService usuarioDetailsService;

    @Test
    void deveCriarOperacaoERetornar201()
            throws Exception {

        OperacaoTrasladoResponse response =
                new OperacaoTrasladoResponse();

        response.setId(100L);

        response.setViagemId(1L);
        response.setViagemNome(
                "Tomorrowland Brasil 2027"
        );

        response.setTipo(
                TipoTraslado.AEROPORTO_PARA_HOSPEDAGEM
        );

        response.setAeroporto(
                Aeroporto.GRU
        );

        response.setStatus(
                StatusTraslado.AGUARDANDO
        );

        response.setDataHoraPrevista(
                LocalDateTime.of(
                        2027,
                        4,
                        29,
                        13,
                        0
                )
        );

        response.setLocalOrigem(
                "Aeroporto de Guarulhos - Terminal 2"
        );

        response.setLocalDestino(
                "Hospedagem Beat Trips"
        );

        response.setMotoristaId(10L);
        response.setMotoristaNome(
                "Matheus Henrique"
        );

        response.setVeiculoId(20L);
        response.setVeiculoModelo(
                "Chevrolet Spin"
        );

        response.setVeiculoPlaca(
                "ABC1D23"
        );

        response.setCapacidadePassageiros(7);
        response.setQuantidadePassageiros(0L);
        response.setVagasDisponiveis(7L);

        response.setObservacao(
                "Operação de chegada dos hóspedes."
        );

        when(
                operacaoTrasladoService.criarOperacao(
                        any(OperacaoTrasladoRequest.class)
                )
        ).thenReturn(response);

        String json = """
                {
                    "viagemId": 1,
                    "tipo": "AEROPORTO_PARA_HOSPEDAGEM",
                    "aeroporto": "GRU",
                    "dataHoraPrevista": "2027-04-29T13:00:00",
                    "localOrigem": "Aeroporto de Guarulhos - Terminal 2",
                    "localDestino": "Hospedagem Beat Trips",
                    "motoristaId": 10,
                    "veiculoId": 20,
                    "observacao": "Operação de chegada dos hóspedes."
                }
                """;

        mockMvc.perform(
                        post("/api/traslados/operacoes")
                                .contentType("application/json")
                                .content(json)
                )
                .andExpect(status().isCreated())
                .andExpect(
                        header().string(
                                "Location",
                                "/api/traslados/operacoes/100"
                        )
                )
                .andExpect(
                        jsonPath("$.id")
                                .value(100)
                )
                .andExpect(
                        jsonPath("$.viagemId")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.viagemNome")
                                .value(
                                        "Tomorrowland Brasil 2027"
                                )
                )
                .andExpect(
                        jsonPath("$.tipo")
                                .value(
                                        "AEROPORTO_PARA_HOSPEDAGEM"
                                )
                )
                .andExpect(
                        jsonPath("$.aeroporto")
                                .value("GRU")
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("AGUARDANDO")
                )
                .andExpect(
                        jsonPath("$.dataHoraPrevista")
                                .value(
                                        "2027-04-29T13:00:00"
                                )
                )
                .andExpect(
                        jsonPath("$.localOrigem")
                                .value(
                                        "Aeroporto de Guarulhos - Terminal 2"
                                )
                )
                .andExpect(
                        jsonPath("$.localDestino")
                                .value(
                                        "Hospedagem Beat Trips"
                                )
                )
                .andExpect(
                        jsonPath("$.motoristaId")
                                .value(10)
                )
                .andExpect(
                        jsonPath("$.motoristaNome")
                                .value("Matheus Henrique")
                )
                .andExpect(
                        jsonPath("$.veiculoId")
                                .value(20)
                )
                .andExpect(
                        jsonPath("$.veiculoModelo")
                                .value("Chevrolet Spin")
                )
                .andExpect(
                        jsonPath("$.veiculoPlaca")
                                .value("ABC1D23")
                )
                .andExpect(
                        jsonPath("$.capacidadePassageiros")
                                .value(7)
                )
                .andExpect(
                        jsonPath("$.quantidadePassageiros")
                                .value(0)
                )
                .andExpect(
                        jsonPath("$.vagasDisponiveis")
                                .value(7)
                )
                .andExpect(
                        jsonPath("$.observacao")
                                .value(
                                        "Operação de chegada dos hóspedes."
                                )
                );

        verify(operacaoTrasladoService)
                .criarOperacao(
                        any(OperacaoTrasladoRequest.class)
                );
    }

    @Test
    void deveVincularTrasladoERetornar200()
            throws Exception {

        OperacaoTrasladoResponse response =
                new OperacaoTrasladoResponse();

        response.setId(100L);
        response.setQuantidadePassageiros(1L);
        response.setVagasDisponiveis(6L);

        when(
                operacaoTrasladoService.vincularTraslado(
                        100L,
                        200L
                )
        ).thenReturn(response);

        mockMvc.perform(
                        patch(
                                "/api/traslados/operacoes/100/traslados/200"
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id")
                                .value(100)
                )
                .andExpect(
                        jsonPath("$.quantidadePassageiros")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.vagasDisponiveis")
                                .value(6)
                );

        verify(operacaoTrasladoService)
                .vincularTraslado(
                        100L,
                        200L
                );
    }

    @Test
    void deveRetornar400QuandoViagemNaoForInformada()
            throws Exception {

        String json = """
                {
                    "tipo": "AEROPORTO_PARA_HOSPEDAGEM",
                    "aeroporto": "GRU",
                    "dataHoraPrevista": "2027-04-29T13:00:00",
                    "localOrigem": "Aeroporto de Guarulhos - Terminal 2",
                    "localDestino": "Hospedagem Beat Trips",
                    "motoristaId": 10,
                    "veiculoId": 20
                }
                """;

        mockMvc.perform(
                        post("/api/traslados/operacoes")
                                .contentType("application/json")
                                .content(json)
                )
                .andExpect(status().isBadRequest());

        verify(
                operacaoTrasladoService,
                never()
        ).criarOperacao(
                any(OperacaoTrasladoRequest.class)
        );
    }

    @Test
    void deveRetornar400QuandoMotoristaNaoForInformado()
            throws Exception {

        String json = """
                {
                    "viagemId": 1,
                    "tipo": "AEROPORTO_PARA_HOSPEDAGEM",
                    "aeroporto": "GRU",
                    "dataHoraPrevista": "2027-04-29T13:00:00",
                    "localOrigem": "Aeroporto de Guarulhos - Terminal 2",
                    "localDestino": "Hospedagem Beat Trips",
                    "veiculoId": 20
                }
                """;

        mockMvc.perform(
                        post("/api/traslados/operacoes")
                                .contentType("application/json")
                                .content(json)
                )
                .andExpect(status().isBadRequest());

        verify(
                operacaoTrasladoService,
                never()
        ).criarOperacao(
                any(OperacaoTrasladoRequest.class)
        );
    }

    @Test
    void deveRetornar400QuandoVeiculoNaoForInformado()
            throws Exception {

        String json = """
                {
                    "viagemId": 1,
                    "tipo": "AEROPORTO_PARA_HOSPEDAGEM",
                    "aeroporto": "GRU",
                    "dataHoraPrevista": "2027-04-29T13:00:00",
                    "localOrigem": "Aeroporto de Guarulhos - Terminal 2",
                    "localDestino": "Hospedagem Beat Trips",
                    "motoristaId": 10
                }
                """;

        mockMvc.perform(
                        post("/api/traslados/operacoes")
                                .contentType("application/json")
                                .content(json)
                )
                .andExpect(status().isBadRequest());

        verify(
                operacaoTrasladoService,
                never()
        ).criarOperacao(
                any(OperacaoTrasladoRequest.class)
        );
    }

    @Test
    void deveRetornar409QuandoCapacidadeForExcedida()
            throws Exception {

        when(
                operacaoTrasladoService.vincularTraslado(
                        100L,
                        200L
                )
        ).thenThrow(
                new CapacidadeVeiculoExcedidaException(
                        "Não há vagas disponíveis no veículo desta operação!"
                )
        );

        mockMvc.perform(
                        patch(
                                "/api/traslados/operacoes/100/traslados/200"
                        )
                )
                .andExpect(status().isConflict())
                .andExpect(
                        jsonPath("$.erro")
                                .value("Conflict")
                )
                .andExpect(
                        jsonPath("$.mensagem")
                                .value(
                                        "Não há vagas disponíveis no veículo desta operação!"
                                )
                )
                .andExpect(
                        jsonPath("$.status")
                                .value(409)
                );
    }

    @Test
    void deveRetornar404QuandoOperacaoNaoExistir()
            throws Exception {

        when(
                operacaoTrasladoService.vincularTraslado(
                        999L,
                        200L
                )
        ).thenThrow(
                new OperacaoTrasladoNaoEncontradaException(
                        "Operação de traslado não encontrada!"
                )
        );

        mockMvc.perform(
                        patch(
                                "/api/traslados/operacoes/999/traslados/200"
                        )
                )
                .andExpect(status().isNotFound())
                .andExpect(
                        jsonPath("$.erro")
                                .value("Not Found")
                )
                .andExpect(
                        jsonPath("$.mensagem")
                                .value(
                                        "Operação de traslado não encontrada!"
                                )
                )
                .andExpect(
                        jsonPath("$.status")
                                .value(404)
                );
    }

    @Test
    void deveAlterarVeiculoDaOperacaoERetornar200()
            throws Exception {

        OperacaoTrasladoResponse response =
                new OperacaoTrasladoResponse();

        response.setId(100L);
        response.setVeiculoId(30L);
        response.setVeiculoModelo(
                "Fiat Ducato"
        );
        response.setVeiculoPlaca(
                "ABC1D23"
        );
        response.setCapacidadePassageiros(15);
        response.setQuantidadePassageiros(7L);
        response.setVagasDisponiveis(8L);

        when(
                operacaoTrasladoService.alterarVeiculo(
                        100L,
                        30L
                )
        ).thenReturn(response);

        mockMvc.perform(
                        patch(
                                "/api/traslados/operacoes/100/veiculo/30"
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id")
                                .value(100)
                )
                .andExpect(
                        jsonPath("$.veiculoId")
                                .value(30)
                )
                .andExpect(
                        jsonPath("$.veiculoModelo")
                                .value("Fiat Ducato")
                )
                .andExpect(
                        jsonPath("$.veiculoPlaca")
                                .value("ABC1D23")
                )
                .andExpect(
                        jsonPath("$.capacidadePassageiros")
                                .value(15)
                )
                .andExpect(
                        jsonPath("$.quantidadePassageiros")
                                .value(7)
                )
                .andExpect(
                        jsonPath("$.vagasDisponiveis")
                                .value(8)
                );

        verify(operacaoTrasladoService)
                .alterarVeiculo(
                        100L,
                        30L
                );
    }

    @Test
    void deveRetornar409QuandoNovoVeiculoNaoComportarPassageiros()
            throws Exception {

        when(
                operacaoTrasladoService.alterarVeiculo(
                        100L,
                        30L
                )
        ).thenThrow(
                new CapacidadeVeiculoExcedidaException(
                        "A capacidade do novo veículo é menor que a quantidade de passageiros da operação!"
                )
        );

        mockMvc.perform(
                        patch(
                                "/api/traslados/operacoes/100/veiculo/30"
                        )
                )
                .andExpect(status().isConflict())
                .andExpect(
                        jsonPath("$.erro")
                                .value("Conflict")
                )
                .andExpect(
                        jsonPath("$.mensagem")
                                .value(
                                        "A capacidade do novo veículo é menor que a quantidade de passageiros da operação!"
                                )
                )
                .andExpect(
                        jsonPath("$.status")
                                .value(409)
                );
    }

    @Test
    void deveRetornar409QuandoNovoVeiculoEstiverInativo()
            throws Exception {

        when(
                operacaoTrasladoService.alterarVeiculo(
                        100L,
                        30L
                )
        ).thenThrow(
                new VeiculoInativoException(
                        "Não é possível associar um veículo inativo!"
                )
        );

        mockMvc.perform(
                        patch(
                                "/api/traslados/operacoes/100/veiculo/30"
                        )
                )
                .andExpect(status().isConflict())
                .andExpect(
                        jsonPath("$.erro")
                                .value("Conflict")
                )
                .andExpect(
                        jsonPath("$.mensagem")
                                .value(
                                        "Não é possível associar um veículo inativo!"
                                )
                )
                .andExpect(
                        jsonPath("$.status")
                                .value(409)
                );
    }

    @Test
    void deveRetornar404QuandoNovoVeiculoNaoExistir()
            throws Exception {

        when(
                operacaoTrasladoService.alterarVeiculo(
                        100L,
                        999L
                )
        ).thenThrow(
                new VeiculoNaoEncontradoException(
                        "Veículo não encontrado!"
                )
        );

        mockMvc.perform(
                        patch(
                                "/api/traslados/operacoes/100/veiculo/999"
                        )
                )
                .andExpect(status().isNotFound())
                .andExpect(
                        jsonPath("$.erro")
                                .value("Not Found")
                )
                .andExpect(
                        jsonPath("$.mensagem")
                                .value(
                                        "Veículo não encontrado!"
                                )
                )
                .andExpect(
                        jsonPath("$.status")
                                .value(404)
                );
    }

    @Test
    void deveListarOperacoesPorViagemERetornar200()
            throws Exception {

        OperacaoTrasladoResponse response1 =
                new OperacaoTrasladoResponse();

        response1.setId(100L);
        response1.setViagemId(1L);
        response1.setViagemNome(
                "Tomorrowland Brasil 2027"
        );

        response1.setTipo(
                TipoTraslado.AEROPORTO_PARA_HOSPEDAGEM
        );

        response1.setAeroporto(
                Aeroporto.GRU
        );

        response1.setStatus(
                StatusTraslado.AGUARDANDO
        );

        response1.setDataHoraPrevista(
                LocalDateTime.of(
                        2027,
                        4,
                        29,
                        13,
                        0
                )
        );

        response1.setLocalOrigem(
                "Aeroporto de Guarulhos"
        );

        response1.setLocalDestino(
                "Hospedagem Beat Trips"
        );

        response1.setMotoristaId(10L);
        response1.setMotoristaNome(
                "Matheus Henrique"
        );

        response1.setVeiculoId(20L);
        response1.setVeiculoModelo(
                "Chevrolet Spin"
        );

        response1.setVeiculoPlaca(
                "ABC1D23"
        );

        response1.setCapacidadePassageiros(7);
        response1.setQuantidadePassageiros(5L);
        response1.setVagasDisponiveis(2L);

        OperacaoTrasladoResponse response2 =
                new OperacaoTrasladoResponse();

        response2.setId(101L);
        response2.setViagemId(1L);
        response2.setViagemNome(
                "Tomorrowland Brasil 2027"
        );

        response2.setTipo(
                TipoTraslado.HOSPEDAGEM_PARA_AEROPORTO
        );

        response2.setAeroporto(
                Aeroporto.GRU
        );

        response2.setStatus(
                StatusTraslado.AGUARDANDO
        );

        response2.setDataHoraPrevista(
                LocalDateTime.of(
                        2027,
                        5,
                        3,
                        9,
                        0
                )
        );

        response2.setLocalOrigem(
                "Hospedagem Beat Trips"
        );

        response2.setLocalDestino(
                "Aeroporto de Guarulhos"
        );

        response2.setMotoristaId(11L);
        response2.setMotoristaNome(
                "Lucas Cesar"
        );

        response2.setVeiculoId(21L);
        response2.setVeiculoModelo(
                "Toyota Corolla"
        );

        response2.setVeiculoPlaca(
                "DEF4G56"
        );

        response2.setCapacidadePassageiros(4);
        response2.setQuantidadePassageiros(3L);
        response2.setVagasDisponiveis(1L);

        when(
                operacaoTrasladoService
                        .listarPorViagem(1L)
        ).thenReturn(
                List.of(
                        response1,
                        response2
                )
        );

        mockMvc.perform(
                        get(
                                "/api/traslados/operacoes/viagem/1"
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[0].id")
                                .value(100)
                )
                .andExpect(
                        jsonPath("$[0].tipo")
                                .value(
                                        "AEROPORTO_PARA_HOSPEDAGEM"
                                )
                )
                .andExpect(
                        jsonPath("$[0].aeroporto")
                                .value("GRU")
                )
                .andExpect(
                        jsonPath("$[0].localOrigem")
                                .value(
                                        "Aeroporto de Guarulhos"
                                )
                )
                .andExpect(
                        jsonPath("$[0].localDestino")
                                .value(
                                        "Hospedagem Beat Trips"
                                )
                )
                .andExpect(
                        jsonPath("$[0].motoristaNome")
                                .value("Matheus Henrique")
                )
                .andExpect(
                        jsonPath("$[0].veiculoModelo")
                                .value("Chevrolet Spin")
                )
                .andExpect(
                        jsonPath("$[0].quantidadePassageiros")
                                .value(5)
                )
                .andExpect(
                        jsonPath("$[0].vagasDisponiveis")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$[1].id")
                                .value(101)
                )
                .andExpect(
                        jsonPath("$[1].tipo")
                                .value(
                                        "HOSPEDAGEM_PARA_AEROPORTO"
                                )
                )
                .andExpect(
                        jsonPath("$[1].veiculoModelo")
                                .value("Toyota Corolla")
                )
                .andExpect(
                        jsonPath("$[1].quantidadePassageiros")
                                .value(3)
                )
                .andExpect(
                        jsonPath("$[1].vagasDisponiveis")
                                .value(1)
                );

        verify(
                operacaoTrasladoService
        ).listarPorViagem(1L);
    }

    @Test
    void deveAtualizarOperacaoERetornar200()
            throws Exception {

        OperacaoTrasladoResponse response =
                new OperacaoTrasladoResponse();

        response.setId(100L);
        response.setTipo(
                TipoTraslado.AEROPORTO_PARA_HOSPEDAGEM
        );
        response.setAeroporto(
                Aeroporto.GRU
        );
        response.setStatus(
                StatusTraslado.AGUARDANDO
        );
        response.setDataHoraPrevista(
                LocalDateTime.of(
                        2027,
                        4,
                        29,
                        14,
                        30
                )
        );
        response.setLocalOrigem(
                "Aeroporto de Guarulhos"
        );
        response.setLocalDestino(
                "Hospedagem Beat Trips"
        );
        response.setMotoristaId(10L);
        response.setMotoristaNome(
                "Lucas Cesar"
        );
        response.setVeiculoId(20L);
        response.setVeiculoModelo(
                "Chevrolet Spin"
        );
        response.setVeiculoPlaca(
                "BRA2E19"
        );
        response.setCapacidadePassageiros(7);
        response.setQuantidadePassageiros(3L);
        response.setVagasDisponiveis(4L);
        response.setObservacao(
                "Horário atualizado"
        );

        when(
                operacaoTrasladoService
                        .atualizarOperacao(
                                eq(100L),
                                any(OperacaoTrasladoUpdateRequest.class)
                        )
        ).thenReturn(response);

        String json = """
            {
                "tipo": "AEROPORTO_PARA_HOSPEDAGEM",
                "aeroporto": "GRU",
                "dataHoraPrevista": "2027-04-29T14:30:00",
                "localOrigem": "Aeroporto de Guarulhos",
                "localDestino": "Hospedagem Beat Trips",
                "motoristaId": 10,
                "veiculoId": 20,
                "observacao": "Horário atualizado"
            }
            """;

        mockMvc.perform(
                        put(
                                "/api/traslados/operacoes/100"
                        )
                                .contentType(
                                        "application/json"
                                )
                                .content(json)
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.id")
                                .value(100)
                )
                .andExpect(
                        jsonPath("$.tipo")
                                .value(
                                        "AEROPORTO_PARA_HOSPEDAGEM"
                                )
                )
                .andExpect(
                        jsonPath("$.dataHoraPrevista")
                                .value(
                                        "2027-04-29T14:30:00"
                                )
                )
                .andExpect(
                        jsonPath("$.localOrigem")
                                .value(
                                        "Aeroporto de Guarulhos"
                                )
                )
                .andExpect(
                        jsonPath("$.localDestino")
                                .value(
                                        "Hospedagem Beat Trips"
                                )
                )
                .andExpect(
                        jsonPath("$.motoristaId")
                                .value(10)
                )
                .andExpect(
                        jsonPath("$.veiculoId")
                                .value(20)
                )
                .andExpect(
                        jsonPath("$.quantidadePassageiros")
                                .value(3)
                )
                .andExpect(
                        jsonPath("$.vagasDisponiveis")
                                .value(4)
                );

        verify(
                operacaoTrasladoService
        ).atualizarOperacao(
                eq(100L),
                any(OperacaoTrasladoUpdateRequest.class)
        );
    }

    @Test
    void deveRetornar400AoAtualizarOperacaoComBodyInvalido()
            throws Exception {

        String json = """
            {
                "tipo": "AEROPORTO_PARA_HOSPEDAGEM",
                "motoristaId": 10,
                "veiculoId": 20
            }
            """;

        mockMvc.perform(
                        put(
                                "/api/traslados/operacoes/100"
                        )
                                .contentType(
                                        "application/json"
                                )
                                .content(json)
                )
                .andExpect(
                        status().isBadRequest()
                );

        verify(
                operacaoTrasladoService,
                never()
        ).atualizarOperacao(
                anyLong(),
                any(OperacaoTrasladoUpdateRequest.class)
        );
    }

    @Test
    void deveRetornar404AoAtualizarOperacaoInexistente()
            throws Exception {

        when(
                operacaoTrasladoService
                        .atualizarOperacao(
                                eq(999L),
                                any(OperacaoTrasladoUpdateRequest.class)
                        )
        ).thenThrow(
                new OperacaoTrasladoNaoEncontradaException(
                        "Operação de traslado não encontrada!"
                )
        );

        String json = """
            {
                "tipo": "AEROPORTO_PARA_HOSPEDAGEM",
                "aeroporto": "GRU",
                "dataHoraPrevista": "2027-04-29T14:30:00",
                "localOrigem": "Aeroporto de Guarulhos",
                "localDestino": "Hospedagem Beat Trips",
                "motoristaId": 10,
                "veiculoId": 20
            }
            """;

        mockMvc.perform(
                        put(
                                "/api/traslados/operacoes/999"
                        )
                                .contentType(
                                        "application/json"
                                )
                                .content(json)
                )
                .andExpect(
                        status().isNotFound()
                )
                .andExpect(
                        jsonPath("$.status")
                                .value(404)
                );
    }

    @Test
    void deveRetornar409AoAtualizarComMotoristaInativo()
            throws Exception {

        when(
                operacaoTrasladoService
                        .atualizarOperacao(
                                eq(100L),
                                any(OperacaoTrasladoUpdateRequest.class)
                        )
        ).thenThrow(
                new MotoristaInativoException(
                        "Não é possível atualizar a operação com motorista inativo!"
                )
        );

        String json = """
            {
                "tipo": "AEROPORTO_PARA_HOSPEDAGEM",
                "aeroporto": "GRU",
                "dataHoraPrevista": "2027-04-29T14:30:00",
                "localOrigem": "Aeroporto de Guarulhos",
                "localDestino": "Hospedagem Beat Trips",
                "motoristaId": 10,
                "veiculoId": 20
            }
            """;

        mockMvc.perform(
                        put(
                                "/api/traslados/operacoes/100"
                        )
                                .contentType(
                                        "application/json"
                                )
                                .content(json)
                )
                .andExpect(
                        status().isConflict()
                )
                .andExpect(
                        jsonPath("$.status")
                                .value(409)
                );
    }

    @Test
    void deveRetornar409AoAtualizarComVeiculoInativo()
            throws Exception {

        when(
                operacaoTrasladoService
                        .atualizarOperacao(
                                eq(100L),
                                any(OperacaoTrasladoUpdateRequest.class)
                        )
        ).thenThrow(
                new VeiculoInativoException(
                        "Não é possível atualizar a operação com veículo inativo!"
                )
        );

        String json = """
            {
                "tipo": "AEROPORTO_PARA_HOSPEDAGEM",
                "aeroporto": "GRU",
                "dataHoraPrevista": "2027-04-29T14:30:00",
                "localOrigem": "Aeroporto de Guarulhos",
                "localDestino": "Hospedagem Beat Trips",
                "motoristaId": 10,
                "veiculoId": 20
            }
            """;

        mockMvc.perform(
                        put(
                                "/api/traslados/operacoes/100"
                        )
                                .contentType(
                                        "application/json"
                                )
                                .content(json)
                )
                .andExpect(
                        status().isConflict()
                )
                .andExpect(
                        jsonPath("$.status")
                                .value(409)
                );
    }

    @Test
    void deveRetornar409AoAtualizarComVeiculoSemCapacidade()
            throws Exception {

        when(
                operacaoTrasladoService
                        .atualizarOperacao(
                                eq(100L),
                                any(OperacaoTrasladoUpdateRequest.class)
                        )
        ).thenThrow(
                new CapacidadeVeiculoExcedidaException(
                        "A capacidade do veículo é menor que a quantidade de passageiros da operação!"
                )
        );

        String json = """
            {
                "tipo": "AEROPORTO_PARA_HOSPEDAGEM",
                "aeroporto": "GRU",
                "dataHoraPrevista": "2027-04-29T14:30:00",
                "localOrigem": "Aeroporto de Guarulhos",
                "localDestino": "Hospedagem Beat Trips",
                "motoristaId": 10,
                "veiculoId": 20
            }
            """;

        mockMvc.perform(
                        put(
                                "/api/traslados/operacoes/100"
                        )
                                .contentType(
                                        "application/json"
                                )
                                .content(json)
                )
                .andExpect(
                        status().isConflict()
                )
                .andExpect(
                        jsonPath("$.status")
                                .value(409)
                );
    }

    @Test
    void deveExcluirOperacaoVaziaERetornar204()
            throws Exception {

        doNothing()
                .when(
                        operacaoTrasladoService
                )
                .excluirOperacao(100L);

        mockMvc.perform(
                        delete(
                                "/api/traslados/operacoes/100"
                        )
                )
                .andExpect(
                        status().isNoContent()
                );

        verify(
                operacaoTrasladoService
        ).excluirOperacao(100L);
    }

    @Test
    void deveRetornar409AoExcluirOperacaoComPassageiros()
            throws Exception {

        doThrow(
                new OperacaoTrasladoComPassageirosException(
                        "Não é possível excluir uma operação que possui passageiros vinculados. Cancele a operação."
                )
        )
                .when(
                        operacaoTrasladoService
                )
                .excluirOperacao(100L);

        mockMvc.perform(
                        delete(
                                "/api/traslados/operacoes/100"
                        )
                )
                .andExpect(
                        status().isConflict()
                )
                .andExpect(
                        jsonPath("$.erro")
                                .value("Conflict")
                )
                .andExpect(
                        jsonPath("$.mensagem")
                                .value(
                                        "Não é possível excluir uma operação que possui passageiros vinculados. Cancele a operação."
                                )
                )
                .andExpect(
                        jsonPath("$.status")
                                .value(409)
                );

        verify(
                operacaoTrasladoService
        ).excluirOperacao(100L);
    }

    @Test
    void deveRetornar404AoExcluirOperacaoInexistente()
            throws Exception {

        doThrow(
                new OperacaoTrasladoNaoEncontradaException(
                        "Operação de traslado não encontrada!"
                )
        )
                .when(
                        operacaoTrasladoService
                )
                .excluirOperacao(999L);

        mockMvc.perform(
                        delete(
                                "/api/traslados/operacoes/999"
                        )
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
                                        "Operação de traslado não encontrada!"
                                )
                )
                .andExpect(
                        jsonPath("$.status")
                                .value(404)
                );

        verify(
                operacaoTrasladoService
        ).excluirOperacao(999L);
    }


    @Test
    void deveListarPassageirosDaOperacaoERetornar200()
            throws Exception {

        OperacaoTrasladoPassageiroResponse passageiro1 =
                new OperacaoTrasladoPassageiroResponse();

        passageiro1.setTrasladoId(200L);
        passageiro1.setHospedeId(1L);
        passageiro1.setHospedeNome(
                "Robson Gabriel"
        );

        OperacaoTrasladoPassageiroResponse passageiro2 =
                new OperacaoTrasladoPassageiroResponse();

        passageiro2.setTrasladoId(201L);
        passageiro2.setHospedeId(2L);
        passageiro2.setHospedeNome(
                "Lucas Cesar"
        );

        when(
                operacaoTrasladoService
                        .listarPassageiros(100L)
        ).thenReturn(
                List.of(
                        passageiro1,
                        passageiro2
                )
        );

        mockMvc.perform(
                        get(
                                "/api/traslados/operacoes/100/passageiros"
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$[0].trasladoId")
                                .value(200)
                )
                .andExpect(
                        jsonPath("$[0].hospedeId")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$[0].hospedeNome")
                                .value(
                                        "Robson Gabriel"
                                )
                )
                .andExpect(
                        jsonPath("$[1].trasladoId")
                                .value(201)
                )
                .andExpect(
                        jsonPath("$[1].hospedeId")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$[1].hospedeNome")
                                .value(
                                        "Lucas Cesar"
                                )
                );

        verify(
                operacaoTrasladoService
        ).listarPassageiros(100L);
    }

    @Test
    void deveRetornarListaVaziaQuandoOperacaoNaoPossuirPassageiros()
            throws Exception {

        when(
                operacaoTrasladoService
                        .listarPassageiros(100L)
        ).thenReturn(
                List.of()
        );

        mockMvc.perform(
                        get(
                                "/api/traslados/operacoes/100/passageiros"
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$")
                                .isArray()
                )
                .andExpect(
                        jsonPath("$")
                                .isEmpty()
                );

        verify(
                operacaoTrasladoService
        ).listarPassageiros(100L);
    }

    @Test
    void deveRetornar404AoListarPassageirosDeOperacaoInexistente()
            throws Exception {

        when(
                operacaoTrasladoService
                        .listarPassageiros(999L)
        ).thenThrow(
                new OperacaoTrasladoNaoEncontradaException(
                        "Operação de traslado não encontrada!"
                )
        );

        mockMvc.perform(
                        get(
                                "/api/traslados/operacoes/999/passageiros"
                        )
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
                                        "Operação de traslado não encontrada!"
                                )
                )
                .andExpect(
                        jsonPath("$.status")
                                .value(404)
                );

        verify(
                operacaoTrasladoService
        ).listarPassageiros(999L);
    }

    @Test
    void deveAtualizarStatusDaOperacaoERetornar200()
            throws Exception {

        OperacaoTrasladoResponse response =
                new OperacaoTrasladoResponse();

        response.setId(100L);
        response.setStatus(
                StatusTraslado.EM_ANDAMENTO
        );

        when(
                operacaoTrasladoService.atualizarStatus(
                        eq(100L),
                        any(OperacaoTrasladoStatusRequest.class)
                )
        ).thenReturn(response);

        String json = """
        {
            "status": "EM_ANDAMENTO"
        }
        """;

        mockMvc.perform(
                        patch(
                                "/api/traslados/operacoes/100/status"
                        )
                                .contentType(
                                        "application/json"
                                )
                                .content(json)
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.id")
                                .value(100)
                )
                .andExpect(
                        jsonPath("$.status")
                                .value(
                                        "EM_ANDAMENTO"
                                )
                );

        verify(
                operacaoTrasladoService
        ).atualizarStatus(
                eq(100L),
                any(OperacaoTrasladoStatusRequest.class)
        );
    }

    @Test
    void deveRetornar400AoAtualizarStatusSemInformarStatus()
            throws Exception {

        String json = """
        {}
        """;

        mockMvc.perform(
                        patch(
                                "/api/traslados/operacoes/100/status"
                        )
                                .contentType(
                                        "application/json"
                                )
                                .content(json)
                )
                .andExpect(
                        status().isBadRequest()
                );

        verify(
                operacaoTrasladoService,
                never()
        ).atualizarStatus(
                anyLong(),
                any(OperacaoTrasladoStatusRequest.class)
        );
    }

    @Test
    void deveRetornar404AoAtualizarStatusDeOperacaoInexistente()
            throws Exception {

        when(
                operacaoTrasladoService.atualizarStatus(
                        eq(999L),
                        any(OperacaoTrasladoStatusRequest.class)
                )
        ).thenThrow(
                new OperacaoTrasladoNaoEncontradaException(
                        "Operação de traslado não encontrada!"
                )
        );

        String json = """
        {
            "status": "EM_ANDAMENTO"
        }
        """;

        mockMvc.perform(
                        patch(
                                "/api/traslados/operacoes/999/status"
                        )
                                .contentType(
                                        "application/json"
                                )
                                .content(json)
                )
                .andExpect(
                        status().isNotFound()
                )
                .andExpect(
                        jsonPath("$.status")
                                .value(404)
                )
                .andExpect(
                        jsonPath("$.mensagem")
                                .value(
                                        "Operação de traslado não encontrada!"
                                )
                );
    }

    @Test
    void deveRetornar409AoAtualizarStatusComTransicaoInvalida()
            throws Exception {

        when(
                operacaoTrasladoService.atualizarStatus(
                        eq(100L),
                        any(OperacaoTrasladoStatusRequest.class)
                )
        ).thenThrow(
                new TransicaoStatusTrasladoInvalidaException(
                        "Não é possível alterar o status de AGUARDANDO para CONCLUIDO!"
                )
        );

        String json = """
        {
            "status": "CONCLUIDO"
        }
        """;

        mockMvc.perform(
                        patch(
                                "/api/traslados/operacoes/100/status"
                        )
                                .contentType(
                                        "application/json"
                                )
                                .content(json)
                )
                .andExpect(
                        status().isConflict()
                )
                .andExpect(
                        jsonPath("$.status")
                                .value(409)
                )
                .andExpect(
                        jsonPath("$.mensagem")
                                .value(
                                        "Não é possível alterar o status de AGUARDANDO para CONCLUIDO!"
                                )
                );
    }



}