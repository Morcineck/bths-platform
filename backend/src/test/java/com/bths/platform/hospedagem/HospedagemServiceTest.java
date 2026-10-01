package com.bths.platform.hospedagem;

import com.bths.platform.hospedagem.dto.HospedagemRequest;
import com.bths.platform.hospedagem.dto.HospedagemResponse;
import com.bths.platform.hospedagem.exception.HospedagemNaoEncontradaException;
import com.bths.platform.hospedagem.mapper.HospedagemMapper;
import com.bths.platform.viagem.Viagem;
import com.bths.platform.viagem.ViagemRepository;
import com.bths.platform.viagem.exception.ViagemNaoEncontradaException;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HospedagemServiceTest {

    @Mock
    private HospedagemRepository hospedagemRepository;

    @Mock
    private ViagemRepository viagemRepository;

    @Mock
    private HospedagemMapper hospedagemMapper;

    private HospedagemService hospedagemService;

    @BeforeEach
    void setUp() {

        hospedagemService =
                new HospedagemService(
                        hospedagemRepository,
                        viagemRepository,
                        hospedagemMapper
                );
    }

    @Test
    void deveCadastrarHospedagemComSucesso() {

        Long viagemId = 1L;

        Viagem viagem = new Viagem();
        viagem.setId(viagemId);
        viagem.setNome(
                "Tomorrowland Brasil 2027"
        );

        HospedagemRequest request =
                new HospedagemRequest();

        request.setNome(
                "Chácara Beat Trips"
        );
        request.setEndereco(
                "Estrada Exemplo, 100"
        );
        request.setCidade(
                "Alumínio"
        );
        request.setEstado(
                "SP"
        );
        request.setViagemId(
                viagemId
        );

        Hospedagem hospedagemSalva =
                new Hospedagem();

        hospedagemSalva.setId(10L);
        hospedagemSalva.setNome(
                "Chácara Beat Trips"
        );
        hospedagemSalva.setEndereco(
                "Estrada Exemplo, 100"
        );
        hospedagemSalva.setCidade(
                "Alumínio"
        );
        hospedagemSalva.setEstado(
                "SP"
        );
        hospedagemSalva.setViagem(
                viagem
        );

        HospedagemResponse responseEsperado =
                new HospedagemResponse();

        responseEsperado.setId(10L);
        responseEsperado.setNome(
                "Chácara Beat Trips"
        );
        responseEsperado.setEndereco(
                "Estrada Exemplo, 100"
        );
        responseEsperado.setCidade(
                "Alumínio"
        );
        responseEsperado.setEstado(
                "SP"
        );
        responseEsperado.setViagemId(
                viagemId
        );
        responseEsperado.setViagemNome(
                "Tomorrowland Brasil 2027"
        );

        when(
                viagemRepository
                        .findById(viagemId)
        ).thenReturn(
                Optional.of(viagem)
        );

        when(
                hospedagemRepository
                        .save(
                                any(Hospedagem.class)
                        )
        ).thenReturn(
                hospedagemSalva
        );

        when(
                hospedagemMapper
                        .paraResponse(
                                hospedagemSalva
                        )
        ).thenReturn(
                responseEsperado
        );

        HospedagemResponse resultado =
                hospedagemService
                        .cadastrarHospedagem(
                                request
                        );

        assertEquals(
                10L,
                resultado.getId()
        );

        assertEquals(
                "Chácara Beat Trips",
                resultado.getNome()
        );

        assertEquals(
                viagemId,
                resultado.getViagemId()
        );

        verify(
                viagemRepository
        ).findById(
                viagemId
        );

        verify(
                hospedagemRepository
        ).save(
                any(Hospedagem.class)
        );

        verify(
                hospedagemMapper
        ).paraResponse(
                hospedagemSalva
        );
    }

    @Test
    void deveLancarExcecaoQuandoViagemNaoExistirAoCadastrarHospedagem() {

        Long viagemId = 999L;

        HospedagemRequest request =
                new HospedagemRequest();

        request.setNome(
                "Chácara Beat Trips"
        );

        request.setViagemId(
                viagemId
        );

        when(
                viagemRepository
                        .findById(viagemId)
        ).thenReturn(
                Optional.empty()
        );

        ViagemNaoEncontradaException exception =
                assertThrows(
                        ViagemNaoEncontradaException.class,
                        () ->
                                hospedagemService
                                        .cadastrarHospedagem(
                                                request
                                        )
                );

        assertEquals(
                "Viagem não encontrada",
                exception.getMessage()
        );

        verify(
                viagemRepository
        ).findById(
                viagemId
        );

        verify(
                hospedagemRepository,
                never()
        ).save(
                any(Hospedagem.class)
        );
    }

    @Test
    void deveBuscarHospedagemPorIdComSucesso() {

        Long hospedagemId = 10L;

        Hospedagem hospedagem =
                new Hospedagem();

        hospedagem.setId(
                hospedagemId
        );

        HospedagemResponse responseEsperado =
                new HospedagemResponse();

        responseEsperado.setId(
                hospedagemId
        );

        responseEsperado.setNome(
                "Chácara Beat Trips"
        );

        when(
                hospedagemRepository
                        .findById(
                                hospedagemId
                        )
        ).thenReturn(
                Optional.of(
                        hospedagem
                )
        );

        when(
                hospedagemMapper
                        .paraResponse(
                                hospedagem
                        )
        ).thenReturn(
                responseEsperado
        );

        HospedagemResponse resultado =
                hospedagemService
                        .buscarHospedagemPorId(
                                hospedagemId
                        );

        assertEquals(
                hospedagemId,
                resultado.getId()
        );

        assertEquals(
                "Chácara Beat Trips",
                resultado.getNome()
        );

        verify(
                hospedagemRepository
        ).findById(
                hospedagemId
        );

        verify(
                hospedagemMapper
        ).paraResponse(
                hospedagem
        );
    }

    @Test
    void deveLancarExcecaoQuandoHospedagemNaoForEncontradaPorId() {

        Long hospedagemId = 999L;

        when(
                hospedagemRepository
                        .findById(
                                hospedagemId
                        )
        ).thenReturn(
                Optional.empty()
        );

        HospedagemNaoEncontradaException exception =
                assertThrows(
                        HospedagemNaoEncontradaException.class,
                        () ->
                                hospedagemService
                                        .buscarHospedagemPorId(
                                                hospedagemId
                                        )
                );

        assertEquals(
                "Hospedagem não encontrada!",
                exception.getMessage()
        );

        verify(
                hospedagemRepository
        ).findById(
                hospedagemId
        );

        verify(
                hospedagemMapper,
                never()
        ).paraResponse(
                any(Hospedagem.class)
        );
    }

    @Test
    void deveListarHospedagensComSucesso() {

        Hospedagem hospedagem1 =
                new Hospedagem();

        hospedagem1.setId(1L);
        hospedagem1.setNome(
                "Chácara Beat Trips"
        );

        Hospedagem hospedagem2 =
                new Hospedagem();

        hospedagem2.setId(2L);
        hospedagem2.setNome(
                "Hotel Parceiro"
        );

        HospedagemResponse response1 =
                new HospedagemResponse();

        response1.setId(1L);
        response1.setNome(
                "Chácara Beat Trips"
        );

        HospedagemResponse response2 =
                new HospedagemResponse();

        response2.setId(2L);
        response2.setNome(
                "Hotel Parceiro"
        );

        when(
                hospedagemRepository
                        .findAll()
        ).thenReturn(
                List.of(
                        hospedagem1,
                        hospedagem2
                )
        );

        when(
                hospedagemMapper
                        .paraResponse(
                                hospedagem1
                        )
        ).thenReturn(
                response1
        );

        when(
                hospedagemMapper
                        .paraResponse(
                                hospedagem2
                        )
        ).thenReturn(
                response2
        );

        List<HospedagemResponse> resultado =
                hospedagemService
                        .listarHospedagens();

        assertEquals(
                2,
                resultado.size()
        );

        assertEquals(
                "Chácara Beat Trips",
                resultado
                        .get(0)
                        .getNome()
        );

        assertEquals(
                "Hotel Parceiro",
                resultado
                        .get(1)
                        .getNome()
        );

        verify(
                hospedagemRepository
        ).findAll();

        verify(
                hospedagemMapper
        ).paraResponse(
                hospedagem1
        );

        verify(
                hospedagemMapper
        ).paraResponse(
                hospedagem2
        );
    }

    @Test
    void deveListarHospedagensPorViagemComSucesso() {

        Long viagemId = 1L;

        Viagem viagem =
                new Viagem();

        viagem.setId(
                viagemId
        );

        Hospedagem hospedagem1 =
                new Hospedagem();

        hospedagem1.setId(1L);
        hospedagem1.setNome(
                "Chácara Beat Trips"
        );
        hospedagem1.setViagem(
                viagem
        );

        Hospedagem hospedagem2 =
                new Hospedagem();

        hospedagem2.setId(2L);
        hospedagem2.setNome(
                "Hotel Parceiro"
        );
        hospedagem2.setViagem(
                viagem
        );

        HospedagemResponse response1 =
                new HospedagemResponse();

        response1.setId(1L);
        response1.setNome(
                "Chácara Beat Trips"
        );

        HospedagemResponse response2 =
                new HospedagemResponse();

        response2.setId(2L);
        response2.setNome(
                "Hotel Parceiro"
        );

        when(
                viagemRepository
                        .findById(
                                viagemId
                        )
        ).thenReturn(
                Optional.of(
                        viagem
                )
        );

        when(
                hospedagemRepository
                        .findByViagemId(
                                viagemId
                        )
        ).thenReturn(
                List.of(
                        hospedagem1,
                        hospedagem2
                )
        );

        when(
                hospedagemMapper
                        .paraResponse(
                                hospedagem1
                        )
        ).thenReturn(
                response1
        );

        when(
                hospedagemMapper
                        .paraResponse(
                                hospedagem2
                        )
        ).thenReturn(
                response2
        );

        List<HospedagemResponse> resultado =
                hospedagemService
                        .listarHospedagensPorViagem(
                                viagemId
                        );

        assertEquals(
                2,
                resultado.size()
        );

        assertEquals(
                "Chácara Beat Trips",
                resultado
                        .get(0)
                        .getNome()
        );

        assertEquals(
                "Hotel Parceiro",
                resultado
                        .get(1)
                        .getNome()
        );

        verify(
                viagemRepository
        ).findById(
                viagemId
        );

        verify(
                hospedagemRepository
        ).findByViagemId(
                viagemId
        );
    }

    @Test
    void deveAtualizarHospedagemComSucesso() {

        Long hospedagemId = 10L;
        Long viagemId = 1L;

        Viagem viagem = new Viagem();
        viagem.setId(viagemId);
        viagem.setNome("Tomorrowland Brasil 2027");

        Hospedagem hospedagemExistente =
                new Hospedagem();

        hospedagemExistente.setId(
                hospedagemId
        );

        hospedagemExistente.setNome(
                "Hospedagem Antiga"
        );

        hospedagemExistente.setViagem(
                viagem
        );

        HospedagemRequest request =
                new HospedagemRequest();

        request.setNome(
                "Chácara Beat Trips"
        );

        request.setViagemId(
                viagemId
        );

        HospedagemResponse responseEsperado =
                new HospedagemResponse();

        responseEsperado.setId(
                hospedagemId
        );

        responseEsperado.setNome(
                "Chácara Beat Trips"
        );

        responseEsperado.setViagemId(
                viagemId
        );

        when(
                hospedagemRepository
                        .findById(
                                hospedagemId
                        )
        ).thenReturn(
                Optional.of(
                        hospedagemExistente
                )
        );

        when(
                viagemRepository
                        .findById(
                                viagemId
                        )
        ).thenReturn(
                Optional.of(
                        viagem
                )
        );

        when(
                hospedagemRepository
                        .save(
                                hospedagemExistente
                        )
        ).thenReturn(
                hospedagemExistente
        );

        when(
                hospedagemMapper
                        .paraResponse(
                                hospedagemExistente
                        )
        ).thenReturn(
                responseEsperado
        );

        HospedagemResponse resultado =
                hospedagemService
                        .atualizarHospedagem(
                                hospedagemId,
                                request
                        );

        assertEquals(
                hospedagemId,
                resultado.getId()
        );

        assertEquals(
                "Chácara Beat Trips",
                resultado.getNome()
        );

        verify(
                hospedagemRepository
        ).findById(
                hospedagemId
        );

        verify(
                viagemRepository
        ).findById(
                viagemId
        );

        verify(
                hospedagemMapper
        ).atualizarEntidade(
                hospedagemExistente,
                request
        );

        verify(
                hospedagemRepository
        ).save(
                hospedagemExistente
        );
    }

    @Test
    void deveDeletarHospedagemComSucesso() {

        Long hospedagemId = 10L;

        Hospedagem hospedagem =
                new Hospedagem();

        hospedagem.setId(
                hospedagemId
        );

        when(
                hospedagemRepository
                        .findById(
                                hospedagemId
                        )
        ).thenReturn(
                Optional.of(
                        hospedagem
                )
        );

        hospedagemService
                .deletarHospedagem(
                        hospedagemId
                );

        verify(
                hospedagemRepository
        ).findById(
                hospedagemId
        );

        verify(
                hospedagemRepository
        ).delete(
                hospedagem
        );
    }

    @Test
    void deveLancarExcecaoAoAtualizarHospedagemInexistente() {

        Long hospedagemId = 999L;

        HospedagemRequest request =
                new HospedagemRequest();

        request.setNome(
                "Chácara Beat Trips"
        );

        request.setViagemId(
                1L
        );

        when(
                hospedagemRepository
                        .findById(
                                hospedagemId
                        )
        ).thenReturn(
                Optional.empty()
        );

        HospedagemNaoEncontradaException exception =
                assertThrows(
                        HospedagemNaoEncontradaException.class,
                        () ->
                                hospedagemService
                                        .atualizarHospedagem(
                                                hospedagemId,
                                                request
                                        )
                );

        assertEquals(
                "Hospedagem não encontrada!",
                exception.getMessage()
        );

        verify(
                hospedagemRepository
        ).findById(
                hospedagemId
        );

        verify(
                viagemRepository,
                never()
        ).findById(
                any()
        );

        verify(
                hospedagemRepository,
                never()
        ).save(
                any(Hospedagem.class)
        );
    }

    @Test
    void deveLancarExcecaoAoDeletarHospedagemInexistente() {

        Long hospedagemId = 999L;

        when(
                hospedagemRepository
                        .findById(
                                hospedagemId
                        )
        ).thenReturn(
                Optional.empty()
        );

        HospedagemNaoEncontradaException exception =
                assertThrows(
                        HospedagemNaoEncontradaException.class,
                        () ->
                                hospedagemService
                                        .deletarHospedagem(
                                                hospedagemId
                                        )
                );

        assertEquals(
                "Hospedagem não encontrada!",
                exception.getMessage()
        );

        verify(
                hospedagemRepository
        ).findById(
                hospedagemId
        );

        verify(
                hospedagemRepository,
                never()
        ).delete(
                any(Hospedagem.class)
        );
    }



}