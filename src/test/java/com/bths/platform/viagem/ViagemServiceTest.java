package com.bths.platform.viagem;

import com.bths.platform.exception.ViagemNaoEncontradaException;
import com.bths.platform.viagem.mapper.ViagemMapper;
import com.bths.platform.viagem.dto.ViagemRequest;
import com.bths.platform.viagem.dto.ViagemResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.ArgumentMatchers.any;

import java.util.List;
import java.util.Optional;

import java.time.LocalDate;

@ExtendWith(MockitoExtension.class)
class ViagemServiceTest {

    @Mock
    private ViagemRepository viagemRepository;

    @Mock
    private ViagemMapper viagemMapper;

    @InjectMocks
    private ViagemService viagemService;

    @Test
    void deveCadastrarViagemComSucesso() {

        ViagemRequest request = new ViagemRequest();

        request.setNome("Tomorrowland Brasil 2027");
        request.setEvento("Tomorrowland Brasil");
        request.setDataInicio(LocalDate.of(2027, 4, 29));
        request.setDataFim(LocalDate.of(2027, 5, 3));
        request.setEndereco("Estrada dos Engenheiros");
        request.setCidade("Alumínio");
        request.setEstado("SP");
        request.setStatus(StatusViagem.PLANEJADA);


        Viagem viagem = new Viagem();

        viagem.setNome(request.getNome());
        viagem.setEvento(request.getEvento());
        viagem.setDataInicio(request.getDataInicio());
        viagem.setDataFim(request.getDataFim());
        viagem.setEndereco(request.getEndereco());
        viagem.setCidade(request.getCidade());
        viagem.setEstado(request.getEstado());
        viagem.setStatus(request.getStatus());


        ViagemResponse responseEsperado = new ViagemResponse();

        responseEsperado.setId(1L);
        responseEsperado.setNome("Tomorrowland Brasil 2027");
        responseEsperado.setEvento("Tomorrowland Brasil");
        responseEsperado.setDataInicio(LocalDate.of(2027, 4, 29));
        responseEsperado.setDataFim(LocalDate.of(2027, 5, 3));
        responseEsperado.setEndereco("Estrada dos Engenheiros");
        responseEsperado.setCidade("Alumnio");
        responseEsperado.setEstado("SP");
        responseEsperado.setStatus(StatusViagem.PLANEJADA);


        when(viagemMapper.paraEntidade(request))
                .thenReturn(viagem);

        when(viagemRepository.save(viagem))
                .thenReturn(viagem);

        when(viagemMapper.paraResponse(viagem))
                .thenReturn(responseEsperado);


        ViagemResponse resultado = viagemService.cadastrarViagem(request);

        assertEquals(1L, resultado.getId());
        assertEquals("Tomorrowland Brasil 2027", resultado.getNome());
        assertEquals("Tomorrowland Brasil", resultado.getEvento());
        assertEquals(LocalDate.of(2027, 4, 29), resultado.getDataInicio());
        assertEquals(LocalDate.of(2027, 5, 3), resultado.getDataFim());
        assertEquals(StatusViagem.PLANEJADA, resultado.getStatus());


        verify(viagemRepository).save(viagem);

    }

    @Test
    void deveLancarExcecaoQuandoDataFimForAnteriorADataIncio() {

        ViagemRequest request = new ViagemRequest();

        request.setNome("Tomorrowland Brasil 2027");
        request.setEvento("Tomorrowland Brasil");
        request.setDataInicio(LocalDate.of(2027, 5, 3));


        // Data final propositalmente inválida
        request.setDataFim(LocalDate.of(2027, 4, 29));

        request.setEndereco("Estrada dos Engenheiros");
        request.setCidade("Alumnio");
        request.setEstado("SP");
        request.setStatus(StatusViagem.PLANEJADA);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> viagemService.cadastrarViagem(request)
        );

        assertEquals(
                "A data final da viagem não pode ser anterior à data inicial.",
                exception.getMessage()
        );

        verify(viagemRepository, never()).save(any());

    }

    @Test
    void deveBuscarViagemPorIdComSucesso() {

        Long id = 1L;

        Viagem viagem = new Viagem();
        viagem.setId(id);
        viagem.setNome("Tomorrowland Brasil 2027");

        ViagemResponse responseEsperado = new ViagemResponse();
        responseEsperado.setId(id);
        responseEsperado.setNome("Tomorrowland Brasil 2027");

        when(viagemRepository.findById(id))
                .thenReturn(Optional.of(viagem));

        when(viagemMapper.paraResponse(viagem))
                .thenReturn(responseEsperado);


        ViagemResponse resultado = viagemService.buscarViagemPorId(id);

        assertEquals(1L, resultado.getId());
        assertEquals("Tomorrowland Brasil 2027", resultado.getNome());

        verify(viagemRepository).findById(id);
        verify(viagemMapper).paraResponse(viagem);


        Optional.of(viagem);
    }

    @Test
    void deveLancarExcecaoQuandoViagemNaoforEncontrada() {

        Long id = 999L;

        when(viagemRepository.findById(id))
                .thenReturn(Optional.empty());

        ViagemNaoEncontradaException exception = assertThrows(
                ViagemNaoEncontradaException.class,
                () -> viagemService.buscarViagemPorId(id)
        );

        assertEquals(
                "Viagem não encontrada!",
                exception.getMessage()
        );

        verify(viagemRepository).findById(id);
        verify(viagemMapper, never()).paraResponse(any());
    }

    @Test
    void deveListarviagensComSucesso() {

        Viagem viagem1 = new Viagem();
        viagem1.setId(1L);
        viagem1.setNome("Tomorrowland Brasil 2027");

        Viagem viagem2 = new Viagem();
        viagem2.setId(2L);
        viagem2.setNome("Tomorrowland Brasil 2027");

        ViagemResponse response1 = new ViagemResponse();
        response1.setId(1L);
        response1.setNome("Tomorrowland Brasil 2027");

        ViagemResponse response2 = new ViagemResponse();
        response2.setId(2L);
        response2.setNome("Tomorrowland Brasil 2027");

        when(viagemRepository.findAll())
                .thenReturn(List.of(viagem1, viagem2));

        when(viagemMapper.paraResponse(viagem1))
                .thenReturn(response1);

        when(viagemMapper.paraResponse(viagem2))
                .thenReturn(response2);

        List<ViagemResponse> resultado = viagemService.listarViagens();

        assertEquals(2, resultado.size());

        assertEquals(1L, resultado.get(0).getId());
        assertEquals("Tomorrowland Brasil 2027", resultado.get(0).getNome());

        assertEquals(2L, resultado.get(1).getId());
        assertEquals("Tomorrowland Brasil 2027", resultado.get(1).getNome());

        verify(viagemRepository).findAll();
        verify(viagemMapper).paraResponse(viagem1);
        verify(viagemMapper).paraResponse(viagem2);

    }

    @Test
    void deveRetornarListaVaziaQuandoNaoExistiremViagens() {

        when(viagemRepository.findAll())
                .thenReturn(List.of());

        List<ViagemResponse> resultado = viagemService.listarViagens();

        assertEquals(0, resultado.size());

        verify(viagemRepository).findAll();
        verify(viagemMapper, never()).paraResponse(any());
    }

    @Test
    void deveAtualizarViagemComSucesso() {

        Long id = 1L;

        ViagemRequest request = new ViagemRequest();
        request.setNome("Tomorrowland Brasil 2027 - Atualizado");
        request.setEvento("Tomorrowland Brasil 2027");
        request.setDataInicio(LocalDate.of(2027, 4, 29));
        request.setDataFim(LocalDate.of(2027, 5, 4));
        request.setEndereco("Estrada dos Engenheiros");
        request.setCidade("Alumnio");
        request.setEstado("SP");
        request.setStatus(StatusViagem.PLANEJADA);

        Viagem viagemExistente = new Viagem();
        viagemExistente.setId(id);
        viagemExistente.setNome("Tomorrowland Brasil 2027");

        ViagemResponse responseEsperado = new ViagemResponse();
        responseEsperado.setId(id);
        responseEsperado.setNome("Tomorrowland Brasil 2027 - Atualizado");


        when(viagemRepository.findById(id))
                .thenReturn(Optional.of(viagemExistente));

        when(viagemRepository.save(viagemExistente))
                .thenReturn(viagemExistente);

        when(viagemMapper.paraResponse(viagemExistente))
                .thenReturn(responseEsperado);

        ViagemResponse resultado =
                viagemService.atualizarViagem(id, request);

        assertEquals(1L, resultado.getId());
        assertEquals(
                "Tomorrowland Brasil 2027 - Atualizado",
                resultado.getNome()
        );

        verify(viagemRepository).findById(id);
        verify(viagemMapper).atualizaEntidade(request, viagemExistente);
        verify(viagemRepository).save(viagemExistente);
        verify(viagemMapper).paraResponse(viagemExistente);

    }

    @Test
    void deveLancarExcecaoAoAtualizarViagemExistente() {

        Long id = 1L;

        ViagemRequest request = new ViagemRequest();
        request.setNome("Tomorrowland Brasil 2027");
        request.setEvento("Tomorrowland Brasil 2027");
        request.setDataInicio(LocalDate.of(2027, 4, 29));
        request.setDataFim(LocalDate.of(2027, 5, 3));
        request.setStatus(StatusViagem.PLANEJADA);

        when(viagemRepository.findById(id))
                .thenReturn(Optional.empty());

        ViagemNaoEncontradaException exception = assertThrows(ViagemNaoEncontradaException.class,
                () -> viagemService.atualizarViagem(id, request)
        );

        assertEquals(
                "Viagem não encontrada!",
                exception.getMessage()
        );

        verify(viagemRepository).findById(id);

        verify(viagemMapper, never())
                .atualizaEntidade(any(), any());

        verify(viagemRepository, never())
                .save(any());

    }

    @Test
    void deveLancarExcecaoAoAtualizarViagemComDataInvalida() {

        Long id = 1L;

        ViagemRequest request = new ViagemRequest();
        request.setNome("Tomorrowland Brasil 2027");
        request.setEvento("Tomorrowland Brasil 2027");
        request.setDataInicio(LocalDate.of(2027, 5, 3));
        request.setDataFim(LocalDate.of(2027, 4, 29));
        request.setStatus(StatusViagem.PLANEJADA);

        Viagem viagemExistente = new Viagem();
        viagemExistente.setId(id);

        when(viagemRepository.findById(id))
                .thenReturn(Optional.of(viagemExistente));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> viagemService.atualizarViagem(id, request)
        );

        assertEquals(
                "A data final da viagem não pode ser anterior à data inicial.",
                exception.getMessage()
        );

        verify(viagemRepository).findById(id);

        verify(viagemMapper, never())
                .atualizaEntidade(any(), any());

        verify(viagemRepository, never())
                .save(any());

    }

    @Test
    void deveDeletarViagemComSucesso() {

        Long id = 1L;

        Viagem viagem =  new Viagem();
        viagem.setId(id);

        when(viagemRepository.findById(id))
                .thenReturn(Optional.of(viagem));

        viagemService.deletarViagem(id);

        verify(viagemRepository).findById(id);
        verify(viagemRepository).delete(viagem);
    }

    @Test
    void deveLancarExcecaoAoDeletarViagemInexistente() {

        Long id = 1L;

        when(viagemRepository.findById(id))
                .thenReturn(Optional.empty());

        ViagemNaoEncontradaException exception  = assertThrows(
                ViagemNaoEncontradaException.class,
                () -> viagemService.deletarViagem(id)
        );

        verify(viagemRepository).findById(id);
        verify(viagemRepository, never()).delete(any());
    }
}



