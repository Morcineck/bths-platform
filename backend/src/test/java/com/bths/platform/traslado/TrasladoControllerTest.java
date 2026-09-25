package com.bths.platform.traslado;

import com.bths.platform.traslado.dto.*;
import com.bths.platform.traslado.enums.StatusTraslado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TrasladoControllerTest {

    @Mock
    private TrasladoService trasladoService;

    private TrasladoController trasladoController;

    @BeforeEach
    void setUp() {
        trasladoController = new TrasladoController(trasladoService);
    }

    @Test
    void deveCadastrarTrasladoComSucesso() {

        TrasladoRequest request = new TrasladoRequest();

        TrasladoResponse responseEsperado = new TrasladoResponse();
        responseEsperado.setId(1L);
        responseEsperado.setStatus(StatusTraslado.AGUARDANDO);

        when(trasladoService.cadastrarTraslado(request))
                .thenReturn(responseEsperado);

        ResponseEntity<TrasladoResponse> response =
                trasladoController.cadastrarTraslado(request);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(responseEsperado, response.getBody());

        assertNotNull(response.getHeaders().getLocation());

        assertEquals(
                "/api/traslados/1",
                response.getHeaders().getLocation().toString()
        );

        verify(trasladoService).cadastrarTraslado(request);
    }

    @Test
    void deveBuscarTrasladoComSucesso() {

        TrasladoResponse responseEsperado = new TrasladoResponse();
        responseEsperado.setId(1L);
        responseEsperado.setStatus(StatusTraslado.AGUARDANDO);

        when(trasladoService.buscarTrasladoPorId(1L))
                .thenReturn(responseEsperado);

        ResponseEntity<TrasladoResponse> response =
                trasladoController.buscarTraslado(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseEsperado, response.getBody());

        verify(trasladoService).buscarTrasladoPorId(1L);
    }

    @Test
    void deveListarTrasladosPorViagemComSucesso() {

        TrasladoResponse traslado1 = new TrasladoResponse();
        traslado1.setId(1L);
        traslado1.setStatus(StatusTraslado.AGUARDANDO);

        TrasladoResponse traslado2 = new TrasladoResponse();
        traslado2.setId(2L);
        traslado2.setStatus(StatusTraslado.EM_ANDAMENTO);

        List<TrasladoResponse> listaEsperada =
                List.of(traslado1, traslado2);

        when(trasladoService.listarTrasladosPorViagem(1L))
                .thenReturn(listaEsperada);

        ResponseEntity<List<TrasladoResponse>> response =
                trasladoController.listarTrasladoPorViagem(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(listaEsperada, response.getBody());

        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals(1L, response.getBody().get(0).getId());
        assertEquals(2L, response.getBody().get(1).getId());

        verify(trasladoService).listarTrasladosPorViagem(1L);
    }

    @Test
    void deveListarTrasladosPorHospedeComSucesso() {

        TrasladoResponse traslado1 = new TrasladoResponse();
        traslado1.setId(1L);
        traslado1.setStatus(StatusTraslado.AGUARDANDO);

        TrasladoResponse traslado2 = new TrasladoResponse();
        traslado2.setId(2L);
        traslado2.setStatus(StatusTraslado.CONCLUIDO);

        List<TrasladoResponse> listaEsperada =
                List.of(traslado1, traslado2);

        when(trasladoService.listarTrasladosPorHospede(1L))
                .thenReturn(listaEsperada);

        ResponseEntity<List<TrasladoResponse>> response =
                trasladoController.listarTrasladoPorHospede(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(listaEsperada, response.getBody());

        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals(1L, response.getBody().get(0).getId());
        assertEquals(2L, response.getBody().get(1).getId());

        verify(trasladoService).listarTrasladosPorHospede(1L);
    }

    @Test
    void deveAtualizarTrasladoComSucesso() {

        TrasladoUpdateRequest request =
                new TrasladoUpdateRequest();

        TrasladoResponse responseEsperado =
                new TrasladoResponse();

        responseEsperado.setId(1L);
        responseEsperado.setStatus(StatusTraslado.AGUARDANDO);

        when(trasladoService.atualizarTraslado(1L, request))
                .thenReturn(responseEsperado);

        ResponseEntity<TrasladoResponse> response =
                trasladoController.atualizarTraslado(1L, request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseEsperado, response.getBody());

        verify(trasladoService)
                .atualizarTraslado(1L, request);
    }

    @Test
    void deveAtualizarStatusTrasladoComSucesso() {

        TrasladoStatusRequest request =
                new TrasladoStatusRequest();

        request.setStatus(StatusTraslado.EM_ANDAMENTO);

        TrasladoResponse responseEsperado =
                new TrasladoResponse();

        responseEsperado.setId(1L);
        responseEsperado.setStatus(StatusTraslado.EM_ANDAMENTO);

        when(trasladoService.atualizarStatusTraslado(1L, request))
                .thenReturn(responseEsperado);

        ResponseEntity<TrasladoResponse> response =
                trasladoController.atualizarStatusTraslado(1L, request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseEsperado, response.getBody());

        assertNotNull(response.getBody());
        assertEquals(
                StatusTraslado.EM_ANDAMENTO,
                response.getBody().getStatus()
        );

        verify(trasladoService)
                .atualizarStatusTraslado(1L, request);
    }

    @Test
    void deveListarHistoricoStatusTrasladoComSucesso() {

        HistoricoStatusTrasladoResponse historico1 =
                new HistoricoStatusTrasladoResponse();
        historico1.setId(1L);

        HistoricoStatusTrasladoResponse historico2 =
                new HistoricoStatusTrasladoResponse();
        historico2.setId(2L);

        List<HistoricoStatusTrasladoResponse> listaEsperada =
                List.of(historico1, historico2);

        when(trasladoService.listarHistoricoStatusTraslado(1L))
                .thenReturn(listaEsperada);

        ResponseEntity<List<HistoricoStatusTrasladoResponse>> response =
                trasladoController.listarHistoricoStatusTraslado(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(listaEsperada, response.getBody());

        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals(1L, response.getBody().get(0).getId());
        assertEquals(2L, response.getBody().get(1).getId());

        verify(trasladoService)
                .listarHistoricoStatusTraslado(1L);
    }

    @Test
    void deveAssociarOperacaoAoTrasladoComSucesso() {

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
        responseEsperado.setVeiculoCapacidadePassageiros(4);

        when(
                trasladoService.associarOperacao(
                        1L,
                        request
                )
        ).thenReturn(responseEsperado);

        ResponseEntity<TrasladoResponse> response =
                trasladoController.associarOperacao(
                        1L,
                        request
                );

        assertNotNull(response);
        assertEquals(
                HttpStatus.OK,
                response.getStatusCode()
        );

        assertEquals(
                responseEsperado,
                response.getBody()
        );

        assertNotNull(response.getBody());

        assertEquals(
                10L,
                response.getBody().getMotoristaId()
        );

        assertEquals(
                "João da Silva",
                response.getBody().getMotoristaNome()
        );

        assertEquals(
                20L,
                response.getBody().getVeiculoId()
        );

        assertEquals(
                "Renault Duster",
                response.getBody().getVeiculoModelo()
        );

        assertEquals(
                "ABC1D23",
                response.getBody().getVeiculoPlaca()
        );

        assertEquals(
                4,
                response.getBody()
                        .getVeiculoCapacidadePassageiros()
        );

        verify(trasladoService)
                .associarOperacao(
                        1L,
                        request
                );
    }
}