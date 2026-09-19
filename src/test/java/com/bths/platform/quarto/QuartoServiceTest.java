package com.bths.platform.quarto;

import com.bths.platform.quarto.exception.QuartoNaoEncontradoException;
import com.bths.platform.viagem.exception.ViagemNaoEncontradaException;
import com.bths.platform.quarto.dto.QuartoRequest;
import com.bths.platform.quarto.dto.QuartoResponse;
import com.bths.platform.quarto.enums.StatusQuarto;
import com.bths.platform.quarto.enums.TipoQuarto;
import com.bths.platform.quarto.mapper.QuartoMapper;
import com.bths.platform.viagem.Viagem;
import com.bths.platform.viagem.ViagemRepository;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuartoServiceTest {

    @Mock
    private QuartoRepository quartoRepository;

    @Mock
    private ViagemRepository viagemRepository;

    @Mock
    private QuartoMapper quartoMapper;

    private QuartoService quartoService;

    @BeforeEach
    void setUp() {

        quartoService = new QuartoService(
                quartoRepository,
                viagemRepository,
                quartoMapper
        );
    }

    @Test
    void deveCadastrarQuartoComSucesso() {

        Long viagemId = 1L;

        QuartoRequest request = new QuartoRequest();
        request.setNome("Suíte 01");
        request.setTipo(TipoQuarto.SUITE);
        request.setCapacidade(6);
        request.setStatus(StatusQuarto.DISPONIVEL);
        request.setViagemId(viagemId);

        Viagem viagem = new Viagem();
        viagem.setId(viagemId);
        viagem.setNome("Tomorrowland Brasil 2027");

        Quarto quartoSalvo = new Quarto();
        quartoSalvo.setId(1L);
        quartoSalvo.setNome("Suíte 01");
        quartoSalvo.setTipo(TipoQuarto.SUITE);
        quartoSalvo.setCapacidade(6);
        quartoSalvo.setStatus(StatusQuarto.DISPONIVEL);
        quartoSalvo.setViagem(viagem);

        QuartoResponse responseEsperado = new QuartoResponse();
        responseEsperado.setId(1L);
        responseEsperado.setNome("Suíte 01");
        responseEsperado.setTipo(TipoQuarto.SUITE);
        responseEsperado.setCapacidade(6);
        responseEsperado.setStatus(StatusQuarto.DISPONIVEL);
        responseEsperado.setViagemId(viagemId);
        responseEsperado.setViagemNome("Tomorrowland Brasil 2027");

        when(viagemRepository.findById(viagemId))
                .thenReturn(Optional.of(viagem));

        when(quartoRepository.save(any(Quarto.class)))
                .thenReturn(quartoSalvo);

        when(quartoMapper.paraResponse(quartoSalvo))
                .thenReturn(responseEsperado);

        QuartoResponse resultado =
                quartoService.cadastrarQuarto(request);

        assertEquals(1L, resultado.getId());
        assertEquals("Suíte 01", resultado.getNome());
        assertEquals(TipoQuarto.SUITE, resultado.getTipo());
        assertEquals(6, resultado.getCapacidade());
        assertEquals(StatusQuarto.DISPONIVEL, resultado.getStatus());
        assertEquals(1L, resultado.getViagemId());
        assertEquals("Tomorrowland Brasil 2027", resultado.getViagemNome());

        verify(viagemRepository).findById(viagemId);

        verify(quartoMapper)
                .atualizarEntidade(any(Quarto.class), eq(request));

        verify(quartoRepository)
                .save(any(Quarto.class));

        verify(quartoMapper)
                .paraResponse(quartoSalvo);
    }

    @Test
    void deveLancarExcecaoAoCadastrarQuartoComViagemInexistente() {

        Long viagemId = 999L;

        QuartoRequest request = new QuartoRequest();
        request.setNome("Suíte 01");
        request.setTipo(TipoQuarto.SUITE);
        request.setCapacidade(6);
        request.setStatus(StatusQuarto.DISPONIVEL);
        request.setViagemId(viagemId);

        when(viagemRepository.findById(viagemId))
                .thenReturn(Optional.empty());

        ViagemNaoEncontradaException exception = assertThrows(
                ViagemNaoEncontradaException.class,
                () -> quartoService.cadastrarQuarto(request)
        );

        assertEquals(
                "Viagem não encontrada!",
                exception.getMessage()
        );

        verify(viagemRepository).findById(viagemId);

        verify(quartoMapper, never())
                .atualizarEntidade(
                        any(Quarto.class),
                        any(QuartoRequest.class)
                );

        verify(quartoRepository, never())
                .save(any(Quarto.class));
    }

    @Test
    void deveBuscarQuartoPorIdComSucesso() {

        Long id = 1L;

        Viagem viagem = new Viagem();
        viagem.setId(1L);
        viagem.setNome("Tomorrowland Brasil 2027");

        Quarto quarto = new Quarto();
        quarto.setId(id);
        quarto.setNome("Suíte 01");
        quarto.setTipo(TipoQuarto.SUITE);
        quarto.setCapacidade(6);
        quarto.setStatus(StatusQuarto.DISPONIVEL);
        quarto.setViagem(viagem);

        QuartoResponse responseEsperado = new QuartoResponse();
        responseEsperado.setId(id);
        responseEsperado.setNome("Suíte 01");
        responseEsperado.setTipo(TipoQuarto.SUITE);
        responseEsperado.setCapacidade(6);
        responseEsperado.setStatus(StatusQuarto.DISPONIVEL);
        responseEsperado.setViagemId(1L);
        responseEsperado.setViagemNome("Tomorrowland Brasil 2027");

        when(quartoRepository.findById(id))
                .thenReturn(Optional.of(quarto));

        when(quartoMapper.paraResponse(quarto))
                .thenReturn(responseEsperado);

        QuartoResponse resultado =
                quartoService.buscarQuartoPorId(id);

        assertEquals(1L, resultado.getId());
        assertEquals("Suíte 01", resultado.getNome());
        assertEquals(TipoQuarto.SUITE, resultado.getTipo());
        assertEquals(6, resultado.getCapacidade());
        assertEquals(StatusQuarto.DISPONIVEL, resultado.getStatus());
        assertEquals(1L, resultado.getViagemId());
        assertEquals("Tomorrowland Brasil 2027", resultado.getViagemNome());

        verify(quartoRepository).findById(id);
        verify(quartoMapper).paraResponse(quarto);
    }

    @Test
    void deveLancarExcecaoQuandoQuartoNaoForEncontrado() {

        Long id = 999L;

        when(quartoRepository.findById(id))
                .thenReturn(Optional.empty());

        QuartoNaoEncontradoException exception = assertThrows(
                QuartoNaoEncontradoException.class,
                () -> quartoService.buscarQuartoPorId(id)
        );

        assertEquals(
                "Quarto não encontrado!",
                exception.getMessage()
        );

        verify(quartoRepository).findById(id);

        verify(quartoMapper, never())
                .paraResponse(any(Quarto.class));
    }

    @Test
    void deveListarQuartosComSucesso() {

        Quarto quarto1 = new Quarto();
        quarto1.setId(1L);
        quarto1.setNome("Suíte 01");
        quarto1.setTipo(TipoQuarto.SUITE);
        quarto1.setCapacidade(6);
        quarto1.setStatus(StatusQuarto.DISPONIVEL);

        Quarto quarto2 = new Quarto();
        quarto2.setId(2L);
        quarto2.setNome("Alojamento 01");
        quarto2.setTipo(TipoQuarto.ALOJAMENTO);
        quarto2.setCapacidade(18);
        quarto2.setStatus(StatusQuarto.DISPONIVEL);

        QuartoResponse response1 = new QuartoResponse();
        response1.setId(1L);
        response1.setNome("Suíte 01");
        response1.setTipo(TipoQuarto.SUITE);
        response1.setCapacidade(6);

        QuartoResponse response2 = new QuartoResponse();
        response2.setId(2L);
        response2.setNome("Alojamento 01");
        response2.setTipo(TipoQuarto.ALOJAMENTO);
        response2.setCapacidade(18);

        when(quartoRepository.findAll())
                .thenReturn(List.of(quarto1, quarto2));

        when(quartoMapper.paraResponse(quarto1))
                .thenReturn(response1);

        when(quartoMapper.paraResponse(quarto2))
                .thenReturn(response2);

        List<QuartoResponse> resultado =
                quartoService.listarQuartos();

        assertEquals(2, resultado.size());

        assertEquals("Suíte 01", resultado.get(0).getNome());
        assertEquals(TipoQuarto.SUITE, resultado.get(0).getTipo());
        assertEquals(6, resultado.get(0).getCapacidade());

        assertEquals("Alojamento 01", resultado.get(1).getNome());
        assertEquals(TipoQuarto.ALOJAMENTO, resultado.get(1).getTipo());
        assertEquals(18, resultado.get(1).getCapacidade());

        verify(quartoRepository).findAll();
        verify(quartoMapper).paraResponse(quarto1);
        verify(quartoMapper).paraResponse(quarto2);
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoExistiremQuartos() {

        when(quartoRepository.findAll())
                .thenReturn(List.of());

        List<QuartoResponse> resultado =
                quartoService.listarQuartos();

        assertEquals(0, resultado.size());

        verify(quartoRepository).findAll();

        verify(quartoMapper, never())
                .paraResponse(any(Quarto.class));
    }

    @Test
    void deveAtualizarQuartoComSucesso() {

        Long quartoId = 1L;
        Long viagemId = 1L;

        QuartoRequest request = new QuartoRequest();
        request.setNome("Suíte 01 Premium");
        request.setTipo(TipoQuarto.SUITE);
        request.setCapacidade(8);
        request.setStatus(StatusQuarto.DISPONIVEL);
        request.setViagemId(viagemId);

        Viagem viagem = new Viagem();
        viagem.setId(viagemId);
        viagem.setNome("Tomorrowland Brasil 2027");

        Quarto quarto = new Quarto();
        quarto.setId(quartoId);
        quarto.setNome("Suíte 01");
        quarto.setTipo(TipoQuarto.SUITE);
        quarto.setCapacidade(6);
        quarto.setStatus(StatusQuarto.DISPONIVEL);
        quarto.setViagem(viagem);

        QuartoResponse responseEsperado = new QuartoResponse();
        responseEsperado.setId(quartoId);
        responseEsperado.setNome("Suíte 01 Premium");
        responseEsperado.setTipo(TipoQuarto.SUITE);
        responseEsperado.setCapacidade(8);
        responseEsperado.setStatus(StatusQuarto.DISPONIVEL);
        responseEsperado.setViagemId(viagemId);
        responseEsperado.setViagemNome("Tomorrowland Brasil 2027");

        when(quartoRepository.findById(quartoId))
                .thenReturn(Optional.of(quarto));

        when(viagemRepository.findById(viagemId))
                .thenReturn(Optional.of(viagem));

        when(quartoRepository.save(quarto))
                .thenReturn(quarto);

        when(quartoMapper.paraResponse(quarto))
                .thenReturn(responseEsperado);

        QuartoResponse resultado =
                quartoService.atualizarQuarto(quartoId, request);

        assertEquals(1L, resultado.getId());
        assertEquals("Suíte 01 Premium", resultado.getNome());
        assertEquals(TipoQuarto.SUITE, resultado.getTipo());
        assertEquals(8, resultado.getCapacidade());
        assertEquals(StatusQuarto.DISPONIVEL, resultado.getStatus());
        assertEquals(1L, resultado.getViagemId());

        verify(quartoRepository).findById(quartoId);
        verify(viagemRepository).findById(viagemId);

        verify(quartoMapper)
                .atualizarEntidade(quarto, request);

        verify(quartoRepository).save(quarto);
        verify(quartoMapper).paraResponse(quarto);
    }

    @Test
    void deveLancarExcecaoAoAtualizarQuartoInexistente() {

        Long quartoId = 999L;

        QuartoRequest request = new QuartoRequest();
        request.setNome("Suíte 01 Premium");
        request.setTipo(TipoQuarto.SUITE);
        request.setCapacidade(8);
        request.setStatus(StatusQuarto.DISPONIVEL);
        request.setViagemId(1L);

        when(quartoRepository.findById(quartoId))
                .thenReturn(Optional.empty());

        QuartoNaoEncontradoException exception = assertThrows(
                QuartoNaoEncontradoException.class,
                () -> quartoService.atualizarQuarto(quartoId, request)
        );

        assertEquals(
                "Quarto não encontrado!",
                exception.getMessage()
        );

        verify(quartoRepository).findById(quartoId);

        verify(viagemRepository, never())
                .findById(anyLong());

        verify(quartoMapper, never())
                .atualizarEntidade(
                        any(Quarto.class),
                        any(QuartoRequest.class)
                );

        verify(quartoRepository, never())
                .save(any(Quarto.class));
    }

    @Test
    void deveLancarExcecaoAoAtualizarQuartoComViagemInexistente() {

        Long quartoId = 1L;
        Long viagemId = 999L;

        QuartoRequest request = new QuartoRequest();
        request.setNome("Suíte 01 Premium");
        request.setTipo(TipoQuarto.SUITE);
        request.setCapacidade(8);
        request.setStatus(StatusQuarto.DISPONIVEL);
        request.setViagemId(viagemId);

        Quarto quarto = new Quarto();
        quarto.setId(quartoId);
        quarto.setNome("Suíte 01");

        when(quartoRepository.findById(quartoId))
                .thenReturn(Optional.of(quarto));

        when(viagemRepository.findById(viagemId))
                .thenReturn(Optional.empty());

        ViagemNaoEncontradaException exception = assertThrows(
                ViagemNaoEncontradaException.class,
                () -> quartoService.atualizarQuarto(quartoId, request)
        );

        assertEquals(
                "Viagem não encontrada!",
                exception.getMessage()
        );

        verify(quartoRepository).findById(quartoId);
        verify(viagemRepository).findById(viagemId);

        verify(quartoMapper, never())
                .atualizarEntidade(
                        any(Quarto.class),
                        any(QuartoRequest.class)
                );

        verify(quartoRepository, never())
                .save(any(Quarto.class));
    }

    @Test
    void deveDeletarQuartoComSucesso() {

        Long id = 1L;

        Quarto quarto = new Quarto();
        quarto.setId(id);
        quarto.setNome("Suíte 01");

        when(quartoRepository.findById(id))
                .thenReturn(Optional.of(quarto));

        quartoService.deletarQuarto(id);

        verify(quartoRepository).findById(id);
        verify(quartoRepository).delete(quarto);
    }

    @Test
    void deveLancarExcecaoAoDeletarQuartoInexistente() {

        Long id = 999L;

        when(quartoRepository.findById(id))
                .thenReturn(Optional.empty());

        QuartoNaoEncontradoException exception = assertThrows(
                QuartoNaoEncontradoException.class,
                () -> quartoService.deletarQuarto(id)
        );

        assertEquals(
                "Quarto não encontrado!",
                exception.getMessage()
        );

        verify(quartoRepository).findById(id);

        verify(quartoRepository, never())
                .delete(any(Quarto.class));
    }
}