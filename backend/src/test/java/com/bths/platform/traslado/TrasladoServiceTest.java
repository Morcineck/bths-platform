package com.bths.platform.traslado;

import com.bths.platform.alocacao.exception.ViagemIncompativelException;
import com.bths.platform.hospede.Hospede;
import com.bths.platform.hospede.HospedeRepository;
import com.bths.platform.hospede.exception.HospedeNaoEncontradoException;
import com.bths.platform.motorista.Motorista;
import com.bths.platform.motorista.MotoristaRepository;
import com.bths.platform.motorista.exception.MotoristaNaoEncontradoException;
import com.bths.platform.traslado.dto.*;
import com.bths.platform.traslado.enums.Aeroporto;
import com.bths.platform.traslado.enums.StatusTraslado;
import com.bths.platform.traslado.enums.TipoTraslado;
import com.bths.platform.traslado.exception.*;
import com.bths.platform.traslado.mapper.HistoricoStatusTrasladoMapper;
import com.bths.platform.traslado.mapper.TrasladoMapper;
import com.bths.platform.veiculo.Veiculo;
import com.bths.platform.veiculo.VeiculoRepository;
import com.bths.platform.veiculo.exception.VeiculoNaoEncontradoException;
import com.bths.platform.viagem.Viagem;
import com.bths.platform.viagem.ViagemRepository;
import com.bths.platform.viagem.exception.ViagemNaoEncontradaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrasladoServiceTest {

    @Mock
    private TrasladoRepository trasladoRepository;

    @Mock
    private HospedeRepository hospedeRepository;

    @Mock
    private ViagemRepository viagemRepository;

    @Mock
    private TrasladoMapper trasladoMapper;

    @Mock
    private HistoricoStatusTrasladoRepository historicoStatusTrasladoRepository;

    @Mock
    private HistoricoStatusTrasladoMapper historicoStatusTrasladoMapper;

    private TrasladoService trasladoService;

    @Mock
    private MotoristaRepository motoristaRepository;

    @Mock
    private VeiculoRepository veiculoRepository;

    @BeforeEach
    void setUp() {

        trasladoService = new TrasladoService(
                trasladoRepository,
                hospedeRepository,
                viagemRepository,
                trasladoMapper,
                historicoStatusTrasladoRepository,
                historicoStatusTrasladoMapper,
                motoristaRepository,
                veiculoRepository
        );
    }


    @Test
    void deveCadastrarTrasladoComSucesso() {

        Viagem viagem = new Viagem();
        viagem.setId(1L);
        viagem.setNome("Tomorrowland Brasil 2027");

        Hospede hospede = new Hospede();
        hospede.setId(1L);
        hospede.setNomeCompleto("João da Silva");
        hospede.setViagem(viagem);

        TrasladoRequest request = new TrasladoRequest();
        request.setHospedeId(1L);
        request.setViagemId(1L);
        request.setTipo(TipoTraslado.AEROPORTO_PARA_HOSPEDAGEM);
        request.setAeroporto(Aeroporto.GRU);
        request.setDataHoraPrevista(
                LocalDateTime.of(2027, 4, 29, 10, 0)
        );
        request.setNumeroVoo("G31234");
        request.setCompanhiaAerea("Gol");
        request.setLocalOrigem("Aeroporto de Guarulhos");
        request.setLocalDestino("Hospedagem Beat Trips");
        request.setObservacoes("Teste de traslado");

        TrasladoResponse responseEsperado = new TrasladoResponse();
        responseEsperado.setId(1L);
        responseEsperado.setHospedeId(1L);
        responseEsperado.setHospedeNome("João da Silva");
        responseEsperado.setViagemId(1L);
        responseEsperado.setViagemNome("Tomorrowland Brasil 2027");
        responseEsperado.setStatus(StatusTraslado.AGUARDANDO);

        when(hospedeRepository.findById(1L))
                .thenReturn(Optional.of(hospede));

        when(viagemRepository.findById(1L))
                .thenReturn(Optional.of(viagem));

        when(trasladoRepository.save(any(Traslado.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(trasladoMapper.paraResponse(any(Traslado.class)))
                .thenReturn(responseEsperado);

        TrasladoResponse response =
                trasladoService.cadastrarTraslado(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(1L, response.getHospedeId());
        assertEquals("João da Silva", response.getHospedeNome());
        assertEquals(StatusTraslado.AGUARDANDO, response.getStatus());

        verify(hospedeRepository).findById(1L);
        verify(viagemRepository).findById(1L);
        verify(trasladoRepository).save(any(Traslado.class));
        verify(trasladoMapper).paraResponse(any(Traslado.class));
    }

    @Test
    void deveLancarExcecaoQuandoHospedeNaoExistirAoCadastrarTraslado() {

        TrasladoRequest request = new TrasladoRequest();
        request.setHospedeId(999L);
        request.setViagemId(1L);

        when(hospedeRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                HospedeNaoEncontradoException.class,
                () -> trasladoService.cadastrarTraslado(request)
        );

        verify(hospedeRepository).findById(999L);

        verifyNoInteractions(
                viagemRepository,
                trasladoRepository,
                trasladoMapper
        );
    }

    @Test
    void deveLancarExcecaoQuandoViagemNaoExistirAoCadastrarTraslado() {

        Viagem viagemHospede = new Viagem();
        viagemHospede.setId(1L);

        Hospede hospede = new Hospede();
        hospede.setId(1L);
        hospede.setViagem(viagemHospede);

        TrasladoRequest request = new TrasladoRequest();
        request.setHospedeId(1L);
        request.setViagemId(999L);

        when(hospedeRepository.findById(1L))
                .thenReturn(Optional.of(hospede));

        when(viagemRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ViagemNaoEncontradaException.class,
                () -> trasladoService.cadastrarTraslado(request)
        );

        verify(hospedeRepository).findById(1L);
        verify(viagemRepository).findById(999L);

        verifyNoInteractions(
                trasladoRepository,
                trasladoMapper
        );
    }

    @Test
    void deveLancarExcecaoQuandoHospedeNaoPertencerAViagem() {

        Viagem viagemDoHospede = new Viagem();
        viagemDoHospede.setId(1L);

        Viagem viagemInformada = new Viagem();
        viagemInformada.setId(2L);

        Hospede hospede = new Hospede();
        hospede.setId(1L);
        hospede.setViagem(viagemDoHospede);

        TrasladoRequest request = new TrasladoRequest();
        request.setHospedeId(1L);
        request.setViagemId(2L);

        when(hospedeRepository.findById(1L))
                .thenReturn(Optional.of(hospede));

        when(viagemRepository.findById(2L))
                .thenReturn(Optional.of(viagemInformada));

        assertThrows(
                ViagemIncompativelException.class,
                () -> trasladoService.cadastrarTraslado(request)
        );

        verify(hospedeRepository).findById(1L);
        verify(viagemRepository).findById(2L);

        verifyNoInteractions(
                trasladoRepository,
                trasladoMapper
        );
    }

    @Test
    void deveLancarExcecaoQuandoAeroportoForObrigatorioENaoForInformado() {

        Viagem viagem = new Viagem();
        viagem.setId(1L);

        Hospede hospede = new Hospede();
        hospede.setId(1L);
        hospede.setViagem(viagem);

        TrasladoRequest request = new TrasladoRequest();
        request.setHospedeId(1L);
        request.setViagemId(1L);
        request.setTipo(TipoTraslado.AEROPORTO_PARA_HOSPEDAGEM);
        request.setAeroporto(null);

        when(hospedeRepository.findById(1L))
                .thenReturn(Optional.of(hospede));

        when(viagemRepository.findById(1L))
                .thenReturn(Optional.of(viagem));

        assertThrows(
                AeroportoObrigatorioException.class,
                () -> trasladoService.cadastrarTraslado(request)
        );

        verify(hospedeRepository).findById(1L);
        verify(viagemRepository).findById(1L);

        verifyNoInteractions(
                trasladoRepository,
                trasladoMapper
        );
    }

    @Test
    void devePermitirCadastrarTrasladoDoTipoOutroSemAeroporto() {

        Viagem viagem = new Viagem();
        viagem.setId(1L);
        viagem.setNome("Tomorrowland Brasil 2027");

        Hospede hospede = new Hospede();
        hospede.setId(1L);
        hospede.setNomeCompleto("João da Silva");
        hospede.setViagem(viagem);

        TrasladoRequest request = new TrasladoRequest();
        request.setHospedeId(1L);
        request.setViagemId(1L);
        request.setTipo(TipoTraslado.OUTRO);
        request.setAeroporto(null);
        request.setLocalOrigem("Rodoviária");
        request.setLocalDestino("Hospedagem Beat Trips");

        TrasladoResponse responseEsperado = new TrasladoResponse();
        responseEsperado.setId(1L);
        responseEsperado.setTipo(TipoTraslado.OUTRO);
        responseEsperado.setAeroporto(null);
        responseEsperado.setStatus(StatusTraslado.AGUARDANDO);

        when(hospedeRepository.findById(1L))
                .thenReturn(Optional.of(hospede));

        when(viagemRepository.findById(1L))
                .thenReturn(Optional.of(viagem));

        when(trasladoRepository.save(any(Traslado.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(trasladoMapper.paraResponse(any(Traslado.class)))
                .thenReturn(responseEsperado);

        TrasladoResponse response =
                trasladoService.cadastrarTraslado(request);

        assertNotNull(response);
        assertEquals(TipoTraslado.OUTRO, response.getTipo());
        assertNull(response.getAeroporto());
        assertEquals(StatusTraslado.AGUARDANDO, response.getStatus());

        verify(trasladoRepository).save(any(Traslado.class));
        verify(trasladoMapper).paraResponse(any(Traslado.class));
    }

    @Test
    void deveBuscarTrasladoPorIdComSucesso() {

        Traslado traslado = new Traslado();
        traslado.setId(1L);
        traslado.setStatus(StatusTraslado.AGUARDANDO);

        TrasladoResponse responseEsperado = new TrasladoResponse();
        responseEsperado.setId(1L);
        responseEsperado.setStatus(StatusTraslado.AGUARDANDO);

        when(trasladoRepository.findById(1L))
                .thenReturn(Optional.of(traslado));

        when(trasladoMapper.paraResponse(traslado))
                .thenReturn(responseEsperado);

        TrasladoResponse response =
                trasladoService.buscarTrasladoPorId(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(StatusTraslado.AGUARDANDO, response.getStatus());

        verify(trasladoRepository).findById(1L);
        verify(trasladoMapper).paraResponse(traslado);
    }

    @Test
    void deveLancarExcecaoQuandoTrasladoNaoExistirAoBuscarPorId() {

        when(trasladoRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                TrasladoNaoEncontradoException.class,
                () -> trasladoService.buscarTrasladoPorId(999L)
        );

        verify(trasladoRepository).findById(999L);
        verifyNoInteractions(trasladoMapper);
    }

    @Test
    void deveListarTrasladosPorViagemComSucesso() {

        Viagem viagem = new Viagem();
        viagem.setId(1L);
        viagem.setNome("Tomorrowland Brasil 2027");

        Traslado traslado1 = new Traslado();
        traslado1.setId(1L);
        traslado1.setViagem(viagem);

        Traslado traslado2 = new Traslado();
        traslado2.setId(2L);
        traslado2.setViagem(viagem);

        TrasladoResponse response1 = new TrasladoResponse();
        response1.setId(1L);
        response1.setViagemId(1L);

        TrasladoResponse response2 = new TrasladoResponse();
        response2.setId(2L);
        response2.setViagemId(1L);

        when(viagemRepository.findById(1L))
                .thenReturn(Optional.of(viagem));

        when(trasladoRepository.findByViagemId(1L))
                .thenReturn(List.of(traslado1, traslado2));

        when(trasladoMapper.paraResponse(traslado1))
                .thenReturn(response1);

        when(trasladoMapper.paraResponse(traslado2))
                .thenReturn(response2);

        List<TrasladoResponse> response =
                trasladoService.listarTrasladosPorViagem(1L);

        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals(1L, response.get(0).getId());
        assertEquals(2L, response.get(1).getId());

        verify(viagemRepository).findById(1L);
        verify(trasladoRepository).findByViagemId(1L);
        verify(trasladoMapper).paraResponse(traslado1);
        verify(trasladoMapper).paraResponse(traslado2);
    }

    @Test
    void deveLancarExcecaoQuandoViagemNaoExistirAoListarTraslados() {

        when(viagemRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ViagemNaoEncontradaException.class,
                () -> trasladoService.listarTrasladosPorViagem(999L)
        );

        verify(viagemRepository).findById(999L);

        verify(trasladoRepository, never())
                .findByViagemId(anyLong());

        verifyNoInteractions(trasladoMapper);
    }

    @Test
    void deveListarTrasladosPorHospedeComSucesso() {

        Hospede hospede = new Hospede();
        hospede.setId(1L);
        hospede.setNomeCompleto("João da Silva");

        Traslado traslado1 = new Traslado();
        traslado1.setId(1L);
        traslado1.setHospede(hospede);

        Traslado traslado2 = new Traslado();
        traslado2.setId(2L);
        traslado2.setHospede(hospede);

        TrasladoResponse response1 = new TrasladoResponse();
        response1.setId(1L);
        response1.setHospedeId(1L);

        TrasladoResponse response2 = new TrasladoResponse();
        response2.setId(2L);
        response2.setHospedeId(1L);

        when(hospedeRepository.findById(1L))
                .thenReturn(Optional.of(hospede));

        when(trasladoRepository.findByHospedeId(1L))
                .thenReturn(List.of(traslado1, traslado2));

        when(trasladoMapper.paraResponse(traslado1))
                .thenReturn(response1);

        when(trasladoMapper.paraResponse(traslado2))
                .thenReturn(response2);

        List<TrasladoResponse> response =
                trasladoService.listarTrasladosPorHospede(1L);

        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals(1L, response.get(0).getId());
        assertEquals(2L, response.get(1).getId());

        verify(hospedeRepository).findById(1L);
        verify(trasladoRepository).findByHospedeId(1L);
        verify(trasladoMapper).paraResponse(traslado1);
        verify(trasladoMapper).paraResponse(traslado2);
    }

    @Test
    void deveLancarExcecaoQuandoHospedeNaoExistirAoListarTraslados() {

        when(hospedeRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                HospedeNaoEncontradoException.class,
                () -> trasladoService.listarTrasladosPorHospede(999L)
        );

        verify(hospedeRepository).findById(999L);

        verify(trasladoRepository, never())
                .findByHospedeId(anyLong());

        verifyNoInteractions(trasladoMapper);
    }

    @Test
    void deveAtualizarTrasladoComSucesso() {

        Traslado traslado = new Traslado();
        traslado.setId(1L);
        traslado.setStatus(StatusTraslado.AGUARDANDO);

        TrasladoUpdateRequest request = new TrasladoUpdateRequest();
        request.setTipo(TipoTraslado.HOSPEDAGEM_PARA_AEROPORTO);
        request.setAeroporto(Aeroporto.GRU);
        request.setDataHoraPrevista(
                LocalDateTime.of(2027, 5, 3, 10, 0)
        );
        request.setNumeroVoo("G39876");
        request.setCompanhiaAerea("Gol");
        request.setLocalOrigem("Hospedagem Beat Trips");
        request.setLocalDestino("Aeroporto de Guarulhos");
        request.setObservacoes("Traslado atualizado");

        TrasladoResponse responseEsperado = new TrasladoResponse();
        responseEsperado.setId(1L);
        responseEsperado.setTipo(TipoTraslado.HOSPEDAGEM_PARA_AEROPORTO);
        responseEsperado.setAeroporto(Aeroporto.GRU);
        responseEsperado.setStatus(StatusTraslado.AGUARDANDO);
        responseEsperado.setNumeroVoo("G39876");

        when(trasladoRepository.findById(1L))
                .thenReturn(Optional.of(traslado));

        when(trasladoRepository.save(traslado))
                .thenReturn(traslado);

        when(trasladoMapper.paraResponse(traslado))
                .thenReturn(responseEsperado);

        TrasladoResponse response =
                trasladoService.atualizarTraslado(1L, request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(
                TipoTraslado.HOSPEDAGEM_PARA_AEROPORTO,
                response.getTipo()
        );
        assertEquals(Aeroporto.GRU, response.getAeroporto());
        assertEquals(StatusTraslado.AGUARDANDO, response.getStatus());
        assertEquals("G39876", response.getNumeroVoo());

        verify(trasladoRepository).findById(1L);
        verify(trasladoRepository).save(traslado);
        verify(trasladoMapper).paraResponse(traslado);
    }

    @Test
    void deveLancarExcecaoQuandoTrasladoNaoExistirAoAtualizar() {

        TrasladoUpdateRequest request = new TrasladoUpdateRequest();

        when(trasladoRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                TrasladoNaoEncontradoException.class,
                () -> trasladoService.atualizarTraslado(999L, request)
        );

        verify(trasladoRepository).findById(999L);

        verify(trasladoRepository, never())
                .save(any(Traslado.class));

        verifyNoInteractions(trasladoMapper);
    }

    @Test
    void deveLancarExcecaoQuandoAeroportoForObrigatorioAoAtualizarTraslado() {

        Traslado traslado = new Traslado();
        traslado.setId(1L);
        traslado.setStatus(StatusTraslado.AGUARDANDO);

        TrasladoUpdateRequest request = new TrasladoUpdateRequest();
        request.setTipo(TipoTraslado.HOSPEDAGEM_PARA_AEROPORTO);
        request.setAeroporto(null);

        when(trasladoRepository.findById(1L))
                .thenReturn(Optional.of(traslado));

        assertThrows(
                AeroportoObrigatorioException.class,
                () -> trasladoService.atualizarTraslado(1L, request)
        );

        verify(trasladoRepository).findById(1L);

        verify(trasladoRepository, never())
                .save(any(Traslado.class));

        verifyNoInteractions(trasladoMapper);
    }

    @Test
    void deveAtualizarStatusDeAguardandoParaEmAndamentoComSucesso() {

        Traslado traslado = new Traslado();
        traslado.setId(1L);
        traslado.setStatus(StatusTraslado.AGUARDANDO);

        TrasladoStatusRequest request = new TrasladoStatusRequest();
        request.setStatus(StatusTraslado.EM_ANDAMENTO);

        TrasladoResponse responseEsperado = new TrasladoResponse();
        responseEsperado.setId(1L);
        responseEsperado.setStatus(StatusTraslado.EM_ANDAMENTO);

        when(trasladoRepository.findById(1L))
                .thenReturn(Optional.of(traslado));

        when(trasladoRepository.save(traslado))
                .thenReturn(traslado);

        when(trasladoMapper.paraResponse(traslado))
                .thenReturn(responseEsperado);

        TrasladoResponse response =
                trasladoService.atualizarStatusTraslado(1L, request);

        assertNotNull(response);
        assertEquals(StatusTraslado.EM_ANDAMENTO, response.getStatus());
        assertEquals(StatusTraslado.EM_ANDAMENTO, traslado.getStatus());

        verify(trasladoRepository).findById(1L);

        ArgumentCaptor<HistoricoStatusTraslado> captor =
                ArgumentCaptor.forClass(HistoricoStatusTraslado.class);

        verify(historicoStatusTrasladoRepository)
                .save(captor.capture());

        HistoricoStatusTraslado historicoSalvo = captor.getValue();

        assertEquals(
                StatusTraslado.AGUARDANDO,
                historicoSalvo.getStatusAnterior()
        );

        assertEquals(
                StatusTraslado.EM_ANDAMENTO,
                historicoSalvo.getNovoStatus()
        );

        assertEquals(
                "Alteração normal de status",
                historicoSalvo.getMotivo()
        );

        assertEquals(
                traslado,
                historicoSalvo.getTraslado()
        );

        assertNotNull(historicoSalvo.getDataHora());

        verify(trasladoRepository).save(traslado);
        verify(trasladoMapper).paraResponse(traslado);
    }

    @Test
    void deveAtualizarStatusDeEmAndamentoParaConcluidoComSucesso() {

        Traslado traslado = new Traslado();
        traslado.setId(1L);
        traslado.setStatus(StatusTraslado.EM_ANDAMENTO);

        TrasladoStatusRequest request = new TrasladoStatusRequest();
        request.setStatus(StatusTraslado.CONCLUIDO);

        TrasladoResponse responseEsperado = new TrasladoResponse();
        responseEsperado.setId(1L);
        responseEsperado.setStatus(StatusTraslado.CONCLUIDO);

        when(trasladoRepository.findById(1L))
                .thenReturn(Optional.of(traslado));

        when(trasladoRepository.save(traslado))
                .thenReturn(traslado);

        when(trasladoMapper.paraResponse(traslado))
                .thenReturn(responseEsperado);

        TrasladoResponse response =
                trasladoService.atualizarStatusTraslado(1L, request);

        assertNotNull(response);
        assertEquals(StatusTraslado.CONCLUIDO, response.getStatus());
        assertEquals(StatusTraslado.CONCLUIDO, traslado.getStatus());

        ArgumentCaptor<HistoricoStatusTraslado> captor =
                ArgumentCaptor.forClass(HistoricoStatusTraslado.class);

        verify(historicoStatusTrasladoRepository)
                .save(captor.capture());

        HistoricoStatusTraslado historicoSalvo = captor.getValue();

        assertEquals(
                StatusTraslado.EM_ANDAMENTO,
                historicoSalvo.getStatusAnterior()
        );

        assertEquals(
                StatusTraslado.CONCLUIDO,
                historicoSalvo.getNovoStatus()
        );

        assertEquals(
                "Alteração normal de status",
                historicoSalvo.getMotivo()
        );

        assertEquals(traslado, historicoSalvo.getTraslado());
        assertNotNull(historicoSalvo.getDataHora());

        verify(trasladoRepository).findById(1L);
        verify(trasladoRepository).save(traslado);
        verify(trasladoMapper).paraResponse(traslado);
    }

    @Test
    void deveAtualizarStatusDeAguardandoParaCanceladoComSucesso() {

        Traslado traslado = new Traslado();
        traslado.setId(1L);
        traslado.setStatus(StatusTraslado.AGUARDANDO);

        TrasladoStatusRequest request = new TrasladoStatusRequest();
        request.setStatus(StatusTraslado.CANCELADO);

        TrasladoResponse responseEsperado = new TrasladoResponse();
        responseEsperado.setId(1L);
        responseEsperado.setStatus(StatusTraslado.CANCELADO);

        when(trasladoRepository.findById(1L))
                .thenReturn(Optional.of(traslado));

        when(trasladoRepository.save(traslado))
                .thenReturn(traslado);

        when(trasladoMapper.paraResponse(traslado))
                .thenReturn(responseEsperado);

        TrasladoResponse response =
                trasladoService.atualizarStatusTraslado(1L, request);

        assertNotNull(response);
        assertEquals(StatusTraslado.CANCELADO, response.getStatus());
        assertEquals(StatusTraslado.CANCELADO, traslado.getStatus());

        ArgumentCaptor<HistoricoStatusTraslado> captor =
                ArgumentCaptor.forClass(HistoricoStatusTraslado.class);

        verify(historicoStatusTrasladoRepository)
                .save(captor.capture());

        HistoricoStatusTraslado historicoSalvo = captor.getValue();

        assertEquals(
                StatusTraslado.AGUARDANDO,
                historicoSalvo.getStatusAnterior()
        );

        assertEquals(
                StatusTraslado.CANCELADO,
                historicoSalvo.getNovoStatus()
        );

        assertEquals(
                "Alteração normal de status",
                historicoSalvo.getMotivo()
        );

        assertEquals(traslado, historicoSalvo.getTraslado());
        assertNotNull(historicoSalvo.getDataHora());

        verify(trasladoRepository).findById(1L);
        verify(trasladoRepository).save(traslado);
        verify(trasladoMapper).paraResponse(traslado);
    }

    @Test
    void deveAtualizarStatusDeEmAndamentoParaCanceladoComSucesso() {

        Traslado traslado = new Traslado();
        traslado.setId(1L);
        traslado.setStatus(StatusTraslado.EM_ANDAMENTO);

        TrasladoStatusRequest request = new TrasladoStatusRequest();
        request.setStatus(StatusTraslado.CANCELADO);

        TrasladoResponse responseEsperado = new TrasladoResponse();
        responseEsperado.setId(1L);
        responseEsperado.setStatus(StatusTraslado.CANCELADO);

        when(trasladoRepository.findById(1L))
                .thenReturn(Optional.of(traslado));

        when(trasladoRepository.save(traslado))
                .thenReturn(traslado);

        when(trasladoMapper.paraResponse(traslado))
                .thenReturn(responseEsperado);

        TrasladoResponse response =
                trasladoService.atualizarStatusTraslado(1L, request);

        assertNotNull(response);
        assertEquals(StatusTraslado.CANCELADO, response.getStatus());
        assertEquals(StatusTraslado.CANCELADO, traslado.getStatus());

        ArgumentCaptor<HistoricoStatusTraslado> captor =
                ArgumentCaptor.forClass(HistoricoStatusTraslado.class);

        verify(historicoStatusTrasladoRepository)
                .save(captor.capture());

        HistoricoStatusTraslado historicoSalvo = captor.getValue();

        assertEquals(
                StatusTraslado.EM_ANDAMENTO,
                historicoSalvo.getStatusAnterior()
        );

        assertEquals(
                StatusTraslado.CANCELADO,
                historicoSalvo.getNovoStatus()
        );

        assertEquals(
                "Alteração normal de status",
                historicoSalvo.getMotivo()
        );

        assertEquals(traslado, historicoSalvo.getTraslado());
        assertNotNull(historicoSalvo.getDataHora());

        verify(trasladoRepository).findById(1L);
        verify(trasladoRepository).save(traslado);
        verify(trasladoMapper).paraResponse(traslado);
    }

    @Test
    void deveLancarExcecaoAoAlterarStatusDeConcluidoParaEmAndamento() {

        Traslado traslado = new Traslado();
        traslado.setId(1L);
        traslado.setStatus(StatusTraslado.CONCLUIDO);

        TrasladoStatusRequest request = new TrasladoStatusRequest();
        request.setStatus(StatusTraslado.EM_ANDAMENTO);

        when(trasladoRepository.findById(1L))
                .thenReturn(Optional.of(traslado));

        assertThrows(
                TransicaoStatusTrasladoInvalidaException.class,
                () -> trasladoService.atualizarStatusTraslado(1L, request)
        );

        verify(trasladoRepository).findById(1L);

        verify(historicoStatusTrasladoRepository, never())
                .save(any(HistoricoStatusTraslado.class));

        verify(trasladoRepository, never())
                .save(any(Traslado.class));

        verifyNoInteractions(trasladoMapper);
    }

    @Test
    void deveLancarExcecaoAoAlterarStatusDeCanceladoParaAguardando() {

        Traslado traslado = new Traslado();
        traslado.setId(1L);
        traslado.setStatus(StatusTraslado.CANCELADO);

        TrasladoStatusRequest request = new TrasladoStatusRequest();
        request.setStatus(StatusTraslado.AGUARDANDO);

        when(trasladoRepository.findById(1L))
                .thenReturn(Optional.of(traslado));

        assertThrows(
                TransicaoStatusTrasladoInvalidaException.class,
                () -> trasladoService.atualizarStatusTraslado(1L, request)
        );

        verify(trasladoRepository).findById(1L);

        verify(historicoStatusTrasladoRepository, never())
                .save(any(HistoricoStatusTraslado.class));

        verify(trasladoRepository, never())
                .save(any(Traslado.class));

        verifyNoInteractions(trasladoMapper);
    }

    @Test
    void deveLancarExcecaoAoAtualizarStatusDeTrasladoInexistente() {

        TrasladoStatusRequest request = new TrasladoStatusRequest();
        request.setStatus(StatusTraslado.EM_ANDAMENTO);

        when(trasladoRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                TrasladoNaoEncontradoException.class,
                () -> trasladoService.atualizarStatusTraslado(999L, request)
        );

        verify(trasladoRepository).findById(999L);

        verify(historicoStatusTrasladoRepository, never())
                .save(any(HistoricoStatusTraslado.class));

        verify(trasladoRepository, never())
                .save(any(Traslado.class));

        verifyNoInteractions(trasladoMapper);
    }

    @Test
    void deveCorrigirStatusDeConcluidoParaEmAndamentoComSucesso() {

        Traslado traslado = new Traslado();
        traslado.setId(1L);
        traslado.setStatus(StatusTraslado.CONCLUIDO);

        TrasladoCorrecaoStatusRequest request =
                new TrasladoCorrecaoStatusRequest();

        request.setStatus(StatusTraslado.EM_ANDAMENTO);
        request.setMotivo("Traslado concluído por engano");

        TrasladoResponse responseEsperado = new TrasladoResponse();
        responseEsperado.setId(1L);
        responseEsperado.setStatus(StatusTraslado.EM_ANDAMENTO);

        when(trasladoRepository.findById(1L))
                .thenReturn(Optional.of(traslado));

        when(trasladoRepository.save(traslado))
                .thenReturn(traslado);

        when(trasladoMapper.paraResponse(traslado))
                .thenReturn(responseEsperado);

        TrasladoResponse response =
                trasladoService.corrigirStatusTraslado(1L, request);

        assertNotNull(response);
        assertEquals(
                StatusTraslado.EM_ANDAMENTO,
                response.getStatus()
        );

        assertEquals(
                StatusTraslado.EM_ANDAMENTO,
                traslado.getStatus()
        );

        ArgumentCaptor<HistoricoStatusTraslado> captor =
                ArgumentCaptor.forClass(HistoricoStatusTraslado.class);

        verify(historicoStatusTrasladoRepository)
                .save(captor.capture());

        HistoricoStatusTraslado historicoSalvo = captor.getValue();

        assertEquals(
                StatusTraslado.CONCLUIDO,
                historicoSalvo.getStatusAnterior()
        );

        assertEquals(
                StatusTraslado.EM_ANDAMENTO,
                historicoSalvo.getNovoStatus()
        );

        assertEquals(
                "Traslado concluído por engano",
                historicoSalvo.getMotivo()
        );

        assertEquals(
                traslado,
                historicoSalvo.getTraslado()
        );

        assertNotNull(historicoSalvo.getDataHora());

        verify(trasladoRepository).findById(1L);
        verify(trasladoRepository).save(traslado);
        verify(trasladoMapper).paraResponse(traslado);
    }

    @Test
    void deveCorrigirStatusDeCanceladoParaAguardandoComSucesso() {

        Traslado traslado = new Traslado();
        traslado.setId(1L);
        traslado.setStatus(StatusTraslado.CANCELADO);

        TrasladoCorrecaoStatusRequest request =
                new TrasladoCorrecaoStatusRequest();

        request.setStatus(StatusTraslado.AGUARDANDO);
        request.setMotivo("Traslado cancelado por engano");

        TrasladoResponse responseEsperado = new TrasladoResponse();
        responseEsperado.setId(1L);
        responseEsperado.setStatus(StatusTraslado.AGUARDANDO);

        when(trasladoRepository.findById(1L))
                .thenReturn(Optional.of(traslado));

        when(trasladoRepository.save(traslado))
                .thenReturn(traslado);

        when(trasladoMapper.paraResponse(traslado))
                .thenReturn(responseEsperado);

        TrasladoResponse response =
                trasladoService.corrigirStatusTraslado(1L, request);

        assertNotNull(response);
        assertEquals(
                StatusTraslado.AGUARDANDO,
                response.getStatus()
        );

        assertEquals(
                StatusTraslado.AGUARDANDO,
                traslado.getStatus()
        );

        ArgumentCaptor<HistoricoStatusTraslado> captor =
                ArgumentCaptor.forClass(HistoricoStatusTraslado.class);

        verify(historicoStatusTrasladoRepository)
                .save(captor.capture());

        HistoricoStatusTraslado historicoSalvo = captor.getValue();

        assertEquals(
                StatusTraslado.CANCELADO,
                historicoSalvo.getStatusAnterior()
        );

        assertEquals(
                StatusTraslado.AGUARDANDO,
                historicoSalvo.getNovoStatus()
        );

        assertEquals(
                "Traslado cancelado por engano",
                historicoSalvo.getMotivo()
        );

        assertEquals(
                traslado,
                historicoSalvo.getTraslado()
        );

        assertNotNull(historicoSalvo.getDataHora());

        verify(trasladoRepository).findById(1L);
        verify(trasladoRepository).save(traslado);
        verify(trasladoMapper).paraResponse(traslado);
    }

    @Test
    void deveLancarExcecaoAoCorrigirStatusSemMotivo() {

        Traslado traslado = new Traslado();
        traslado.setId(1L);
        traslado.setStatus(StatusTraslado.CONCLUIDO);

        TrasladoCorrecaoStatusRequest request =
                new TrasladoCorrecaoStatusRequest();

        request.setStatus(StatusTraslado.EM_ANDAMENTO);
        request.setMotivo(null);

        when(trasladoRepository.findById(1L))
                .thenReturn(Optional.of(traslado));

        assertThrows(
                MotivoCorrecaoObrigatorioException.class,
                () -> trasladoService.corrigirStatusTraslado(1L, request)
        );

        assertEquals(
                StatusTraslado.CONCLUIDO,
                traslado.getStatus()
        );

        verify(trasladoRepository).findById(1L);

        verify(historicoStatusTrasladoRepository, never())
                .save(any(HistoricoStatusTraslado.class));

        verify(trasladoRepository, never())
                .save(any(Traslado.class));

        verifyNoInteractions(trasladoMapper);
    }

    @Test
    void deveLancarExcecaoAoCorrigirStatusComTransicaoInvalida() {

        Traslado traslado = new Traslado();
        traslado.setId(1L);
        traslado.setStatus(StatusTraslado.CONCLUIDO);

        TrasladoCorrecaoStatusRequest request =
                new TrasladoCorrecaoStatusRequest();

        request.setStatus(StatusTraslado.AGUARDANDO);
        request.setMotivo("Tentativa de correção");

        when(trasladoRepository.findById(1L))
                .thenReturn(Optional.of(traslado));

        assertThrows(
                TransicaoStatusTrasladoInvalidaException.class,
                () -> trasladoService.corrigirStatusTraslado(1L, request)
        );

        assertEquals(
                StatusTraslado.CONCLUIDO,
                traslado.getStatus()
        );

        verify(trasladoRepository).findById(1L);

        verify(historicoStatusTrasladoRepository, never())
                .save(any(HistoricoStatusTraslado.class));

        verify(trasladoRepository, never())
                .save(any(Traslado.class));

        verifyNoInteractions(trasladoMapper);
    }

    @Test
    void deveLancarExcecaoAoCorrigirStatusDeTrasladoInexistente() {

        TrasladoCorrecaoStatusRequest request =
                new TrasladoCorrecaoStatusRequest();

        request.setStatus(StatusTraslado.EM_ANDAMENTO);
        request.setMotivo("Correção de status");

        when(trasladoRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                TrasladoNaoEncontradoException.class,
                () -> trasladoService.corrigirStatusTraslado(999L, request)
        );

        verify(trasladoRepository).findById(999L);

        verify(historicoStatusTrasladoRepository, never())
                .save(any(HistoricoStatusTraslado.class));

        verify(trasladoRepository, never())
                .save(any(Traslado.class));

        verifyNoInteractions(trasladoMapper);
    }

    @Test
    void deveListarHistoricoStatusTrasladoComSucesso() {

        Traslado traslado = new Traslado();
        traslado.setId(1L);

        HistoricoStatusTraslado historico1 =
                new HistoricoStatusTraslado();
        historico1.setId(1L);
        historico1.setTraslado(traslado);
        historico1.setStatusAnterior(StatusTraslado.AGUARDANDO);
        historico1.setNovoStatus(StatusTraslado.EM_ANDAMENTO);
        historico1.setMotivo("Alteração normal de status");

        HistoricoStatusTraslado historico2 =
                new HistoricoStatusTraslado();
        historico2.setId(2L);
        historico2.setTraslado(traslado);
        historico2.setStatusAnterior(StatusTraslado.EM_ANDAMENTO);
        historico2.setNovoStatus(StatusTraslado.CONCLUIDO);
        historico2.setMotivo("Alteração normal de status");

        HistoricoStatusTrasladoResponse response1 =
                new HistoricoStatusTrasladoResponse();
        response1.setId(1L);

        HistoricoStatusTrasladoResponse response2 =
                new HistoricoStatusTrasladoResponse();
        response2.setId(2L);

        when(trasladoRepository.findById(1L))
                .thenReturn(Optional.of(traslado));

        when(historicoStatusTrasladoRepository.findByTrasladoId(1L))
                .thenReturn(List.of(historico1, historico2));

        when(historicoStatusTrasladoMapper.paraResponse(historico1))
                .thenReturn(response1);

        when(historicoStatusTrasladoMapper.paraResponse(historico2))
                .thenReturn(response2);

        List<HistoricoStatusTrasladoResponse> resultado =
                trasladoService.listarHistoricoStatusTraslado(1L);

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(1L, resultado.get(0).getId());
        assertEquals(2L, resultado.get(1).getId());

        verify(trasladoRepository).findById(1L);

        verify(historicoStatusTrasladoRepository)
                .findByTrasladoId(1L);

        verify(historicoStatusTrasladoMapper)
                .paraResponse(historico1);

        verify(historicoStatusTrasladoMapper)
                .paraResponse(historico2);
    }

    @Test
    void deveLancarExcecaoAoListarHistoricoDeTrasladoInexistente() {

        when(trasladoRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                TrasladoNaoEncontradoException.class,
                () -> trasladoService.listarHistoricoStatusTraslado(999L)
        );

        verify(trasladoRepository).findById(999L);

        verify(historicoStatusTrasladoRepository, never())
                .findByTrasladoId(anyLong());

        verifyNoInteractions(historicoStatusTrasladoMapper);
    }

    @Test
    void deveAssociarMotoristaEVeiculoAoTrasladoComSucesso() {

        Traslado traslado = new Traslado();
        traslado.setId(1L);

        Motorista motorista = new Motorista();
        motorista.setId(10L);
        motorista.setNomeCompleto("João da Silva");
        motorista.setAtivo(true);

        Veiculo veiculo = new Veiculo();
        veiculo.setId(20L);
        veiculo.setModelo("Renault Duster");
        veiculo.setPlaca("ABC1D23");
        veiculo.setCapacidadePassageiros(4);
        veiculo.setAtivo(true);

        TrasladoOperacaoRequest request =
                new TrasladoOperacaoRequest();

        request.setMotoristaId(10L);
        request.setVeiculoId(20L);

        TrasladoResponse responseEsperado =
                new TrasladoResponse();

        responseEsperado.setId(1L);
        responseEsperado.setMotoristaId(10L);
        responseEsperado.setMotoristaNome(
                "João da Silva"
        );
        responseEsperado.setVeiculoId(20L);
        responseEsperado.setVeiculoModelo(
                "Renault Duster"
        );
        responseEsperado.setVeiculoPlaca(
                "ABC1D23"
        );

        when(trasladoRepository.findById(1L))
                .thenReturn(Optional.of(traslado));

        when(motoristaRepository.findById(10L))
                .thenReturn(Optional.of(motorista));

        when(veiculoRepository.findById(20L))
                .thenReturn(Optional.of(veiculo));

        when(trasladoRepository.save(traslado))
                .thenReturn(traslado);

        when(trasladoMapper.paraResponse(traslado))
                .thenReturn(responseEsperado);

        TrasladoResponse response =
                trasladoService.associarOperacao(
                        1L,
                        request
                );

        assertNotNull(response);

        assertEquals(
                10L,
                response.getMotoristaId()
        );

        assertEquals(
                20L,
                response.getVeiculoId()
        );

        assertEquals(
                motorista,
                traslado.getMotorista()
        );

        assertEquals(
                veiculo,
                traslado.getVeiculo()
        );

        verify(trasladoRepository)
                .findById(1L);

        verify(motoristaRepository)
                .findById(10L);

        verify(veiculoRepository)
                .findById(20L);

        verify(trasladoRepository)
                .save(traslado);

        verify(trasladoMapper)
                .paraResponse(traslado);
    }

    @Test
    void deveLancarExcecaoAoAssociarOperacaoEmTrasladoInexistente() {

        TrasladoOperacaoRequest request =
                new TrasladoOperacaoRequest();

        request.setMotoristaId(10L);
        request.setVeiculoId(20L);

        when(trasladoRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                TrasladoNaoEncontradoException.class,
                () -> trasladoService.associarOperacao(
                        999L,
                        request
                )
        );

        verify(trasladoRepository)
                .findById(999L);

        verifyNoInteractions(
                motoristaRepository,
                veiculoRepository,
                trasladoMapper
        );

        verify(trasladoRepository, never())
                .save(any(Traslado.class));
    }

    @Test
    void deveLancarExcecaoQuandoMotoristaNaoExistirAoAssociarOperacao() {

        Traslado traslado = new Traslado();
        traslado.setId(1L);

        TrasladoOperacaoRequest request =
                new TrasladoOperacaoRequest();

        request.setMotoristaId(999L);
        request.setVeiculoId(20L);

        when(trasladoRepository.findById(1L))
                .thenReturn(Optional.of(traslado));

        when(motoristaRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                MotoristaNaoEncontradoException.class,
                () -> trasladoService.associarOperacao(
                        1L,
                        request
                )
        );

        verify(trasladoRepository)
                .findById(1L);

        verify(motoristaRepository)
                .findById(999L);

        verifyNoInteractions(
                veiculoRepository,
                trasladoMapper
        );

        verify(trasladoRepository, never())
                .save(any(Traslado.class));
    }

    @Test
    void deveLancarExcecaoQuandoVeiculoNaoExistirAoAssociarOperacao() {

        Traslado traslado = new Traslado();
        traslado.setId(1L);

        Motorista motorista = new Motorista();
        motorista.setId(10L);
        motorista.setAtivo(true);

        TrasladoOperacaoRequest request =
                new TrasladoOperacaoRequest();

        request.setMotoristaId(10L);
        request.setVeiculoId(999L);

        when(trasladoRepository.findById(1L))
                .thenReturn(Optional.of(traslado));

        when(motoristaRepository.findById(10L))
                .thenReturn(Optional.of(motorista));

        when(veiculoRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                VeiculoNaoEncontradoException.class,
                () -> trasladoService.associarOperacao(
                        1L,
                        request
                )
        );

        verify(trasladoRepository)
                .findById(1L);

        verify(motoristaRepository)
                .findById(10L);

        verify(veiculoRepository)
                .findById(999L);

        verify(trasladoRepository, never())
                .save(any(Traslado.class));

        verifyNoInteractions(trasladoMapper);
    }

    @Test
    void deveLancarExcecaoQuandoMotoristaEstiverInativoAoAssociarOperacao() {

        Traslado traslado = new Traslado();
        traslado.setId(1L);

        Motorista motorista = new Motorista();
        motorista.setId(10L);
        motorista.setAtivo(false);

        Veiculo veiculo = new Veiculo();
        veiculo.setId(20L);
        veiculo.setAtivo(true);

        TrasladoOperacaoRequest request =
                new TrasladoOperacaoRequest();

        request.setMotoristaId(10L);
        request.setVeiculoId(20L);

        when(trasladoRepository.findById(1L))
                .thenReturn(Optional.of(traslado));

        when(motoristaRepository.findById(10L))
                .thenReturn(Optional.of(motorista));

        when(veiculoRepository.findById(20L))
                .thenReturn(Optional.of(veiculo));

        assertThrows(
                MotoristaInativoException.class,
                () -> trasladoService.associarOperacao(
                        1L,
                        request
                )
        );

        assertNull(traslado.getMotorista());
        assertNull(traslado.getVeiculo());

        verify(trasladoRepository, never())
                .save(any(Traslado.class));

        verifyNoInteractions(trasladoMapper);
    }

    @Test
    void deveLancarExcecaoQuandoVeiculoEstiverInativoAoAssociarOperacao() {

        Traslado traslado = new Traslado();
        traslado.setId(1L);

        Motorista motorista = new Motorista();
        motorista.setId(10L);
        motorista.setAtivo(true);

        Veiculo veiculo = new Veiculo();
        veiculo.setId(20L);
        veiculo.setAtivo(false);

        TrasladoOperacaoRequest request =
                new TrasladoOperacaoRequest();

        request.setMotoristaId(10L);
        request.setVeiculoId(20L);

        when(trasladoRepository.findById(1L))
                .thenReturn(Optional.of(traslado));

        when(motoristaRepository.findById(10L))
                .thenReturn(Optional.of(motorista));

        when(veiculoRepository.findById(20L))
                .thenReturn(Optional.of(veiculo));

        assertThrows(
                VeiculoInativoException.class,
                () -> trasladoService.associarOperacao(
                        1L,
                        request
                )
        );

        assertNull(traslado.getMotorista());
        assertNull(traslado.getVeiculo());

        verify(trasladoRepository, never())
                .save(any(Traslado.class));

        verifyNoInteractions(trasladoMapper);
    }



}