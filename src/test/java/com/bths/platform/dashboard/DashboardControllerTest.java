package com.bths.platform.dashboard;

import com.bths.platform.dashboard.dto.DashboardHospedagemResponse;
import com.bths.platform.dashboard.dto.DashboardHospedesResponse;
import com.bths.platform.dashboard.dto.DashboardResponse;
import com.bths.platform.dashboard.dto.DashboardTrasladosResponse;
import com.bths.platform.exception.ViagemNaoEncontradaException;
import com.bths.platform.security.JwtService;
import com.bths.platform.security.UsuarioDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DashboardController.class)
@AutoConfigureMockMvc(addFilters = false)
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DashboardService dashboardService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UsuarioDetailsService usuarioDetailsService;

    @Test
    void deveBuscarDashboardERetornar200() throws Exception {

        Long viagemId = 1L;

        DashboardHospedesResponse hospedes =
                new DashboardHospedesResponse();

        hospedes.setTotal(4L);
        hospedes.setPresentes(2L);
        hospedes.setPendentes(2L);
        hospedes.setTaxaCheckIn(50.0);

        DashboardHospedagemResponse hospedagem =
                new DashboardHospedagemResponse();

        hospedagem.setVagasTotais(7L);
        hospedagem.setOcupadas(2L);
        hospedagem.setDisponiveis(5L);

        DashboardTrasladosResponse traslados =
                new DashboardTrasladosResponse();

        traslados.setAguardando(6L);
        traslados.setEmAndamento(1L);
        traslados.setConcluidos(1L);
        traslados.setProximos(List.of());

        DashboardResponse response =
                new DashboardResponse();

        response.setViagemId(viagemId);
        response.setHospedes(hospedes);
        response.setHospedagem(hospedagem);
        response.setTraslados(traslados);

        when(dashboardService.buscarDashboard(viagemId))
                .thenReturn(response);

        mockMvc.perform(
                        get("/api/dashboard/viagens/{viagemId}", viagemId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.viagemId").value(1))

                .andExpect(jsonPath("$.hospedes.total").value(4))
                .andExpect(jsonPath("$.hospedes.presentes").value(2))
                .andExpect(jsonPath("$.hospedes.pendentes").value(2))
                .andExpect(jsonPath("$.hospedes.taxaCheckIn").value(50.0))

                .andExpect(jsonPath("$.hospedagem.vagasTotais").value(7))
                .andExpect(jsonPath("$.hospedagem.ocupadas").value(2))
                .andExpect(jsonPath("$.hospedagem.disponiveis").value(5))

                .andExpect(jsonPath("$.traslados.aguardando").value(6))
                .andExpect(jsonPath("$.traslados.emAndamento").value(1))
                .andExpect(jsonPath("$.traslados.concluidos").value(1))
                .andExpect(jsonPath("$.traslados.proximos").isArray())
                .andExpect(jsonPath("$.traslados.proximos").isEmpty());

        verify(dashboardService).buscarDashboard(viagemId);
    }

    @Test
    void deveRetornar404QuandoViagemNaoForEncontrada() throws Exception {

        Long viagemId = 999L;

        when(dashboardService.buscarDashboard(viagemId))
                .thenThrow(
                        new ViagemNaoEncontradaException(
                                "Viagem não encontrada!"
                        )
                );

        mockMvc.perform(
                        get(
                                "/api/dashboard/viagens/{viagemId}",
                                viagemId
                        )
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro")
                        .value("Not Found"))
                .andExpect(jsonPath("$.mensagem")
                        .value("Viagem não encontrada!"))
                .andExpect(jsonPath("$.status")
                        .value(404));

        verify(dashboardService)
                .buscarDashboard(viagemId);
    }
}