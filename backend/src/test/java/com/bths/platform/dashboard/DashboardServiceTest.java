package com.bths.platform.dashboard;

import com.bths.platform.alocacao.AlocacaoQuartoRepository;
import com.bths.platform.dashboard.dto.DashboardResponse;
import com.bths.platform.viagem.exception.ViagemNaoEncontradaException;
import com.bths.platform.hospede.Hospede;
import com.bths.platform.hospede.HospedeRepository;
import com.bths.platform.hospede.enums.StatusCheckIn;
import com.bths.platform.quarto.QuartoRepository;
import com.bths.platform.quarto.enums.StatusQuarto;
import com.bths.platform.traslado.Traslado;
import com.bths.platform.traslado.TrasladoRepository;
import com.bths.platform.traslado.enums.StatusTraslado;
import com.bths.platform.traslado.enums.TipoTraslado;
import com.bths.platform.viagem.ViagemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private ViagemRepository viagemRepository;

    @Mock
    private HospedeRepository hospedeRepository;

    @Mock
    private QuartoRepository quartoRepository;

    @Mock
    private AlocacaoQuartoRepository alocacaoQuartoRepository;

    @Mock
    private TrasladoRepository trasladoRepository;

    private DashboardService dashboardService;

    @BeforeEach
    void setUp() {

        dashboardService = new DashboardService(
                viagemRepository,
                hospedeRepository,
                quartoRepository,
                alocacaoQuartoRepository,
                trasladoRepository
        );
    }

    @Test
    void deveBuscarDashboardComSucesso() {

        Long viagemId = 1L;

        when(viagemRepository.existsById(viagemId))
                .thenReturn(true);

        when(hospedeRepository.countByViagemId(viagemId))
                .thenReturn(4L);

        when(hospedeRepository.contarPorViagemEStatusCheckIn(
                viagemId,
                StatusCheckIn.REALIZADO
        )).thenReturn(2L);

        when(hospedeRepository.contarPorViagemEStatusCheckIn(
                viagemId,
                StatusCheckIn.PENDENTE
        )).thenReturn(2L);

        when(quartoRepository.somarCapacidadePorViagemExcluindoStatus(
                viagemId,
                StatusQuarto.INDISPONIVEL
        )).thenReturn(7L);

        when(alocacaoQuartoRepository.countByViagemId(viagemId))
                .thenReturn(2L);

        when(trasladoRepository.countByViagemIdAndStatus(
                viagemId,
                StatusTraslado.AGUARDANDO
        )).thenReturn(6L);

        when(trasladoRepository.countByViagemIdAndStatus(
                viagemId,
                StatusTraslado.EM_ANDAMENTO
        )).thenReturn(1L);

        when(trasladoRepository.countByViagemIdAndStatus(
                viagemId,
                StatusTraslado.CONCLUIDO
        )).thenReturn(1L);

        when(trasladoRepository.buscarProximosTraslados(
                eq(viagemId),
                eq(StatusTraslado.AGUARDANDO),
                any(),
                any()
        )).thenReturn(List.of());

        DashboardResponse response =
                dashboardService.buscarDashboard(viagemId);

        assertNotNull(response);
        assertEquals(viagemId, response.getViagemId());

        assertNotNull(response.getHospedes());
        assertEquals(4L, response.getHospedes().getTotal());
        assertEquals(2L, response.getHospedes().getPresentes());
        assertEquals(2L, response.getHospedes().getPendentes());
        assertEquals(50.0, response.getHospedes().getTaxaCheckIn());

        assertNotNull(response.getHospedagem());
        assertEquals(7L, response.getHospedagem().getVagasTotais());
        assertEquals(2L, response.getHospedagem().getOcupadas());
        assertEquals(5L, response.getHospedagem().getDisponiveis());

        assertNotNull(response.getTraslados());
        assertEquals(6L, response.getTraslados().getAguardando());
        assertEquals(1L, response.getTraslados().getEmAndamento());
        assertEquals(1L, response.getTraslados().getConcluidos());
        assertTrue(response.getTraslados().getProximos().isEmpty());

        verify(viagemRepository).existsById(viagemId);
    }

    @Test
    void deveLancarExcecaoQuandoViagemNaoExistir() {

        Long viagemId = 999L;

        when(viagemRepository.existsById(viagemId))
                .thenReturn(false);

        assertThrows(
                ViagemNaoEncontradaException.class,
                () -> dashboardService.buscarDashboard(viagemId)
        );

        verify(viagemRepository).existsById(viagemId);

        verifyNoInteractions(
                hospedeRepository,
                quartoRepository,
                alocacaoQuartoRepository,
                trasladoRepository
        );
    }

    @Test
    void deveRetornarTaxaCheckInZeroQuandoNaoHouverHospedes() {

        Long viagemId = 1L;

        when(viagemRepository.existsById(viagemId))
                .thenReturn(true);

        when(hospedeRepository.countByViagemId(viagemId))
                .thenReturn(0L);

        when(hospedeRepository.contarPorViagemEStatusCheckIn(
                viagemId,
                StatusCheckIn.REALIZADO
        )).thenReturn(0L);

        when(hospedeRepository.contarPorViagemEStatusCheckIn(
                viagemId,
                StatusCheckIn.PENDENTE
        )).thenReturn(0L);

        when(quartoRepository.somarCapacidadePorViagemExcluindoStatus(
                viagemId,
                StatusQuarto.INDISPONIVEL
        )).thenReturn(0L);

        when(alocacaoQuartoRepository.countByViagemId(viagemId))
                .thenReturn(0L);

        when(trasladoRepository.buscarProximosTraslados(
                eq(viagemId),
                eq(StatusTraslado.AGUARDANDO),
                any(),
                any()
        )).thenReturn(List.of());

        DashboardResponse response =
                dashboardService.buscarDashboard(viagemId);

        assertNotNull(response);
        assertNotNull(response.getHospedes());

        assertEquals(0L, response.getHospedes().getTotal());
        assertEquals(0L, response.getHospedes().getPresentes());
        assertEquals(0L, response.getHospedes().getPendentes());
        assertEquals(0.0, response.getHospedes().getTaxaCheckIn());
    }

    @Test
    void deveMapearProximosTrasladosComSucesso() {

        Long viagemId = 1L;

        Hospede hospede = new Hospede();
        hospede.setNomeCompleto("João da Silva");

        LocalDateTime dataHoraPrevista =
                LocalDateTime.of(2027, 4, 29, 10, 0);

        Traslado traslado = new Traslado();
        traslado.setId(10L);
        traslado.setHospede(hospede);
        traslado.setTipo(TipoTraslado.AEROPORTO_PARA_HOSPEDAGEM);
        traslado.setDataHoraPrevista(dataHoraPrevista);
        traslado.setLocalOrigem("Aeroporto de Guarulhos");
        traslado.setLocalDestino("Hospedagem Beat Trips");

        when(viagemRepository.existsById(viagemId))
                .thenReturn(true);

        when(trasladoRepository.buscarProximosTraslados(
                eq(viagemId),
                eq(StatusTraslado.AGUARDANDO),
                any(),
                any()
        )).thenReturn(List.of(traslado));

        DashboardResponse response =
                dashboardService.buscarDashboard(viagemId);

        assertNotNull(response);
        assertNotNull(response.getTraslados());
        assertEquals(1, response.getTraslados().getProximos().size());

        var proximo =
                response.getTraslados().getProximos().get(0);

        assertEquals(10L, proximo.getId());
        assertEquals("João da Silva", proximo.getHospedeNome());
        assertEquals(
                TipoTraslado.AEROPORTO_PARA_HOSPEDAGEM,
                proximo.getTipo()
        );
        assertEquals(
                dataHoraPrevista,
                proximo.getDataHoraPrevista()
        );
        assertEquals(
                "Aeroporto de Guarulhos",
                proximo.getLocalOrigem()
        );
        assertEquals(
                "Hospedagem Beat Trips",
                proximo.getLocalDestino()
        );
    }

    @Test
    void deveRetornarZeroVagasDisponiveisQuandoOcupacaoSuperarCapacidade() {

        Long viagemId = 1L;

        when(viagemRepository.existsById(viagemId))
                .thenReturn(true);

        when(quartoRepository.somarCapacidadePorViagemExcluindoStatus(
                viagemId,
                StatusQuarto.INDISPONIVEL
        )).thenReturn(5L);

        when(alocacaoQuartoRepository.countByViagemId(viagemId))
                .thenReturn(7L);

        when(trasladoRepository.buscarProximosTraslados(
                eq(viagemId),
                eq(StatusTraslado.AGUARDANDO),
                any(),
                any()
        )).thenReturn(List.of());

        DashboardResponse response =
                dashboardService.buscarDashboard(viagemId);

        assertNotNull(response);
        assertNotNull(response.getHospedagem());

        assertEquals(5L, response.getHospedagem().getVagasTotais());
        assertEquals(7L, response.getHospedagem().getOcupadas());
        assertEquals(0L, response.getHospedagem().getDisponiveis());
    }

    @Test
    void deveBuscarNoMaximoCincoProximosTrasladosAguardando() {

        Long viagemId = 1L;

        when(viagemRepository.existsById(viagemId))
                .thenReturn(true);

        when(trasladoRepository.buscarProximosTraslados(
                eq(viagemId),
                eq(StatusTraslado.AGUARDANDO),
                any(LocalDateTime.class),
                any(Pageable.class)
        )).thenReturn(List.of());

        dashboardService.buscarDashboard(viagemId);

        ArgumentCaptor<LocalDateTime> dataCaptor =
                ArgumentCaptor.forClass(LocalDateTime.class);

        ArgumentCaptor<Pageable> pageableCaptor =
                ArgumentCaptor.forClass(Pageable.class);

        verify(trasladoRepository).buscarProximosTraslados(
                eq(viagemId),
                eq(StatusTraslado.AGUARDANDO),
                dataCaptor.capture(),
                pageableCaptor.capture()
        );

        Pageable pageable = pageableCaptor.getValue();

        assertNotNull(dataCaptor.getValue());
        assertEquals(0, pageable.getPageNumber());
        assertEquals(5, pageable.getPageSize());
    }
}