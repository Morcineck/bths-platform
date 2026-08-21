package com.bths.platform.alocacao;

import com.bths.platform.alocacao.dto.AlocacaoQuartoRequest;
import com.bths.platform.alocacao.dto.AlocacaoQuartoResponse;
import com.bths.platform.alocacao.dto.OcupacaoQuartoResponse;
import com.bths.platform.alocacao.mapper.AlocacaoQuartoMapper;
import com.bths.platform.exception.*;
import com.bths.platform.hospede.Hospede;
import com.bths.platform.hospede.HospedeRepository;
import com.bths.platform.quarto.Quarto;
import com.bths.platform.quarto.QuartoRepository;
import com.bths.platform.quarto.StatusQuarto;
import com.bths.platform.viagem.Viagem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlocacaoQuartoServiceTest {

    @Mock
    private AlocacaoQuartoRepository alocacaoRepository;

    @Mock
    private HospedeRepository hospedeRepository;

    @Mock
    private QuartoRepository quartoRepository;

    @Mock
    private AlocacaoQuartoMapper alocacaoMapper;

    private AlocacaoService alocacaoService;

    @BeforeEach
    void setUp() {

        alocacaoService = new AlocacaoService(
                alocacaoRepository,
                hospedeRepository,
                quartoRepository,
                alocacaoMapper
        );
    }

    @Test
    void deveAlocarHospedeComSucesso() {

        Long hospedeId = 1L;
        Long quartoId = 2L;
        Long viagemId = 1L;

        AlocacaoQuartoRequest request = new AlocacaoQuartoRequest();
        request.setHospedeId(hospedeId);
        request.setQuartoId(quartoId);

        Viagem viagem = new Viagem();
        viagem.setId(viagemId);
        viagem.setNome("Tomorrowland Brasil 2027");

        Hospede hospede = new Hospede();
        hospede.setId(hospedeId);
        hospede.setNomeCompleto("João da Silva");
        hospede.setViagem(viagem);

        Quarto quarto = new Quarto();
        quarto.setId(quartoId);
        quarto.setNome("Suíte 01");
        quarto.setCapacidade(6);
        quarto.setStatus(StatusQuarto.DISPONIVEL);
        quarto.setViagem(viagem);

        AlocacaoQuarto alocacaoSalva = new AlocacaoQuarto();
        alocacaoSalva.setId(1L);
        alocacaoSalva.setHospede(hospede);
        alocacaoSalva.setQuarto(quarto);
        alocacaoSalva.setViagem(viagem);

        AlocacaoQuartoResponse responseEsperado =
                new AlocacaoQuartoResponse();

        responseEsperado.setId(1L);
        responseEsperado.setHospedeId(hospedeId);
        responseEsperado.setHospedeNome("João da Silva");
        responseEsperado.setQuartoId(quartoId);
        responseEsperado.setQuartoNome("Suíte 01");
        responseEsperado.setViagemId(viagemId);
        responseEsperado.setViagemNome("Tomorrowland Brasil 2027");

        when(hospedeRepository.findById(hospedeId))
                .thenReturn(Optional.of(hospede));

        when(quartoRepository.findById(quartoId))
                .thenReturn(Optional.of(quarto));

        when(alocacaoRepository.existsByHospedeIdAndViagemId(
                hospedeId,
                viagemId
        )).thenReturn(false);

        when(alocacaoRepository.countByQuartoId(quartoId))
                .thenReturn(0L);

        when(alocacaoRepository.save(
                org.mockito.ArgumentMatchers.any(AlocacaoQuarto.class)
        )).thenReturn(alocacaoSalva);

        when(alocacaoMapper.paraResponse(alocacaoSalva))
                .thenReturn(responseEsperado);

        AlocacaoQuartoResponse resultado =
                alocacaoService.alocarHospede(request);

        assertEquals(1L, resultado.getId());
        assertEquals(1L, resultado.getHospedeId());
        assertEquals("João da Silva", resultado.getHospedeNome());
        assertEquals(2L, resultado.getQuartoId());
        assertEquals("Suíte 01", resultado.getQuartoNome());
        assertEquals(1L, resultado.getViagemId());

        verify(hospedeRepository).findById(hospedeId);
        verify(quartoRepository).findById(quartoId);

        verify(alocacaoRepository)
                .existsByHospedeIdAndViagemId(
                        hospedeId,
                        viagemId
                );

        verify(alocacaoRepository)
                .countByQuartoId(quartoId);

        verify(alocacaoRepository)
                .save(org.mockito.ArgumentMatchers.any(AlocacaoQuarto.class));

        verify(alocacaoMapper)
                .paraResponse(alocacaoSalva);
    }

    @Test
    void deveLancarExcecaoQuandoHospedeNaoExistir() {

        Long hospedeId = 999L;
        Long quartoId = 2L;

        AlocacaoQuartoRequest request = new AlocacaoQuartoRequest();
        request.setHospedeId(hospedeId);
        request.setQuartoId(quartoId);

        when(hospedeRepository.findById(hospedeId))
                .thenReturn(Optional.empty());

        HospedeNaoEncontradoException exception = assertThrows(
                HospedeNaoEncontradoException.class,
                () -> alocacaoService.alocarHospede(request)
        );

        assertEquals(
                "Hóspede não encontrado!",
                exception.getMessage()
        );

        verify(hospedeRepository).findById(hospedeId);

        verify(quartoRepository, never())
                .findById(anyLong());

        verify(alocacaoRepository, never())
                .save(any(AlocacaoQuarto.class));
    }

    @Test
    void deveLancarExcecaoQuandoQuartoNaoExistir() {

        Long hospedeId = 1L;
        Long quartoId = 999L;

        AlocacaoQuartoRequest request = new AlocacaoQuartoRequest();
        request.setHospedeId(hospedeId);
        request.setQuartoId(quartoId);

        Hospede hospede = new Hospede();
        hospede.setId(hospedeId);
        hospede.setNomeCompleto("João da Silva");

        when(hospedeRepository.findById(hospedeId))
                .thenReturn(Optional.of(hospede));

        when(quartoRepository.findById(quartoId))
                .thenReturn(Optional.empty());

        QuartoNaoEncontradoException exception = assertThrows(
                QuartoNaoEncontradoException.class,
                () -> alocacaoService.alocarHospede(request)
        );

        assertEquals(
                "Quarto não encontrado!",
                exception.getMessage()
        );

        verify(hospedeRepository).findById(hospedeId);
        verify(quartoRepository).findById(quartoId);

        verify(alocacaoRepository, never())
                .existsByHospedeIdAndViagemId(
                        anyLong(),
                        anyLong()
                );

        verify(alocacaoRepository, never())
                .countByQuartoId(anyLong());

        verify(alocacaoRepository, never())
                .save(any(AlocacaoQuarto.class));
    }

    @Test
    void deveLancarExcecaoQuandoHospedeEQuartoForemDeViagensDiferentes() {

        Long hospedeId = 1L;
        Long quartoId = 5L;

        Viagem viagemHospede = new Viagem();
        viagemHospede.setId(1L);
        viagemHospede.setNome("Tomorrowland Brasil 2027");

        Viagem viagemQuarto = new Viagem();
        viagemQuarto.setId(3L);
        viagemQuarto.setNome("Tomorrowland Brasil 2029");

        Hospede hospede = new Hospede();
        hospede.setId(hospedeId);
        hospede.setNomeCompleto("João da Silva");
        hospede.setViagem(viagemHospede);

        Quarto quarto = new Quarto();
        quarto.setId(quartoId);
        quarto.setNome("Suíte 2029");
        quarto.setStatus(StatusQuarto.DISPONIVEL);
        quarto.setCapacidade(6);
        quarto.setViagem(viagemQuarto);

        AlocacaoQuartoRequest request = new AlocacaoQuartoRequest();
        request.setHospedeId(hospedeId);
        request.setQuartoId(quartoId);

        when(hospedeRepository.findById(hospedeId))
                .thenReturn(Optional.of(hospede));

        when(quartoRepository.findById(quartoId))
                .thenReturn(Optional.of(quarto));

        ViagemIncompativelException exception = assertThrows(
                ViagemIncompativelException.class,
                () -> alocacaoService.alocarHospede(request)
        );

        assertEquals(
                "Hóspede e quarto pertencem a viagens diferentes!",
                exception.getMessage()
        );

        verify(hospedeRepository).findById(hospedeId);
        verify(quartoRepository).findById(quartoId);

        verify(alocacaoRepository, never())
                .existsByHospedeIdAndViagemId(
                        anyLong(),
                        anyLong()
                );

        verify(alocacaoRepository, never())
                .countByQuartoId(anyLong());

        verify(alocacaoRepository, never())
                .save(any(AlocacaoQuarto.class));
    }

    @Test
    void deveLancarExcecaoQuandoQuartoEstiverIndisponivel() {

        Long hospedeId = 1L;
        Long quartoId = 2L;
        Long viagemId = 1L;

        Viagem viagem = new Viagem();
        viagem.setId(viagemId);

        Hospede hospede = new Hospede();
        hospede.setId(hospedeId);
        hospede.setNomeCompleto("João da Silva");
        hospede.setViagem(viagem);

        Quarto quarto = new Quarto();
        quarto.setId(quartoId);
        quarto.setNome("Suíte 01");
        quarto.setStatus(StatusQuarto.INDISPONIVEL);
        quarto.setCapacidade(6);
        quarto.setViagem(viagem);

        AlocacaoQuartoRequest request = new AlocacaoQuartoRequest();
        request.setHospedeId(hospedeId);
        request.setQuartoId(quartoId);

        when(hospedeRepository.findById(hospedeId))
                .thenReturn(Optional.of(hospede));

        when(quartoRepository.findById(quartoId))
                .thenReturn(Optional.of(quarto));

        QuartoIndisponivelException exception = assertThrows(
                QuartoIndisponivelException.class,
                () -> alocacaoService.alocarHospede(request)
        );

        assertEquals(
                "Quarto indisponível para alocação!",
                exception.getMessage()
        );

        verify(hospedeRepository).findById(hospedeId);
        verify(quartoRepository).findById(quartoId);

        verify(alocacaoRepository, never())
                .existsByHospedeIdAndViagemId(
                        anyLong(),
                        anyLong()
                );

        verify(alocacaoRepository, never())
                .countByQuartoId(anyLong());

        verify(alocacaoRepository, never())
                .save(any(AlocacaoQuarto.class));
    }

    @Test
    void deveLancarExcecaoQuandoHospedeJaEstiverAlocado() {

        Long hospedeId = 1L;
        Long quartoId = 2L;
        Long viagemId = 1L;

        Viagem viagem = new Viagem();
        viagem.setId(viagemId);

        Hospede hospede = new Hospede();
        hospede.setId(hospedeId);
        hospede.setViagem(viagem);

        Quarto quarto = new Quarto();
        quarto.setId(quartoId);
        quarto.setStatus(StatusQuarto.DISPONIVEL);
        quarto.setCapacidade(6);
        quarto.setViagem(viagem);

        AlocacaoQuartoRequest request = new AlocacaoQuartoRequest();
        request.setHospedeId(hospedeId);
        request.setQuartoId(quartoId);

        when(hospedeRepository.findById(hospedeId))
                .thenReturn(Optional.of(hospede));

        when(quartoRepository.findById(quartoId))
                .thenReturn(Optional.of(quarto));

        when(alocacaoRepository.existsByHospedeIdAndViagemId(
                hospedeId,
                viagemId
        )).thenReturn(true);

        HospedeJaAlocadoException exception = assertThrows(
                HospedeJaAlocadoException.class,
                () -> alocacaoService.alocarHospede(request)
        );

        assertEquals(
                "Hóspede já está alocado nesta viagem!",
                exception.getMessage()
        );

        verify(alocacaoRepository)
                .existsByHospedeIdAndViagemId(
                        hospedeId,
                        viagemId
                );

        verify(alocacaoRepository, never())
                .countByQuartoId(anyLong());

        verify(alocacaoRepository, never())
                .save(any(AlocacaoQuarto.class));
    }

    @Test
    void deveLancarExcecaoQuandoQuartoEstiverLotado() {

        Long hospedeId = 1L;
        Long quartoId = 2L;
        Long viagemId = 1L;

        Viagem viagem = new Viagem();
        viagem.setId(viagemId);

        Hospede hospede = new Hospede();
        hospede.setId(hospedeId);
        hospede.setViagem(viagem);

        Quarto quarto = new Quarto();
        quarto.setId(quartoId);
        quarto.setStatus(StatusQuarto.DISPONIVEL);
        quarto.setCapacidade(6);
        quarto.setViagem(viagem);

        AlocacaoQuartoRequest request = new AlocacaoQuartoRequest();
        request.setHospedeId(hospedeId);
        request.setQuartoId(quartoId);

        when(hospedeRepository.findById(hospedeId))
                .thenReturn(Optional.of(hospede));

        when(quartoRepository.findById(quartoId))
                .thenReturn(Optional.of(quarto));

        when(alocacaoRepository.existsByHospedeIdAndViagemId(
                hospedeId,
                viagemId
        )).thenReturn(false);

        when(alocacaoRepository.countByQuartoId(quartoId))
                .thenReturn(6L);

        QuartoLotadoException exception = assertThrows(
                QuartoLotadoException.class,
                () -> alocacaoService.alocarHospede(request)
        );

        assertEquals(
                "Quarto lotado!",
                exception.getMessage()
        );

        verify(alocacaoRepository)
                .existsByHospedeIdAndViagemId(
                        hospedeId,
                        viagemId
                );

        verify(alocacaoRepository)
                .countByQuartoId(quartoId);

        verify(alocacaoRepository, never())
                .save(any(AlocacaoQuarto.class));
    }

    @Test
    void deveBuscarAlocacaoPorIdComSucesso() {

        Long alocacaoId = 1L;

        AlocacaoQuarto alocacao = new AlocacaoQuarto();
        alocacao.setId(alocacaoId);

        AlocacaoQuartoResponse responseEsperado =
                new AlocacaoQuartoResponse();

        responseEsperado.setId(alocacaoId);
        responseEsperado.setHospedeId(1L);
        responseEsperado.setHospedeNome("João da Silva");
        responseEsperado.setQuartoId(2L);
        responseEsperado.setQuartoNome("Suíte 01");
        responseEsperado.setViagemId(1L);
        responseEsperado.setViagemNome("Tomorrowland Brasil 2027");

        when(alocacaoRepository.findById(alocacaoId))
                .thenReturn(Optional.of(alocacao));

        when(alocacaoMapper.paraResponse(alocacao))
                .thenReturn(responseEsperado);

        AlocacaoQuartoResponse resultado =
                alocacaoService.buscarAlocacaoPorId(alocacaoId);

        assertEquals(1L, resultado.getId());
        assertEquals(1L, resultado.getHospedeId());
        assertEquals("João da Silva", resultado.getHospedeNome());
        assertEquals(2L, resultado.getQuartoId());
        assertEquals("Suíte 01", resultado.getQuartoNome());
        assertEquals(1L, resultado.getViagemId());
        assertEquals(
                "Tomorrowland Brasil 2027",
                resultado.getViagemNome()
        );

        verify(alocacaoRepository).findById(alocacaoId);
        verify(alocacaoMapper).paraResponse(alocacao);
    }

    @Test
    void deveLancarExcecaoQuandoAlocacaoNaoForEncontrada() {

        Long alocacaoId = 999L;

        when(alocacaoRepository.findById(alocacaoId))
                .thenReturn(Optional.empty());

        AlocacaoNaoEncontradaException exception = assertThrows(
                AlocacaoNaoEncontradaException.class,
                () -> alocacaoService.buscarAlocacaoPorId(alocacaoId)
        );

        assertEquals(
                "Alocação não encontrada!",
                exception.getMessage()
        );

        verify(alocacaoRepository).findById(alocacaoId);

        verify(alocacaoMapper, never())
                .paraResponse(any(AlocacaoQuarto.class));
    }

    @Test
    void deveListarAlocacoesComSucesso() {

        AlocacaoQuarto alocacao1 = new AlocacaoQuarto();
        alocacao1.setId(1L);

        AlocacaoQuarto alocacao2 = new AlocacaoQuarto();
        alocacao2.setId(2L);

        AlocacaoQuartoResponse response1 =
                new AlocacaoQuartoResponse();
        response1.setId(1L);
        response1.setHospedeNome("João da Silva");
        response1.setQuartoNome("Suíte 01");

        AlocacaoQuartoResponse response2 =
                new AlocacaoQuartoResponse();
        response2.setId(2L);
        response2.setHospedeNome("Maria Oliveira Santos");
        response2.setQuartoNome("Suíte 02");

        when(alocacaoRepository.findAll())
                .thenReturn(List.of(alocacao1, alocacao2));

        when(alocacaoMapper.paraResponse(alocacao1))
                .thenReturn(response1);

        when(alocacaoMapper.paraResponse(alocacao2))
                .thenReturn(response2);

        List<AlocacaoQuartoResponse> resultado =
                alocacaoService.listarAlocacoes();

        assertEquals(2, resultado.size());

        assertEquals(1L, resultado.get(0).getId());
        assertEquals(
                "João da Silva",
                resultado.get(0).getHospedeNome()
        );

        assertEquals(2L, resultado.get(1).getId());
        assertEquals(
                "Maria Oliveira Santos",
                resultado.get(1).getHospedeNome()
        );

        verify(alocacaoRepository).findAll();
        verify(alocacaoMapper).paraResponse(alocacao1);
        verify(alocacaoMapper).paraResponse(alocacao2);
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoExistiremAlocacoes() {

        when(alocacaoRepository.findAll())
                .thenReturn(List.of());

        List<AlocacaoQuartoResponse> resultado =
                alocacaoService.listarAlocacoes();

        assertEquals(0, resultado.size());

        verify(alocacaoRepository).findAll();

        verify(alocacaoMapper, never())
                .paraResponse(any(AlocacaoQuarto.class));
    }

    @Test
    void deveListarAlocacoesPorQuartoComSucesso() {

        Long quartoId = 2L;

        Quarto quarto = new Quarto();
        quarto.setId(quartoId);
        quarto.setNome("Suíte 01");

        AlocacaoQuarto alocacao1 = new AlocacaoQuarto();
        alocacao1.setId(1L);

        AlocacaoQuarto alocacao2 = new AlocacaoQuarto();
        alocacao2.setId(2L);

        AlocacaoQuartoResponse response1 =
                new AlocacaoQuartoResponse();
        response1.setId(1L);
        response1.setHospedeId(1L);
        response1.setHospedeNome("João da Silva");
        response1.setQuartoId(quartoId);
        response1.setQuartoNome("Suíte 01");

        AlocacaoQuartoResponse response2 =
                new AlocacaoQuartoResponse();
        response2.setId(2L);
        response2.setHospedeId(2L);
        response2.setHospedeNome("Maria Oliveira Santos");
        response2.setQuartoId(quartoId);
        response2.setQuartoNome("Suíte 01");

        when(quartoRepository.findById(quartoId))
                .thenReturn(Optional.of(quarto));

        when(alocacaoRepository.findByQuartoId(quartoId))
                .thenReturn(List.of(alocacao1, alocacao2));

        when(alocacaoMapper.paraResponse(alocacao1))
                .thenReturn(response1);

        when(alocacaoMapper.paraResponse(alocacao2))
                .thenReturn(response2);

        List<AlocacaoQuartoResponse> resultado =
                alocacaoService.listarAlocacoesPorQuarto(quartoId);

        assertEquals(2, resultado.size());

        assertEquals(
                "João da Silva",
                resultado.get(0).getHospedeNome()
        );

        assertEquals(
                "Maria Oliveira Santos",
                resultado.get(1).getHospedeNome()
        );

        assertEquals(quartoId, resultado.get(0).getQuartoId());
        assertEquals(quartoId, resultado.get(1).getQuartoId());

        verify(quartoRepository).findById(quartoId);
        verify(alocacaoRepository).findByQuartoId(quartoId);

        verify(alocacaoMapper).paraResponse(alocacao1);
        verify(alocacaoMapper).paraResponse(alocacao2);
    }

    @Test
    void deveRetornarListaVaziaQuandoQuartoNaoPossuirAlocacoes() {

        Long quartoId = 2L;

        Quarto quarto = new Quarto();
        quarto.setId(quartoId);
        quarto.setNome("Suíte 01");

        when(quartoRepository.findById(quartoId))
                .thenReturn(Optional.of(quarto));

        when(alocacaoRepository.findByQuartoId(quartoId))
                .thenReturn(List.of());

        List<AlocacaoQuartoResponse> resultado =
                alocacaoService.listarAlocacoesPorQuarto(quartoId);

        assertEquals(0, resultado.size());

        verify(quartoRepository).findById(quartoId);
        verify(alocacaoRepository).findByQuartoId(quartoId);

        verify(alocacaoMapper, never())
                .paraResponse(any(AlocacaoQuarto.class));
    }

    @Test
    void deveLancarExcecaoAoListarAlocacoesDeQuartoInexistente() {

        Long quartoId = 999L;

        when(quartoRepository.findById(quartoId))
                .thenReturn(Optional.empty());

        QuartoNaoEncontradoException exception = assertThrows(
                QuartoNaoEncontradoException.class,
                () -> alocacaoService.listarAlocacoesPorQuarto(quartoId)
        );

        assertEquals(
                "Quarto não encontrado!",
                exception.getMessage()
        );

        verify(quartoRepository).findById(quartoId);

        verify(alocacaoRepository, never())
                .findByQuartoId(anyLong());

        verify(alocacaoMapper, never())
                .paraResponse(any(AlocacaoQuarto.class));
    }

    @Test
    void deveTrocarQuartoComSucesso() {

        Long alocacaoId = 1L;
        Long novoQuartoId = 3L;
        Long viagemId = 1L;

        Viagem viagem = new Viagem();
        viagem.setId(viagemId);
        viagem.setNome("Tomorrowland Brasil 2027");

        Hospede hospede = new Hospede();
        hospede.setId(1L);
        hospede.setNomeCompleto("João da Silva");
        hospede.setViagem(viagem);

        Quarto quartoAtual = new Quarto();
        quartoAtual.setId(2L);
        quartoAtual.setNome("Suíte 01");
        quartoAtual.setViagem(viagem);

        Quarto novoQuarto = new Quarto();
        novoQuarto.setId(novoQuartoId);
        novoQuarto.setNome("Suíte 02");
        novoQuarto.setCapacidade(6);
        novoQuarto.setStatus(StatusQuarto.DISPONIVEL);
        novoQuarto.setViagem(viagem);

        AlocacaoQuarto alocacao = new AlocacaoQuarto();
        alocacao.setId(alocacaoId);
        alocacao.setHospede(hospede);
        alocacao.setQuarto(quartoAtual);
        alocacao.setViagem(viagem);

        AlocacaoQuartoResponse responseEsperado =
                new AlocacaoQuartoResponse();

        responseEsperado.setId(alocacaoId);
        responseEsperado.setHospedeId(1L);
        responseEsperado.setHospedeNome("João da Silva");
        responseEsperado.setQuartoId(novoQuartoId);
        responseEsperado.setQuartoNome("Suíte 02");
        responseEsperado.setViagemId(viagemId);

        when(alocacaoRepository.findById(alocacaoId))
                .thenReturn(Optional.of(alocacao));

        when(quartoRepository.findById(novoQuartoId))
                .thenReturn(Optional.of(novoQuarto));

        when(alocacaoRepository.countByQuartoId(novoQuartoId))
                .thenReturn(2L);

        when(alocacaoRepository.save(alocacao))
                .thenReturn(alocacao);

        when(alocacaoMapper.paraResponse(alocacao))
                .thenReturn(responseEsperado);

        AlocacaoQuartoResponse resultado =
                alocacaoService.trocarQuarto(
                        alocacaoId,
                        novoQuartoId
                );

        assertEquals(alocacaoId, resultado.getId());
        assertEquals(novoQuartoId, resultado.getQuartoId());
        assertEquals("Suíte 02", resultado.getQuartoNome());

        // Verifica a alteração real na entidade
        assertEquals(novoQuarto, alocacao.getQuarto());

        verify(alocacaoRepository).findById(alocacaoId);
        verify(quartoRepository).findById(novoQuartoId);
        verify(alocacaoRepository).countByQuartoId(novoQuartoId);
        verify(alocacaoRepository).save(alocacao);
        verify(alocacaoMapper).paraResponse(alocacao);
    }

    @Test
    void deveLancarExcecaoAoTrocarQuartoDeAlocacaoInexistente() {

        Long alocacaoId = 999L;
        Long novoQuartoId = 3L;

        when(alocacaoRepository.findById(alocacaoId))
                .thenReturn(Optional.empty());

        AlocacaoNaoEncontradaException exception = assertThrows(
                AlocacaoNaoEncontradaException.class,
                () -> alocacaoService.trocarQuarto(
                        alocacaoId,
                        novoQuartoId
                )
        );

        assertEquals(
                "Alocação não encontrada!",
                exception.getMessage()
        );

        verify(alocacaoRepository).findById(alocacaoId);

        verify(quartoRepository, never())
                .findById(anyLong());

        verify(alocacaoRepository, never())
                .countByQuartoId(anyLong());

        verify(alocacaoRepository, never())
                .save(any(AlocacaoQuarto.class));

        verify(alocacaoMapper, never())
                .paraResponse(any(AlocacaoQuarto.class));
    }

    @Test
    void deveLancarExcecaoAoTrocarParaQuartoInexistente() {

        Long alocacaoId = 1L;
        Long novoQuartoId = 999L;

        AlocacaoQuarto alocacao = new AlocacaoQuarto();
        alocacao.setId(alocacaoId);

        when(alocacaoRepository.findById(alocacaoId))
                .thenReturn(Optional.of(alocacao));

        when(quartoRepository.findById(novoQuartoId))
                .thenReturn(Optional.empty());

        QuartoNaoEncontradoException exception = assertThrows(
                QuartoNaoEncontradoException.class,
                () -> alocacaoService.trocarQuarto(
                        alocacaoId,
                        novoQuartoId
                )
        );

        assertEquals(
                "Quarto não encontrado!",
                exception.getMessage()
        );

        verify(alocacaoRepository).findById(alocacaoId);
        verify(quartoRepository).findById(novoQuartoId);

        verify(alocacaoRepository, never())
                .countByQuartoId(anyLong());

        verify(alocacaoRepository, never())
                .save(any(AlocacaoQuarto.class));

        verify(alocacaoMapper, never())
                .paraResponse(any(AlocacaoQuarto.class));
    }

    @Test
    void deveLancarExcecaoAoTrocarParaQuartoDeOutraViagem() {

        Long alocacaoId = 1L;
        Long novoQuartoId = 5L;

        Viagem viagemAlocacao = new Viagem();
        viagemAlocacao.setId(1L);

        Viagem viagemNovoQuarto = new Viagem();
        viagemNovoQuarto.setId(3L);

        AlocacaoQuarto alocacao = new AlocacaoQuarto();
        alocacao.setId(alocacaoId);
        alocacao.setViagem(viagemAlocacao);

        Quarto novoQuarto = new Quarto();
        novoQuarto.setId(novoQuartoId);
        novoQuarto.setViagem(viagemNovoQuarto);
        novoQuarto.setStatus(StatusQuarto.DISPONIVEL);
        novoQuarto.setCapacidade(6);

        when(alocacaoRepository.findById(alocacaoId))
                .thenReturn(Optional.of(alocacao));

        when(quartoRepository.findById(novoQuartoId))
                .thenReturn(Optional.of(novoQuarto));

        ViagemIncompativelException exception = assertThrows(
                ViagemIncompativelException.class,
                () -> alocacaoService.trocarQuarto(
                        alocacaoId,
                        novoQuartoId
                )
        );

        assertEquals(
                "Hóspede e quarto pertencem a viagens diferentes!",
                exception.getMessage()
        );

        verify(alocacaoRepository).findById(alocacaoId);
        verify(quartoRepository).findById(novoQuartoId);

        verify(alocacaoRepository, never())
                .countByQuartoId(anyLong());

        verify(alocacaoRepository, never())
                .save(any(AlocacaoQuarto.class));

        verify(alocacaoMapper, never())
                .paraResponse(any(AlocacaoQuarto.class));
    }

    @Test
    void deveLancarExcecaoAoTrocarParaQuartoIndisponivel() {

        Long alocacaoId = 1L;
        Long novoQuartoId = 3L;
        Long viagemId = 1L;

        Viagem viagem = new Viagem();
        viagem.setId(viagemId);

        AlocacaoQuarto alocacao = new AlocacaoQuarto();
        alocacao.setId(alocacaoId);
        alocacao.setViagem(viagem);

        Quarto novoQuarto = new Quarto();
        novoQuarto.setId(novoQuartoId);
        novoQuarto.setViagem(viagem);
        novoQuarto.setStatus(StatusQuarto.INDISPONIVEL);
        novoQuarto.setCapacidade(6);

        when(alocacaoRepository.findById(alocacaoId))
                .thenReturn(Optional.of(alocacao));

        when(quartoRepository.findById(novoQuartoId))
                .thenReturn(Optional.of(novoQuarto));

        QuartoIndisponivelException exception = assertThrows(
                QuartoIndisponivelException.class,
                () -> alocacaoService.trocarQuarto(
                        alocacaoId,
                        novoQuartoId
                )
        );

        assertEquals(
                "Quarto indisponível para alocação!",
                exception.getMessage()
        );

        verify(alocacaoRepository).findById(alocacaoId);
        verify(quartoRepository).findById(novoQuartoId);

        verify(alocacaoRepository, never())
                .countByQuartoId(anyLong());

        verify(alocacaoRepository, never())
                .save(any(AlocacaoQuarto.class));

        verify(alocacaoMapper, never())
                .paraResponse(any(AlocacaoQuarto.class));
    }

    @Test
    void deveLancarExcecaoAoTrocarParaQuartoLotado() {

        Long alocacaoId = 1L;
        Long novoQuartoId = 3L;
        Long viagemId = 1L;

        Viagem viagem = new Viagem();
        viagem.setId(viagemId);

        AlocacaoQuarto alocacao = new AlocacaoQuarto();
        alocacao.setId(alocacaoId);
        alocacao.setViagem(viagem);

        Quarto novoQuarto = new Quarto();
        novoQuarto.setId(novoQuartoId);
        novoQuarto.setViagem(viagem);
        novoQuarto.setStatus(StatusQuarto.DISPONIVEL);
        novoQuarto.setCapacidade(6);

        when(alocacaoRepository.findById(alocacaoId))
                .thenReturn(Optional.of(alocacao));

        when(quartoRepository.findById(novoQuartoId))
                .thenReturn(Optional.of(novoQuarto));

        when(alocacaoRepository.countByQuartoId(novoQuartoId))
                .thenReturn(6L);

        QuartoLotadoException exception = assertThrows(
                QuartoLotadoException.class,
                () -> alocacaoService.trocarQuarto(
                        alocacaoId,
                        novoQuartoId
                )
        );

        assertEquals(
                "Quarto lotado!",
                exception.getMessage()
        );

        verify(alocacaoRepository).findById(alocacaoId);
        verify(quartoRepository).findById(novoQuartoId);
        verify(alocacaoRepository).countByQuartoId(novoQuartoId);

        verify(alocacaoRepository, never())
                .save(any(AlocacaoQuarto.class));

        verify(alocacaoMapper, never())
                .paraResponse(any(AlocacaoQuarto.class));
    }

    @Test
    void deveRemoverAlocacaoComSucesso() {

        Long alocacaoId = 1L;

        AlocacaoQuarto alocacao = new AlocacaoQuarto();
        alocacao.setId(alocacaoId);

        when(alocacaoRepository.findById(alocacaoId))
                .thenReturn(Optional.of(alocacao));

        alocacaoService.removerAlocacao(alocacaoId);

        verify(alocacaoRepository).findById(alocacaoId);
        verify(alocacaoRepository).delete(alocacao);
    }

    @Test
    void deveLancarExcecaoAoRemoverAlocacaoInexistente() {

        Long alocacaoId = 999L;

        when(alocacaoRepository.findById(alocacaoId))
                .thenReturn(Optional.empty());

        AlocacaoNaoEncontradaException exception = assertThrows(
                AlocacaoNaoEncontradaException.class,
                () -> alocacaoService.removerAlocacao(alocacaoId)
        );

        assertEquals(
                "Alocação não encontrada!",
                exception.getMessage()
        );

        verify(alocacaoRepository).findById(alocacaoId);

        verify(alocacaoRepository, never())
                .delete(any(AlocacaoQuarto.class));
    }

    @Test
    void deveBuscarOcupacaoDoQuartoComSucesso() {

        Long quartoId = 2L;

        Quarto quarto = new Quarto();
        quarto.setId(quartoId);
        quarto.setNome("Suíte 01");
        quarto.setCapacidade(6);

        when(quartoRepository.findById(quartoId))
                .thenReturn(Optional.of(quarto));

        when(alocacaoRepository.countByQuartoId(quartoId))
                .thenReturn(4L);

        OcupacaoQuartoResponse resultado =
                alocacaoService.buscarOcupacaoPorQuarto(quartoId);

        assertEquals(2L, resultado.getQuartoId());
        assertEquals("Suíte 01", resultado.getQuartoNome());
        assertEquals(6, resultado.getCapacidade());
        assertEquals(4L, resultado.getOcupacao());
        assertEquals(2L, resultado.getVagasDisponiveis());

        verify(quartoRepository).findById(quartoId);
        verify(alocacaoRepository).countByQuartoId(quartoId);
    }

    @Test
    void deveLancarExcecaoAoBuscarOcupacaoDeQuartoInexistente() {

        Long quartoId = 999L;

        when(quartoRepository.findById(quartoId))
                .thenReturn(Optional.empty());

        QuartoNaoEncontradoException exception = assertThrows(
                QuartoNaoEncontradoException.class,
                () -> alocacaoService.buscarOcupacaoPorQuarto(quartoId)
        );

        assertEquals(
                "Quarto não encontrado!",
                exception.getMessage()
        );

        verify(quartoRepository).findById(quartoId);

        verify(alocacaoRepository, never())
                .countByQuartoId(anyLong());
    }

}