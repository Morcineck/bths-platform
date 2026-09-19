package com.bths.platform.hospede;

import com.bths.platform.hospede.exception.HospedeJaCadastradoException;
import com.bths.platform.hospede.exception.HospedeNaoEncontradoException;
import com.bths.platform.viagem.exception.ViagemNaoEncontradaException;
import com.bths.platform.hospede.dto.HospedeRequest;
import com.bths.platform.hospede.dto.HospedeResponse;
import com.bths.platform.hospede.enums.StatusCheckIn;
import com.bths.platform.hospede.mapper.HospedeMapper;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HospedeServiceTest {

    @Mock
    private HospedeRepository hospedeRepository;

    @Mock
    private ViagemRepository viagemRepository;

    @Mock
    private HospedeMapper hospedeMapper;

    private HospedeService hospedeService;

    @BeforeEach
    void setUp() {

        hospedeService = new HospedeService(
                hospedeRepository,
                viagemRepository,
                hospedeMapper
        );
    }

    @Test
    void deveCadastrarHospedeComSucesso() {

        Long viagemId = 1L;

        HospedeRequest request = new HospedeRequest();
        request.setNomeCompleto("Maria Oliveira");
        request.setCpf("98765432100");
        request.setViagemId(viagemId);
        request.setStatusCheckIn(StatusCheckIn.PENDENTE);

        Viagem viagem = new Viagem();
        viagem.setId(viagemId);
        viagem.setNome("Tomorrowland Brasil 2027");

        Hospede hospede = new Hospede();
        hospede.setNomeCompleto("Maria Oliveira");
        hospede.setCpf("98765432100");
        hospede.setStatusCheckIn(StatusCheckIn.PENDENTE);
        hospede.setViagem(viagem);

        Hospede hospedeSalvo = new Hospede();
        hospedeSalvo.setId(1L);
        hospedeSalvo.setNomeCompleto("Maria Oliveira");
        hospedeSalvo.setCpf("98765432100");
        hospedeSalvo.setStatusCheckIn(StatusCheckIn.PENDENTE);
        hospedeSalvo.setViagem(viagem);

        HospedeResponse responseEsperado = new HospedeResponse();
        responseEsperado.setId(1L);
        responseEsperado.setNomeCompleto("Maria Oliveira");
        responseEsperado.setCpf("98765432100");
        responseEsperado.setStatusCheckIn(StatusCheckIn.PENDENTE);
        responseEsperado.setViagemId(viagemId);
        responseEsperado.setViagemNome("Tomorrowland Brasil 2027");

        when(viagemRepository.findById(viagemId))
                .thenReturn(Optional.of(viagem));

        when(hospedeRepository.existsByCpfAndViagemId(
                request.getCpf(),
                viagemId
        )).thenReturn(false);

        when(hospedeRepository.save(any(Hospede.class)))
                .thenReturn(hospedeSalvo);

        when(hospedeMapper.paraResponse(hospedeSalvo))
                .thenReturn(responseEsperado);

        HospedeResponse resultado =
                hospedeService.cadastrarHospede(request);

        assertEquals(1L, resultado.getId());
        assertEquals("Maria Oliveira", resultado.getNomeCompleto());
        assertEquals("98765432100", resultado.getCpf());
        assertEquals(StatusCheckIn.PENDENTE, resultado.getStatusCheckIn());
        assertEquals(1L, resultado.getViagemId());

        verify(viagemRepository).findById(viagemId);

        verify(hospedeRepository)
                .existsByCpfAndViagemId("98765432100", viagemId);

        verify(hospedeMapper)
                .atualizarEntidade(any(Hospede.class), eq(request));

        verify(hospedeRepository)
                .save(any(Hospede.class));

        verify(hospedeMapper)
                .paraResponse(hospedeSalvo);
    }

    @Test
    void deveLancarExcecaoQuandoViagemNaoExistir() {

        Long viagemId = 999L;

        HospedeRequest request = new HospedeRequest();
        request.setNomeCompleto("Maria Oliveira");
        request.setCpf("98765432100");
        request.setViagemId(viagemId);
        request.setStatusCheckIn(StatusCheckIn.PENDENTE);

        when(viagemRepository.findById(viagemId))
                .thenReturn(Optional.empty());

        assertThrows(
                ViagemNaoEncontradaException.class,
                () -> hospedeService.cadastrarHospede(request)
        );

        verify(viagemRepository).findById(viagemId);

        verify(hospedeRepository, never())
                .existsByCpfAndViagemId(anyString(), anyLong());

        verify(hospedeRepository, never())
                .save(any(Hospede.class));
    }

    @Test
    void deveLancarExcecaoQuandoCpfJaEstiverCadastradoNaViagem() {

        Long viagemId = 1L;

        HospedeRequest request = new HospedeRequest();
        request.setNomeCompleto("Maria Oliveira");
        request.setCpf("98765432100");
        request.setViagemId(viagemId);
        request.setStatusCheckIn(StatusCheckIn.PENDENTE);

        Viagem viagem = new Viagem();
        viagem.setId(viagemId);
        viagem.setNome("Tomorrowland Brasil 2027");

        when(viagemRepository.findById(viagemId))
                .thenReturn(Optional.of(viagem));

        when(hospedeRepository.existsByCpfAndViagemId(
                request.getCpf(),
                viagemId
        )).thenReturn(true);

        assertThrows(
                HospedeJaCadastradoException.class,
                () -> hospedeService.cadastrarHospede(request)
        );

        verify(viagemRepository).findById(viagemId);

        verify(hospedeRepository)
                .existsByCpfAndViagemId(
                        "98765432100",
                        viagemId
                );

        verify(hospedeRepository, never())
                .save(any(Hospede.class));

        verify(hospedeMapper, never())
                .paraResponse(any(Hospede.class));
    }

    @Test
    void deveBuscarHospedePorIdPorIdComSucesso() {

        Long id = 1L;

        Hospede hospede = new Hospede();
        hospede.setId(id);
        hospede.setNomeCompleto("Maria Oliveira");
        hospede.setCpf("98765432100");
        hospede.setStatusCheckIn(StatusCheckIn.PENDENTE);

        HospedeResponse responseEsperado = new HospedeResponse();
        responseEsperado.setId(id);
        responseEsperado.setNomeCompleto("Maria Oliveira");
        responseEsperado.setCpf("98765432100");
        responseEsperado.setStatusCheckIn(StatusCheckIn.PENDENTE);

        when(hospedeRepository.findById(id))
                .thenReturn(Optional.of(hospede));

        when(hospedeMapper.paraResponse(hospede))
                .thenReturn(responseEsperado);

        HospedeResponse resultado =
                hospedeService.buscarHospedePorId(id);

        assertEquals(1L, resultado.getId());
        assertEquals("Maria Oliveira", resultado.getNomeCompleto());
        assertEquals("98765432100", resultado.getCpf());
        assertEquals(StatusCheckIn.PENDENTE, resultado.getStatusCheckIn());

        verify(hospedeRepository).findById(id);
        verify(hospedeMapper).paraResponse(hospede);
    }

    @Test
    void deveLancarExcecaoQuandoHospedeNaoForEncontrado() {

        Long id = 999L;

        when(hospedeRepository.findById(id))
                .thenReturn(Optional.empty());

        HospedeNaoEncontradoException exception = assertThrows(
                HospedeNaoEncontradoException.class,
                () -> hospedeService.buscarHospedePorId(id)
        );

        assertEquals(
                "Hóspede não encontrado!",
                exception.getMessage()
        );

        verify(hospedeRepository).findById(id);

        verify(hospedeMapper, never())
                .paraResponse(any(Hospede.class));
    }

    @Test
    void deveListarHospedesComSucesso() {

        Hospede hospede1 = new Hospede();
        hospede1.setId(1L);
        hospede1.setNomeCompleto("João da Silva");

        Hospede hospede2 = new Hospede();
        hospede2.setId(2L);
        hospede2.setNomeCompleto("Maria Oliveira");

        HospedeResponse response1 = new HospedeResponse();
        response1.setId(1L);
        response1.setNomeCompleto("João da Silva");

        HospedeResponse response2 = new HospedeResponse();
        response2.setId(2L);
        response2.setNomeCompleto("Maria Oliveira");

        when(hospedeRepository.findAll())
                .thenReturn(List.of(hospede1, hospede2));

        when(hospedeMapper.paraResponse(hospede1))
                .thenReturn(response1);

        when(hospedeMapper.paraResponse(hospede2))
                .thenReturn(response2);

        List<HospedeResponse> resultado =
                hospedeService.listarHospedes();

        assertEquals(2, resultado.size());
        assertEquals("João da Silva", resultado.get(0).getNomeCompleto());
        assertEquals("Maria Oliveira", resultado.get(1).getNomeCompleto());

        verify(hospedeRepository).findAll();
        verify(hospedeMapper).paraResponse(hospede1);
        verify(hospedeMapper).paraResponse(hospede2);
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoExistiremHospedes() {

        when(hospedeRepository.findAll())
                .thenReturn(List.of());

        List<HospedeResponse> resultado =
                hospedeService.listarHospedes();

        assertEquals(0, resultado.size());

        verify(hospedeRepository).findAll();

        verify(hospedeMapper, never())
                .paraResponse(any(Hospede.class));
    }

    @Test
    void deveAtualizarHospedeComSucesso() {

        Long hospedeId = 1L;
        Long viagemId = 1L;

        HospedeRequest request = new HospedeRequest();
        request.setNomeCompleto("Maria Oliveira Santos");
        request.setCpf("98765432100");
        request.setViagemId(viagemId);
        request.setStatusCheckIn(StatusCheckIn.REALIZADO);

        Viagem viagem = new Viagem();
        viagem.setId(viagemId);
        viagem.setNome("Tomorrowland Brasil 2027");

        Hospede hospede = new Hospede();
        hospede.setId(hospedeId);
        hospede.setNomeCompleto("Maria Oliveira");
        hospede.setCpf("98765432100");
        hospede.setViagem(viagem);

        HospedeResponse responseEsperado = new HospedeResponse();
        responseEsperado.setId(hospedeId);
        responseEsperado.setNomeCompleto("Maria Oliveira Santos");
        responseEsperado.setCpf("98765432100");
        responseEsperado.setStatusCheckIn(StatusCheckIn.REALIZADO);
        responseEsperado.setViagemId(viagemId);

        when(hospedeRepository.findById(hospedeId))
                .thenReturn(Optional.of(hospede));

        when(viagemRepository.findById(viagemId))
                .thenReturn(Optional.of(viagem));

        when(hospedeRepository.existsByCpfAndViagemIdAndIdNot(
                request.getCpf(),
                viagemId,
                hospedeId
        )).thenReturn(false);

        when(hospedeRepository.save(hospede))
                .thenReturn(hospede);

        when(hospedeMapper.paraResponse(hospede))
                .thenReturn(responseEsperado);

        HospedeResponse resultado =
                hospedeService.atualizarHospede(hospedeId, request);

        assertEquals(1L, resultado.getId());
        assertEquals(
                "Maria Oliveira Santos",
                resultado.getNomeCompleto()
        );
        assertEquals(
                StatusCheckIn.REALIZADO,
                resultado.getStatusCheckIn()
        );

        verify(hospedeRepository).findById(hospedeId);
        verify(viagemRepository).findById(viagemId);

        verify(hospedeRepository)
                .existsByCpfAndViagemIdAndIdNot(
                        "98765432100",
                        viagemId,
                        hospedeId
                );

        verify(hospedeMapper)
                .atualizarEntidade(hospede, request);

        verify(hospedeRepository).save(hospede);
        verify(hospedeMapper).paraResponse(hospede);
    }

    @Test
    void deveLancarExcecaoAoAtualizarHospedeInexistente() {

        Long hospedeId = 999L;

        HospedeRequest request = new HospedeRequest();
        request.setNomeCompleto("Maria Oliveira");
        request.setCpf("98765432100");
        request.setViagemId(1L);
        request.setStatusCheckIn(StatusCheckIn.PENDENTE);

        when(hospedeRepository.findById(hospedeId))
                .thenReturn(Optional.empty());

        HospedeNaoEncontradoException exception = assertThrows(
                HospedeNaoEncontradoException.class,
                () -> hospedeService.atualizarHospede(hospedeId, request)
        );

        assertEquals(
                "Hóspede não encontrado!",
                exception.getMessage()
        );

        verify(hospedeRepository).findById(hospedeId);

        verify(viagemRepository, never())
                .findById(anyLong());

        verify(hospedeRepository, never())
                .save(any(Hospede.class));
    }

    @Test
    void deveLancarExcecaoAoAtualizarHospedeComViagemInexistente() {

        Long hospedeId = 1L;
        Long viagemId = 999L;

        HospedeRequest request = new HospedeRequest();
        request.setNomeCompleto("Maria Oliveira");
        request.setCpf("98765432100");
        request.setViagemId(viagemId);
        request.setStatusCheckIn(StatusCheckIn.PENDENTE);

        Hospede hospede = new Hospede();
        hospede.setId(hospedeId);
        hospede.setNomeCompleto("Maria Oliveira");

        when(hospedeRepository.findById(hospedeId))
                .thenReturn(Optional.of(hospede));

        when(viagemRepository.findById(viagemId))
                .thenReturn(Optional.empty());

        ViagemNaoEncontradaException exception = assertThrows(
                ViagemNaoEncontradaException.class,
                () -> hospedeService.atualizarHospede(hospedeId, request)
        );

        assertEquals(
                "Viagem não encontrada!",
                exception.getMessage()
        );

        verify(hospedeRepository).findById(hospedeId);
        verify(viagemRepository).findById(viagemId);

        verify(hospedeRepository, never())
                .existsByCpfAndViagemIdAndIdNot(
                        anyString(),
                        anyLong(),
                        anyLong()
                );

        verify(hospedeRepository, never())
                .save(any(Hospede.class));
    }

    @Test
    void deveLancarExcecaoAoAtualizarHospedeComCpfJaCadastradoNaViagem() {

        Long hospedeId = 1L;
        Long viagemId = 1L;

        HospedeRequest request = new HospedeRequest();
        request.setNomeCompleto("Maria Oliveira");
        request.setCpf("12345678900");
        request.setViagemId(viagemId);
        request.setStatusCheckIn(StatusCheckIn.PENDENTE);

        Hospede hospede = new Hospede();
        hospede.setId(hospedeId);

        Viagem viagem = new Viagem();
        viagem.setId(viagemId);

        when(hospedeRepository.findById(hospedeId))
                .thenReturn(Optional.of(hospede));

        when(viagemRepository.findById(viagemId))
                .thenReturn(Optional.of(viagem));

        when(hospedeRepository.existsByCpfAndViagemIdAndIdNot(
                request.getCpf(),
                viagemId,
                hospedeId
        )).thenReturn(true);

        HospedeJaCadastradoException exception = assertThrows(
                HospedeJaCadastradoException.class,
                () -> hospedeService.atualizarHospede(hospedeId, request)
        );

        assertEquals(
                "CPF já cadastrado nesta viagem!",
                exception.getMessage()
        );

        verify(hospedeRepository).findById(hospedeId);
        verify(viagemRepository).findById(viagemId);

        verify(hospedeRepository)
                .existsByCpfAndViagemIdAndIdNot(
                        "12345678900",
                        viagemId,
                        hospedeId
                );

        verify(hospedeMapper, never())
                .atualizarEntidade(any(), any());

        verify(hospedeRepository, never())
                .save(any(Hospede.class));
    }

    @Test
    void deveDeletarHospedeComSucesso() {

        Long id = 1L;

        Hospede hospede = new Hospede();
        hospede.setId(id);
        hospede.setNomeCompleto("Maria Oliveira");

        when(hospedeRepository.findById(id))
                .thenReturn(Optional.of(hospede));

        hospedeService.deletarHospede(id);

        verify(hospedeRepository).findById(id);
        verify(hospedeRepository).delete(hospede);
    }

    @Test
    void deveLancarExcecaoAoDeletarHospedeInexistente() {

        Long id = 999L;

        when(hospedeRepository.findById(id))
                .thenReturn(Optional.empty());

        HospedeNaoEncontradoException exception = assertThrows(
                HospedeNaoEncontradoException.class,
                () -> hospedeService.deletarHospede(id)
        );

        assertEquals(
                "Hóspede não encontrado!",
                exception.getMessage()
        );

        verify(hospedeRepository).findById(id);

        verify(hospedeRepository, never())
                .delete(any(Hospede.class));
    }
}