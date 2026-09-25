package com.bths.platform.operacaoTraslado;

import com.bths.platform.alocacao.exception.ViagemIncompativelException;
import com.bths.platform.hospede.Hospede;
import com.bths.platform.motorista.Motorista;
import com.bths.platform.motorista.MotoristaRepository;
import com.bths.platform.motorista.exception.MotoristaNaoEncontradoException;
import com.bths.platform.operacaoTraslado.dto.*;
import com.bths.platform.operacaoTraslado.execepion.CapacidadeVeiculoExcedidaException;
import com.bths.platform.operacaoTraslado.execepion.OperacaoTrasladoComPassageirosException;
import com.bths.platform.operacaoTraslado.execepion.OperacaoTrasladoNaoEncontradaException;
import com.bths.platform.operacaoTraslado.mapper.HistoricoStatusOperacaoTrasladoMapper;
import com.bths.platform.operacaoTraslado.mapper.OperacaoTrasladoMapper;
import com.bths.platform.traslado.Traslado;
import com.bths.platform.traslado.TrasladoRepository;
import com.bths.platform.traslado.enums.Aeroporto;
import com.bths.platform.traslado.enums.StatusTraslado;
import com.bths.platform.traslado.enums.TipoTraslado;
import com.bths.platform.traslado.exception.MotoristaInativoException;
import com.bths.platform.traslado.exception.TransicaoStatusTrasladoInvalidaException;
import com.bths.platform.traslado.exception.TrasladoNaoEncontradoException;
import com.bths.platform.traslado.exception.VeiculoInativoException;
import com.bths.platform.veiculo.Veiculo;
import com.bths.platform.veiculo.VeiculoRepository;
import com.bths.platform.veiculo.exception.VeiculoNaoEncontradoException;
import com.bths.platform.viagem.Viagem;
import com.bths.platform.viagem.ViagemRepository;
import com.bths.platform.viagem.exception.ViagemNaoEncontradaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OperacaoTrasladoServiceTest {

    @Mock
    private OperacaoTrasladoRepository operacaoTrasladoRepository;

    @Mock
    private TrasladoRepository trasladoRepository;

    @Mock
    private ViagemRepository viagemRepository;

    @Mock
    private MotoristaRepository motoristaRepository;

    @Mock
    private VeiculoRepository veiculoRepository;

    @Mock
    private OperacaoTrasladoMapper operacaoTrasladoMapper;

    private OperacaoTrasladoService operacaoTrasladoService;

    @Mock
    private HistoricoStatusOperacaoTrasladoRepository
            historicoStatusOperacaoTrasladoRepository;

    @Mock
    private HistoricoStatusOperacaoTrasladoMapper
            historicoStatusOperacaoTrasladoMapper;

    @BeforeEach
    void setUp() {
        operacaoTrasladoService =
                new OperacaoTrasladoService(
                        operacaoTrasladoRepository,
                        trasladoRepository,
                        viagemRepository,
                        motoristaRepository,
                        veiculoRepository,
                        operacaoTrasladoMapper,
                        historicoStatusOperacaoTrasladoRepository,
                        historicoStatusOperacaoTrasladoMapper
                );
    }

    @Test
    void devePermitirVincularSetimoPassageiroEmVeiculoComCapacidadeSete() {

        Viagem viagem = new Viagem();
        viagem.setId(1L);

        Veiculo veiculo = new Veiculo();
        veiculo.setId(10L);
        veiculo.setCapacidadePassageiros(7);
        veiculo.setAtivo(true);

        OperacaoTraslado operacao =
                new OperacaoTraslado();

        operacao.setId(100L);
        operacao.setViagem(viagem);
        operacao.setVeiculo(veiculo);

        Traslado traslado = new Traslado();
        traslado.setId(200L);
        traslado.setViagem(viagem);

        OperacaoTrasladoResponse responseEsperado =
                new OperacaoTrasladoResponse();

        responseEsperado.setId(100L);
        responseEsperado.setQuantidadePassageiros(7L);
        responseEsperado.setVagasDisponiveis(0L);

        when(operacaoTrasladoRepository.findById(100L))
                .thenReturn(Optional.of(operacao));

        when(trasladoRepository.findById(200L))
                .thenReturn(Optional.of(traslado));

        when(
                trasladoRepository.countByOperacaoTrasladoId(
                        100L
                )
        ).thenReturn(6L);

        when(
                operacaoTrasladoMapper.paraResponse(
                        operacao
                )
        ).thenReturn(responseEsperado);

        OperacaoTrasladoResponse response =
                operacaoTrasladoService.vincularTraslado(
                        100L,
                        200L
                );

        assertNotNull(response);

        assertEquals(
                operacao,
                traslado.getOperacaoTraslado()
        );

        verify(trasladoRepository)
                .save(traslado);

        verify(operacaoTrasladoMapper)
                .paraResponse(operacao);
    }

    @Test
    void deveBloquearOitavoPassageiroEmVeiculoComCapacidadeSete() {

        Viagem viagem = new Viagem();
        viagem.setId(1L);

        Veiculo veiculo = new Veiculo();
        veiculo.setId(10L);
        veiculo.setCapacidadePassageiros(7);

        OperacaoTraslado operacao =
                new OperacaoTraslado();

        operacao.setId(100L);
        operacao.setViagem(viagem);
        operacao.setVeiculo(veiculo);

        Traslado traslado = new Traslado();
        traslado.setId(200L);
        traslado.setViagem(viagem);

        when(operacaoTrasladoRepository.findById(100L))
                .thenReturn(Optional.of(operacao));

        when(trasladoRepository.findById(200L))
                .thenReturn(Optional.of(traslado));

        when(
                trasladoRepository.countByOperacaoTrasladoId(
                        100L
                )
        ).thenReturn(7L);

        assertThrows(
                CapacidadeVeiculoExcedidaException.class,
                () ->
                        operacaoTrasladoService
                                .vincularTraslado(
                                        100L,
                                        200L
                                )
        );

        assertNull(
                traslado.getOperacaoTraslado()
        );

        verify(trasladoRepository, never())
                .save(any(Traslado.class));

        verifyNoInteractions(
                operacaoTrasladoMapper
        );
    }

    @Test
    void devePermitirRepetirVinculoQuandoTrasladoJaPertencerAMesmaOperacao() {

        Viagem viagem = new Viagem();
        viagem.setId(1L);

        Veiculo veiculo = new Veiculo();
        veiculo.setCapacidadePassageiros(7);

        OperacaoTraslado operacao =
                new OperacaoTraslado();

        operacao.setId(100L);
        operacao.setViagem(viagem);
        operacao.setVeiculo(veiculo);

        Traslado traslado = new Traslado();
        traslado.setId(200L);
        traslado.setViagem(viagem);
        traslado.setOperacaoTraslado(
                operacao
        );

        OperacaoTrasladoResponse responseEsperado =
                new OperacaoTrasladoResponse();

        responseEsperado.setId(100L);

        when(operacaoTrasladoRepository.findById(100L))
                .thenReturn(Optional.of(operacao));

        when(trasladoRepository.findById(200L))
                .thenReturn(Optional.of(traslado));

        when(
                operacaoTrasladoMapper.paraResponse(
                        operacao
                )
        ).thenReturn(responseEsperado);

        OperacaoTrasladoResponse response =
                operacaoTrasladoService
                        .vincularTraslado(
                                100L,
                                200L
                        );

        assertNotNull(response);

        verify(
                trasladoRepository,
                never()
        ).countByOperacaoTrasladoId(
                anyLong()
        );

        verify(
                trasladoRepository,
                never()
        ).save(any(Traslado.class));
    }

    @Test
    void deveBloquearTrasladoDeOutraViagem() {

        Viagem viagemOperacao = new Viagem();
        viagemOperacao.setId(1L);

        Viagem viagemTraslado = new Viagem();
        viagemTraslado.setId(2L);

        Veiculo veiculo = new Veiculo();
        veiculo.setCapacidadePassageiros(7);

        OperacaoTraslado operacao =
                new OperacaoTraslado();

        operacao.setId(100L);
        operacao.setViagem(
                viagemOperacao
        );
        operacao.setVeiculo(veiculo);

        Traslado traslado = new Traslado();
        traslado.setId(200L);
        traslado.setViagem(
                viagemTraslado
        );

        when(operacaoTrasladoRepository.findById(100L))
                .thenReturn(Optional.of(operacao));

        when(trasladoRepository.findById(200L))
                .thenReturn(Optional.of(traslado));

        assertThrows(
                ViagemIncompativelException.class,
                () ->
                        operacaoTrasladoService
                                .vincularTraslado(
                                        100L,
                                        200L
                                )
        );

        verify(
                trasladoRepository,
                never()
        ).countByOperacaoTrasladoId(
                anyLong()
        );

        verify(
                trasladoRepository,
                never()
        ).save(any(Traslado.class));
    }

    @Test
    void deveLancarExcecaoQuandoOperacaoNaoExistir() {

        when(
                operacaoTrasladoRepository.findById(
                        999L
                )
        ).thenReturn(Optional.empty());

        assertThrows(
                OperacaoTrasladoNaoEncontradaException.class,
                () ->
                        operacaoTrasladoService
                                .vincularTraslado(
                                        999L,
                                        200L
                                )
        );

        verifyNoInteractions(
                trasladoRepository,
                operacaoTrasladoMapper
        );
    }

    @Test
    void deveLancarExcecaoQuandoTrasladoNaoExistir() {

        Viagem viagem = new Viagem();
        viagem.setId(1L);

        OperacaoTraslado operacao =
                new OperacaoTraslado();

        operacao.setId(100L);
        operacao.setViagem(viagem);

        when(
                operacaoTrasladoRepository.findById(
                        100L
                )
        ).thenReturn(
                Optional.of(operacao)
        );

        when(
                trasladoRepository.findById(
                        999L
                )
        ).thenReturn(Optional.empty());

        assertThrows(
                TrasladoNaoEncontradoException.class,
                () ->
                        operacaoTrasladoService
                                .vincularTraslado(
                                        100L,
                                        999L
                                )
        );

        verify(
                trasladoRepository,
                never()
        ).save(any(Traslado.class));

        verifyNoInteractions(
                operacaoTrasladoMapper
        );
    }

    @Test
    void devePermitirTrocarParaVeiculoComMesmaCapacidade() {

        Veiculo veiculoAtual = new Veiculo();
        veiculoAtual.setId(10L);
        veiculoAtual.setCapacidadePassageiros(7);

        Veiculo novoVeiculo = new Veiculo();
        novoVeiculo.setId(20L);
        novoVeiculo.setCapacidadePassageiros(7);
        novoVeiculo.setAtivo(true);

        OperacaoTraslado operacao =
                new OperacaoTraslado();

        operacao.setId(100L);
        operacao.setVeiculo(veiculoAtual);

        OperacaoTrasladoResponse responseEsperado =
                new OperacaoTrasladoResponse();

        responseEsperado.setId(100L);
        responseEsperado.setVeiculoId(20L);

        when(operacaoTrasladoRepository.findById(100L))
                .thenReturn(Optional.of(operacao));

        when(veiculoRepository.findById(20L))
                .thenReturn(Optional.of(novoVeiculo));

        when(
                trasladoRepository.countByOperacaoTrasladoId(
                        100L
                )
        ).thenReturn(7L);

        when(
                operacaoTrasladoRepository.save(
                        operacao
                )
        ).thenReturn(operacao);

        when(
                operacaoTrasladoMapper.paraResponse(
                        operacao
                )
        ).thenReturn(responseEsperado);

        OperacaoTrasladoResponse response =
                operacaoTrasladoService.alterarVeiculo(
                        100L,
                        20L
                );

        assertNotNull(response);

        assertEquals(
                novoVeiculo,
                operacao.getVeiculo()
        );

        verify(operacaoTrasladoRepository)
                .save(operacao);
    }

    @Test
    void devePermitirTrocarParaVeiculoComMaiorCapacidade() {

        Veiculo novoVeiculo = new Veiculo();
        novoVeiculo.setId(20L);
        novoVeiculo.setCapacidadePassageiros(10);
        novoVeiculo.setAtivo(true);

        OperacaoTraslado operacao =
                new OperacaoTraslado();

        operacao.setId(100L);

        when(operacaoTrasladoRepository.findById(100L))
                .thenReturn(Optional.of(operacao));

        when(veiculoRepository.findById(20L))
                .thenReturn(Optional.of(novoVeiculo));

        when(
                trasladoRepository.countByOperacaoTrasladoId(
                        100L
                )
        ).thenReturn(7L);

        when(
                operacaoTrasladoRepository.save(
                        operacao
                )
        ).thenReturn(operacao);

        when(
                operacaoTrasladoMapper.paraResponse(
                        operacao
                )
        ).thenReturn(
                new OperacaoTrasladoResponse()
        );

        operacaoTrasladoService.alterarVeiculo(
                100L,
                20L
        );

        assertEquals(
                novoVeiculo,
                operacao.getVeiculo()
        );

        verify(operacaoTrasladoRepository)
                .save(operacao);
    }

    @Test
    void deveBloquearTrocaParaVeiculoComCapacidadeMenorQueOcupacao() {

        Veiculo novoVeiculo = new Veiculo();
        novoVeiculo.setId(20L);
        novoVeiculo.setCapacidadePassageiros(4);
        novoVeiculo.setAtivo(true);

        OperacaoTraslado operacao =
                new OperacaoTraslado();

        operacao.setId(100L);

        when(operacaoTrasladoRepository.findById(100L))
                .thenReturn(Optional.of(operacao));

        when(veiculoRepository.findById(20L))
                .thenReturn(Optional.of(novoVeiculo));

        when(
                trasladoRepository.countByOperacaoTrasladoId(
                        100L
                )
        ).thenReturn(7L);

        assertThrows(
                CapacidadeVeiculoExcedidaException.class,
                () ->
                        operacaoTrasladoService.alterarVeiculo(
                                100L,
                                20L
                        )
        );

        verify(operacaoTrasladoRepository, never())
                .save(any(OperacaoTraslado.class));

        verifyNoInteractions(
                operacaoTrasladoMapper
        );
    }

    @Test
    void deveBloquearTrocaParaVeiculoInativo() {

        Veiculo novoVeiculo = new Veiculo();
        novoVeiculo.setId(20L);
        novoVeiculo.setAtivo(false);

        OperacaoTraslado operacao =
                new OperacaoTraslado();

        operacao.setId(100L);

        when(operacaoTrasladoRepository.findById(100L))
                .thenReturn(Optional.of(operacao));

        when(veiculoRepository.findById(20L))
                .thenReturn(Optional.of(novoVeiculo));

        assertThrows(
                VeiculoInativoException.class,
                () ->
                        operacaoTrasladoService.alterarVeiculo(
                                100L,
                                20L
                        )
        );

        verify(
                trasladoRepository,
                never()
        ).countByOperacaoTrasladoId(
                anyLong()
        );

        verify(
                operacaoTrasladoRepository,
                never()
        ).save(any(OperacaoTraslado.class));
    }

    @Test
    void deveLancarExcecaoQuandoNovoVeiculoNaoExistir() {

        OperacaoTraslado operacao =
                new OperacaoTraslado();

        operacao.setId(100L);

        when(operacaoTrasladoRepository.findById(100L))
                .thenReturn(Optional.of(operacao));

        when(veiculoRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                VeiculoNaoEncontradoException.class,
                () ->
                        operacaoTrasladoService.alterarVeiculo(
                                100L,
                                999L
                        )
        );

        verify(
                trasladoRepository,
                never()
        ).countByOperacaoTrasladoId(
                anyLong()
        );

        verify(
                operacaoTrasladoRepository,
                never()
        ).save(any(OperacaoTraslado.class));
    }

    @Test
    void deveListarOperacoesPorViagem() {

        Viagem viagem = new Viagem();
        viagem.setId(1L);

        OperacaoTraslado operacao1 =
                new OperacaoTraslado();

        operacao1.setId(100L);
        operacao1.setViagem(viagem);

        OperacaoTraslado operacao2 =
                new OperacaoTraslado();

        operacao2.setId(101L);
        operacao2.setViagem(viagem);

        OperacaoTrasladoResponse response1 =
                new OperacaoTrasladoResponse();

        response1.setId(100L);

        OperacaoTrasladoResponse response2 =
                new OperacaoTrasladoResponse();

        response2.setId(101L);

        when(
                viagemRepository.findById(1L)
        ).thenReturn(
                Optional.of(viagem)
        );

        when(
                operacaoTrasladoRepository
                        .findByViagemId(1L)
        ).thenReturn(
                List.of(
                        operacao1,
                        operacao2
                )
        );

        when(
                operacaoTrasladoMapper
                        .paraResponse(operacao1)
        ).thenReturn(response1);

        when(
                operacaoTrasladoMapper
                        .paraResponse(operacao2)
        ).thenReturn(response2);

        List<OperacaoTrasladoResponse> response =
                operacaoTrasladoService
                        .listarPorViagem(1L);

        assertEquals(2, response.size());

        assertEquals(
                100L,
                response.get(0).getId()
        );

        assertEquals(
                101L,
                response.get(1).getId()
        );

        verify(
                operacaoTrasladoRepository
        ).findByViagemId(1L);
    }

    @Test
    void deveLancarExcecaoAoListarOperacoesDeViagemInexistente() {

        when(
                viagemRepository.findById(999L)
        ).thenReturn(
                Optional.empty()
        );

        assertThrows(
                ViagemNaoEncontradaException.class,
                () ->
                        operacaoTrasladoService
                                .listarPorViagem(
                                        999L
                                )
        );

        verify(
                operacaoTrasladoRepository,
                never()
        ).findByViagemId(
                anyLong()
        );

        verifyNoInteractions(
                operacaoTrasladoMapper
        );
    }

    @Test
    void deveAtualizarOperacaoComSucesso() {

        OperacaoTraslado operacao =
                new OperacaoTraslado();

        operacao.setId(100L);

        Motorista motorista =
                new Motorista();

        motorista.setId(10L);
        motorista.setNomeCompleto(
                "Lucas Cesar"
        );
        motorista.setAtivo(true);

        Veiculo veiculo =
                new Veiculo();

        veiculo.setId(20L);
        veiculo.setModelo(
                "Chevrolet Spin"
        );
        veiculo.setPlaca(
                "BRA2E19"
        );
        veiculo.setCapacidadePassageiros(7);
        veiculo.setAtivo(true);

        OperacaoTrasladoUpdateRequest request =
                new OperacaoTrasladoUpdateRequest();

        request.setTipo(
                TipoTraslado.AEROPORTO_PARA_HOSPEDAGEM
        );

        request.setAeroporto(
                Aeroporto.GRU
        );

        request.setDataHoraPrevista(
                LocalDateTime.of(
                        2027,
                        4,
                        29,
                        13,
                        0
                )
        );

        request.setLocalOrigem(
                "Aeroporto de Guarulhos"
        );

        request.setLocalDestino(
                "Hospedagem Beat Trips"
        );

        request.setMotoristaId(10L);
        request.setVeiculoId(20L);

        request.setObservacao(
                "Operação atualizada"
        );

        OperacaoTrasladoResponse response =
                new OperacaoTrasladoResponse();

        response.setId(100L);

        when(
                operacaoTrasladoRepository
                        .findById(100L)
        ).thenReturn(
                Optional.of(operacao)
        );

        when(
                motoristaRepository
                        .findById(10L)
        ).thenReturn(
                Optional.of(motorista)
        );

        when(
                veiculoRepository
                        .findById(20L)
        ).thenReturn(
                Optional.of(veiculo)
        );

        when(
                trasladoRepository
                        .countByOperacaoTrasladoId(100L)
        ).thenReturn(3L);

        when(
                operacaoTrasladoRepository
                        .save(operacao)
        ).thenReturn(operacao);

        when(
                operacaoTrasladoMapper
                        .paraResponse(operacao)
        ).thenReturn(response);

        OperacaoTrasladoResponse resultado =
                operacaoTrasladoService
                        .atualizarOperacao(
                                100L,
                                request
                        );

        assertEquals(
                100L,
                resultado.getId()
        );

        assertEquals(
                TipoTraslado.AEROPORTO_PARA_HOSPEDAGEM,
                operacao.getTipo()
        );

        assertEquals(
                Aeroporto.GRU,
                operacao.getAeroporto()
        );

        assertEquals(
                "Aeroporto de Guarulhos",
                operacao.getLocalOrigem()
        );

        assertEquals(
                "Hospedagem Beat Trips",
                operacao.getLocalDestino()
        );

        assertEquals(
                motorista,
                operacao.getMotorista()
        );

        assertEquals(
                veiculo,
                operacao.getVeiculo()
        );

        assertEquals(
                "Operação atualizada",
                operacao.getObservacao()
        );

        verify(
                operacaoTrasladoRepository
        ).save(operacao);
    }

    @Test
    void deveLancarExcecaoAoAtualizarOperacaoInexistente() {

        OperacaoTrasladoUpdateRequest request =
                new OperacaoTrasladoUpdateRequest();

        when(
                operacaoTrasladoRepository
                        .findById(999L)
        ).thenReturn(
                Optional.empty()
        );

        assertThrows(
                OperacaoTrasladoNaoEncontradaException.class,
                () ->
                        operacaoTrasladoService
                                .atualizarOperacao(
                                        999L,
                                        request
                                )
        );

        verify(
                operacaoTrasladoRepository,
                never()
        ).save(any());
    }

    @Test
    void deveLancarExcecaoAoAtualizarComMotoristaInexistente() {

        OperacaoTraslado operacao =
                new OperacaoTraslado();

        operacao.setId(100L);

        OperacaoTrasladoUpdateRequest request =
                new OperacaoTrasladoUpdateRequest();

        request.setMotoristaId(999L);

        when(
                operacaoTrasladoRepository
                        .findById(100L)
        ).thenReturn(
                Optional.of(operacao)
        );

        when(
                motoristaRepository
                        .findById(999L)
        ).thenReturn(
                Optional.empty()
        );

        assertThrows(
                MotoristaNaoEncontradoException.class,
                () ->
                        operacaoTrasladoService
                                .atualizarOperacao(
                                        100L,
                                        request
                                )
        );

        verify(
                operacaoTrasladoRepository,
                never()
        ).save(any());
    }

    @Test
    void deveLancarExcecaoAoAtualizarComMotoristaInativo() {

        OperacaoTraslado operacao =
                new OperacaoTraslado();

        operacao.setId(100L);

        Motorista motorista =
                new Motorista();

        motorista.setId(10L);
        motorista.setAtivo(false);

        Veiculo veiculo =
                new Veiculo();

        veiculo.setId(20L);
        veiculo.setAtivo(true);
        veiculo.setCapacidadePassageiros(7);

        OperacaoTrasladoUpdateRequest request =
                new OperacaoTrasladoUpdateRequest();

        request.setMotoristaId(10L);
        request.setVeiculoId(20L);

        when(
                operacaoTrasladoRepository
                        .findById(100L)
        ).thenReturn(
                Optional.of(operacao)
        );

        when(
                motoristaRepository
                        .findById(10L)
        ).thenReturn(
                Optional.of(motorista)
        );

        when(
                veiculoRepository
                        .findById(20L)
        ).thenReturn(
                Optional.of(veiculo)
        );

        assertThrows(
                MotoristaInativoException.class,
                () ->
                        operacaoTrasladoService
                                .atualizarOperacao(
                                        100L,
                                        request
                                )
        );

        verify(
                operacaoTrasladoRepository,
                never()
        ).save(any());
    }

    @Test
    void deveLancarExcecaoAoAtualizarComVeiculoInexistente() {

        OperacaoTraslado operacao =
                new OperacaoTraslado();

        operacao.setId(100L);

        Motorista motorista =
                new Motorista();

        motorista.setId(10L);
        motorista.setAtivo(true);

        OperacaoTrasladoUpdateRequest request =
                new OperacaoTrasladoUpdateRequest();

        request.setMotoristaId(10L);
        request.setVeiculoId(999L);

        when(
                operacaoTrasladoRepository
                        .findById(100L)
        ).thenReturn(
                Optional.of(operacao)
        );

        when(
                motoristaRepository
                        .findById(10L)
        ).thenReturn(
                Optional.of(motorista)
        );

        when(
                veiculoRepository
                        .findById(999L)
        ).thenReturn(
                Optional.empty()
        );

        assertThrows(
                VeiculoNaoEncontradoException.class,
                () ->
                        operacaoTrasladoService
                                .atualizarOperacao(
                                        100L,
                                        request
                                )
        );

        verify(
                operacaoTrasladoRepository,
                never()
        ).save(any());
    }

    @Test
    void deveLancarExcecaoAoAtualizarComVeiculoInativo() {

        OperacaoTraslado operacao =
                new OperacaoTraslado();

        operacao.setId(100L);

        Motorista motorista =
                new Motorista();

        motorista.setId(10L);
        motorista.setAtivo(true);

        Veiculo veiculo =
                new Veiculo();

        veiculo.setId(20L);
        veiculo.setAtivo(false);
        veiculo.setCapacidadePassageiros(7);

        OperacaoTrasladoUpdateRequest request =
                new OperacaoTrasladoUpdateRequest();

        request.setMotoristaId(10L);
        request.setVeiculoId(20L);

        when(
                operacaoTrasladoRepository
                        .findById(100L)
        ).thenReturn(
                Optional.of(operacao)
        );

        when(
                motoristaRepository
                        .findById(10L)
        ).thenReturn(
                Optional.of(motorista)
        );

        when(
                veiculoRepository
                        .findById(20L)
        ).thenReturn(
                Optional.of(veiculo)
        );

        assertThrows(
                VeiculoInativoException.class,
                () ->
                        operacaoTrasladoService
                                .atualizarOperacao(
                                        100L,
                                        request
                                )
        );

        verify(
                operacaoTrasladoRepository,
                never()
        ).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoVeiculoNaoComportarOcupacaoNaAtualizacao() {

        OperacaoTraslado operacao =
                new OperacaoTraslado();

        operacao.setId(100L);

        Motorista motorista =
                new Motorista();

        motorista.setId(10L);
        motorista.setAtivo(true);

        Veiculo veiculo =
                new Veiculo();

        veiculo.setId(20L);
        veiculo.setAtivo(true);
        veiculo.setCapacidadePassageiros(4);

        OperacaoTrasladoUpdateRequest request =
                new OperacaoTrasladoUpdateRequest();

        request.setMotoristaId(10L);
        request.setVeiculoId(20L);

        when(
                operacaoTrasladoRepository
                        .findById(100L)
        ).thenReturn(
                Optional.of(operacao)
        );

        when(
                motoristaRepository
                        .findById(10L)
        ).thenReturn(
                Optional.of(motorista)
        );

        when(
                veiculoRepository
                        .findById(20L)
        ).thenReturn(
                Optional.of(veiculo)
        );

        when(
                trasladoRepository
                        .countByOperacaoTrasladoId(100L)
        ).thenReturn(5L);

        assertThrows(
                CapacidadeVeiculoExcedidaException.class,
                () ->
                        operacaoTrasladoService
                                .atualizarOperacao(
                                        100L,
                                        request
                                )
        );

        verify(
                operacaoTrasladoRepository,
                never()
        ).save(any());
    }

    @Test
    void deveExcluirOperacaoVaziaComSucesso() {

        OperacaoTraslado operacao =
                new OperacaoTraslado();

        operacao.setId(100L);

        when(
                operacaoTrasladoRepository
                        .findById(100L)
        ).thenReturn(
                Optional.of(operacao)
        );

        when(
                trasladoRepository
                        .countByOperacaoTrasladoId(100L)
        ).thenReturn(0L);

        operacaoTrasladoService
                .excluirOperacao(100L);

        verify(
                operacaoTrasladoRepository
        ).delete(operacao);
    }

    @Test
    void deveLancarExcecaoAoExcluirOperacaoComPassageiros() {

        OperacaoTraslado operacao =
                new OperacaoTraslado();

        operacao.setId(100L);

        when(
                operacaoTrasladoRepository
                        .findById(100L)
        ).thenReturn(
                Optional.of(operacao)
        );

        when(
                trasladoRepository
                        .countByOperacaoTrasladoId(100L)
        ).thenReturn(2L);

        assertThrows(
                OperacaoTrasladoComPassageirosException.class,
                () ->
                        operacaoTrasladoService
                                .excluirOperacao(100L)
        );

        verify(
                operacaoTrasladoRepository,
                never()
        ).delete(any());
    }

    @Test
    void deveLancarExcecaoAoExcluirOperacaoInexistente() {

        when(
                operacaoTrasladoRepository
                        .findById(999L)
        ).thenReturn(
                Optional.empty()
        );

        assertThrows(
                OperacaoTrasladoNaoEncontradaException.class,
                () ->
                        operacaoTrasladoService
                                .excluirOperacao(999L)
        );

        verify(
                operacaoTrasladoRepository,
                never()
        ).delete(any());
    }

    @Test
    void deveListarPassageirosDaOperacao() {

        OperacaoTraslado operacao =
                new OperacaoTraslado();

        operacao.setId(100L);

        Hospede hospede1 =
                new Hospede();

        hospede1.setId(1L);
        hospede1.setNomeCompleto(
                "Robson Gabriel"
        );

        Hospede hospede2 =
                new Hospede();

        hospede2.setId(2L);
        hospede2.setNomeCompleto(
                "Lucas Cesar"
        );

        Traslado traslado1 =
                new Traslado();

        traslado1.setId(200L);
        traslado1.setHospede(
                hospede1
        );

        Traslado traslado2 =
                new Traslado();

        traslado2.setId(201L);
        traslado2.setHospede(
                hospede2
        );

        when(
                operacaoTrasladoRepository
                        .findById(100L)
        ).thenReturn(
                Optional.of(operacao)
        );

        when(
                trasladoRepository
                        .findByOperacaoTrasladoId(
                                100L
                        )
        ).thenReturn(
                List.of(
                        traslado1,
                        traslado2
                )
        );

        List<OperacaoTrasladoPassageiroResponse> resultado =
                operacaoTrasladoService
                        .listarPassageiros(
                                100L
                        );

        assertEquals(
                2,
                resultado.size()
        );

        assertEquals(
                200L,
                resultado.get(0)
                        .getTrasladoId()
        );

        assertEquals(
                1L,
                resultado.get(0)
                        .getHospedeId()
        );

        assertEquals(
                "Robson Gabriel",
                resultado.get(0)
                        .getHospedeNome()
        );

        assertEquals(
                201L,
                resultado.get(1)
                        .getTrasladoId()
        );

        assertEquals(
                2L,
                resultado.get(1)
                        .getHospedeId()
        );

        assertEquals(
                "Lucas Cesar",
                resultado.get(1)
                        .getHospedeNome()
        );
    }

    @Test
    void deveRetornarListaVaziaQuandoOperacaoNaoPossuirPassageiros() {

        OperacaoTraslado operacao =
                new OperacaoTraslado();

        operacao.setId(100L);

        when(
                operacaoTrasladoRepository
                        .findById(100L)
        ).thenReturn(
                Optional.of(operacao)
        );

        when(
                trasladoRepository
                        .findByOperacaoTrasladoId(
                                100L
                        )
        ).thenReturn(
                List.of()
        );

        List<OperacaoTrasladoPassageiroResponse> resultado =
                operacaoTrasladoService
                        .listarPassageiros(
                                100L
                        );

        assertTrue(
                resultado.isEmpty()
        );
    }

    @Test
    void deveLancarExcecaoAoListarPassageirosDeOperacaoInexistente() {

        when(
                operacaoTrasladoRepository
                        .findById(999L)
        ).thenReturn(
                Optional.empty()
        );

        assertThrows(
                OperacaoTrasladoNaoEncontradaException.class,
                () ->
                        operacaoTrasladoService
                                .listarPassageiros(
                                        999L
                                )
        );

        verify(
                trasladoRepository,
                never()
        ).findByOperacaoTrasladoId(
                anyLong()
        );
    }

    @ParameterizedTest
    @CsvSource({
            "AGUARDANDO, EM_ANDAMENTO",
            "AGUARDANDO, CANCELADO",
            "EM_ANDAMENTO, CONCLUIDO",
            "EM_ANDAMENTO, CANCELADO"
    })
    void deveAtualizarStatusQuandoTransicaoForValida(
            StatusTraslado statusAtual,
            StatusTraslado novoStatus
    ) {

        OperacaoTraslado operacao =
                new OperacaoTraslado();

        operacao.setId(100L);
        operacao.setStatus(statusAtual);

        OperacaoTrasladoStatusRequest request =
                new OperacaoTrasladoStatusRequest();

        request.setStatus(novoStatus);

        OperacaoTrasladoResponse responseEsperado =
                new OperacaoTrasladoResponse();

        responseEsperado.setId(100L);
        responseEsperado.setStatus(novoStatus);

        when(
                operacaoTrasladoRepository.findById(100L)
        ).thenReturn(
                Optional.of(operacao)
        );

        when(
                operacaoTrasladoRepository.save(operacao)
        ).thenReturn(operacao);

        when(
                operacaoTrasladoMapper.paraResponse(operacao)
        ).thenReturn(responseEsperado);

        OperacaoTrasladoResponse response =
                operacaoTrasladoService.atualizarStatus(
                        100L,
                        request
                );

        assertEquals(
                novoStatus,
                operacao.getStatus()
        );

        assertEquals(
                novoStatus,
                response.getStatus()
        );

        verify(
                operacaoTrasladoRepository
        ).save(operacao);
    }

    @ParameterizedTest
    @CsvSource({
            "AGUARDANDO, CONCLUIDO",
            "EM_ANDAMENTO, AGUARDANDO",
            "CONCLUIDO, EM_ANDAMENTO",
            "CANCELADO, AGUARDANDO"
    })
    void deveBloquearTransicaoDeStatusInvalida(
            StatusTraslado statusAtual,
            StatusTraslado novoStatus
    ) {

        OperacaoTraslado operacao =
                new OperacaoTraslado();

        operacao.setId(100L);
        operacao.setStatus(statusAtual);

        OperacaoTrasladoStatusRequest request =
                new OperacaoTrasladoStatusRequest();

        request.setStatus(novoStatus);

        when(
                operacaoTrasladoRepository.findById(100L)
        ).thenReturn(
                Optional.of(operacao)
        );

        assertThrows(
                TransicaoStatusTrasladoInvalidaException.class,
                () ->
                        operacaoTrasladoService.atualizarStatus(
                                100L,
                                request
                        )
        );

        assertEquals(
                statusAtual,
                operacao.getStatus()
        );

        verify(
                operacaoTrasladoRepository,
                never()
        ).save(any(OperacaoTraslado.class));

        verifyNoInteractions(
                operacaoTrasladoMapper
        );
    }

    @Test
    void deveLancarExcecaoAoAtualizarStatusDeOperacaoInexistente() {

        OperacaoTrasladoStatusRequest request =
                new OperacaoTrasladoStatusRequest();

        request.setStatus(
                StatusTraslado.EM_ANDAMENTO
        );

        when(
                operacaoTrasladoRepository.findById(999L)
        ).thenReturn(
                Optional.empty()
        );

        assertThrows(
                OperacaoTrasladoNaoEncontradaException.class,
                () ->
                        operacaoTrasladoService.atualizarStatus(
                                999L,
                                request
                        )
        );

        verify(
                operacaoTrasladoRepository,
                never()
        ).save(any(OperacaoTraslado.class));

        verifyNoInteractions(
                operacaoTrasladoMapper
        );
    }

    @Test
    void deveRegistrarHistoricoAoAtualizarStatus() {

        OperacaoTraslado operacao =
                new OperacaoTraslado();

        operacao.setId(100L);
        operacao.setStatus(
                StatusTraslado.AGUARDANDO
        );

        OperacaoTrasladoStatusRequest request =
                new OperacaoTrasladoStatusRequest();

        request.setStatus(
                StatusTraslado.EM_ANDAMENTO
        );

        OperacaoTrasladoResponse responseEsperado =
                new OperacaoTrasladoResponse();

        responseEsperado.setId(100L);
        responseEsperado.setStatus(
                StatusTraslado.EM_ANDAMENTO
        );

        when(
                operacaoTrasladoRepository.findById(100L)
        ).thenReturn(
                Optional.of(operacao)
        );

        when(
                operacaoTrasladoRepository.save(operacao)
        ).thenReturn(operacao);

        when(
                operacaoTrasladoMapper.paraResponse(operacao)
        ).thenReturn(responseEsperado);

        operacaoTrasladoService.atualizarStatus(
                100L,
                request
        );

        verify(
                historicoStatusOperacaoTrasladoRepository
        ).save(
                argThat(
                        historico ->
                                historico.getOperacaoTraslado()
                                        .equals(operacao)
                                        &&
                                        historico.getStatusAnterior()
                                                == StatusTraslado.AGUARDANDO
                                        &&
                                        historico.getNovoStatus()
                                                == StatusTraslado.EM_ANDAMENTO
                                        &&
                                        historico.getMotivo()
                                                .equals(
                                                        "Alteração normal de status"
                                                )
                                        &&
                                        historico.getDataHora()
                                                != null
                )
        );
    }

    @Test
    void deveListarHistoricoDeStatusDaOperacao() {

        OperacaoTraslado operacao =
                new OperacaoTraslado();

        operacao.setId(100L);

        HistoricoStatusOperacaoTraslado historico =
                new HistoricoStatusOperacaoTraslado();

        historico.setId(1L);
        historico.setOperacaoTraslado(
                operacao
        );
        historico.setStatusAnterior(
                StatusTraslado.AGUARDANDO
        );
        historico.setNovoStatus(
                StatusTraslado.EM_ANDAMENTO
        );
        historico.setMotivo(
                "Alteração normal de status"
        );

        HistoricoStatusOperacaoTrasladoResponse responseEsperado =
                new HistoricoStatusOperacaoTrasladoResponse();

        responseEsperado.setId(1L);
        responseEsperado.setOperacaoTrasladoId(100L);
        responseEsperado.setStatusAnterior(
                StatusTraslado.AGUARDANDO
        );
        responseEsperado.setNovoStatus(
                StatusTraslado.EM_ANDAMENTO
        );

        when(
                operacaoTrasladoRepository.findById(100L)
        ).thenReturn(
                Optional.of(operacao)
        );

        when(
                historicoStatusOperacaoTrasladoRepository
                        .findByOperacaoTrasladoId(100L)
        ).thenReturn(
                List.of(historico)
        );

        when(
                historicoStatusOperacaoTrasladoMapper
                        .paraResponse(historico)
        ).thenReturn(
                responseEsperado
        );

        List<HistoricoStatusOperacaoTrasladoResponse> response =
                operacaoTrasladoService
                        .listarHistoricoStatus(
                                100L
                        );

        assertEquals(
                1,
                response.size()
        );

        assertEquals(
                StatusTraslado.AGUARDANDO,
                response.get(0)
                        .getStatusAnterior()
        );

        assertEquals(
                StatusTraslado.EM_ANDAMENTO,
                response.get(0)
                        .getNovoStatus()
        );

        verify(
                historicoStatusOperacaoTrasladoRepository
        ).findByOperacaoTrasladoId(
                100L
        );
    }



}
