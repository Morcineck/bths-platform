package com.bths.platform.hospede;

import com.bths.platform.hospede.dto.HospedeAcessoBthsResponse;
import com.bths.platform.hospede.dto.HospedeCriarAcessoBthsRequest;
import com.bths.platform.hospede.dto.HospedeRequest;
import com.bths.platform.hospede.dto.HospedeResponse;
import com.bths.platform.hospede.enums.StatusCheckIn;
import com.bths.platform.hospede.exception.HospedeJaCadastradoException;
import com.bths.platform.hospede.exception.HospedeJaVinculadoException;
import com.bths.platform.hospede.exception.HospedeNaoEncontradoException;
import com.bths.platform.hospede.mapper.HospedeMapper;
import com.bths.platform.usuario.Usuario;
import com.bths.platform.usuario.UsuarioRepository;
import com.bths.platform.usuario.enums.PerfilUsuario;
import com.bths.platform.usuario.exception.UsuarioNaoEncontradoException;
import com.bths.platform.viagem.Viagem;
import com.bths.platform.viagem.ViagemRepository;
import com.bths.platform.viagem.exception.ViagemNaoEncontradaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
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

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {

        hospedeService = new HospedeService(
                hospedeRepository,
                viagemRepository,
                hospedeMapper,
                usuarioRepository,
                passwordEncoder
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
        hospedeSalvo.setCodigoCheckIn("codigo-teste-123");
        hospedeSalvo.setId(1L);
        hospedeSalvo.setNomeCompleto("Maria Oliveira");
        hospedeSalvo.setCpf("98765432100");
        hospedeSalvo.setStatusCheckIn(StatusCheckIn.PENDENTE);
        hospedeSalvo.setViagem(viagem);

        HospedeResponse responseEsperado = new HospedeResponse();
        responseEsperado.setCodigoCheckIn("codigo-teste-123");
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

        assertEquals("codigo-teste-123", resultado.getCodigoCheckIn());
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

    @Test
    void deveVincularUsuarioHospedeComSucesso() {

        Long hospedeId = 1L;
        UUID usuarioId = UUID.randomUUID();

        Hospede hospede =
                new Hospede();

        hospede.setId(hospedeId);
        hospede.setNomeCompleto(
                "Lucas Cesar"
        );

        Usuario usuario =
                new Usuario();

        usuario.setId(usuarioId);
        usuario.setNome(
                "Lucas Cesar"
        );
        usuario.setPerfil(
                PerfilUsuario.HOSPEDE
        );
        usuario.setAtivo(true);

        Hospede hospedeAtualizado =
                new Hospede();

        hospedeAtualizado.setId(
                hospedeId
        );
        hospedeAtualizado.setNomeCompleto(
                "Lucas Cesar"
        );
        hospedeAtualizado.setUsuario(
                usuario
        );

        HospedeResponse responseEsperado =
                new HospedeResponse();

        responseEsperado.setId(
                hospedeId
        );
        responseEsperado.setNomeCompleto(
                "Lucas Cesar"
        );

        when(
                hospedeRepository
                        .findById(hospedeId)
        ).thenReturn(
                Optional.of(hospede)
        );

        when(
                usuarioRepository
                        .findById(usuarioId)
        ).thenReturn(
                Optional.of(usuario)
        );

        when(
                hospedeRepository.save(
                        hospede
                )
        ).thenReturn(
                hospedeAtualizado
        );

        when(
                hospedeMapper.paraResponse(
                        hospedeAtualizado
                )
        ).thenReturn(
                responseEsperado
        );

        HospedeResponse resultado =
                hospedeService
                        .vincularUsuarioAoHospede(
                                hospedeId,
                                usuarioId
                        );

        assertNotNull(
                resultado
        );

        assertEquals(
                hospedeId,
                resultado.getId()
        );

        assertEquals(
                usuario,
                hospede.getUsuario()
        );

        verify(
                hospedeRepository
        ).findById(
                hospedeId
        );

        verify(
                usuarioRepository
        ).findById(
                usuarioId
        );

        verify(
                hospedeRepository
        ).save(
                hospede
        );

        verify(
                hospedeMapper
        ).paraResponse(
                hospedeAtualizado
        );
    }

    @Test
    void deveLancarExcecaoQuandoHospedeNaoExistirAoVincularUsuario() {

        Long hospedeId = 999L;
        UUID usuarioId = UUID.randomUUID();

        when(
                hospedeRepository.findById(
                        hospedeId
                )
        ).thenReturn(
                Optional.empty()
        );

        assertThrows(
                HospedeNaoEncontradoException.class,
                () ->
                        hospedeService
                                .vincularUsuarioAoHospede(
                                        hospedeId,
                                        usuarioId
                                )
        );

        verify(
                hospedeRepository
        ).findById(
                hospedeId
        );

        verifyNoInteractions(
                usuarioRepository
        );
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoExistirAoVincularHospede() {

        Long hospedeId = 1L;
        UUID usuarioId = UUID.randomUUID();

        Hospede hospede =
                new Hospede();

        hospede.setId(
                hospedeId
        );

        when(
                hospedeRepository.findById(
                        hospedeId
                )
        ).thenReturn(
                Optional.of(
                        hospede
                )
        );

        when(
                usuarioRepository.findById(
                        usuarioId
                )
        ).thenReturn(
                Optional.empty()
        );

        assertThrows(
                UsuarioNaoEncontradoException.class,
                () ->
                        hospedeService
                                .vincularUsuarioAoHospede(
                                        hospedeId,
                                        usuarioId
                                )
        );

        verify(
                hospedeRepository
        ).findById(
                hospedeId
        );

        verify(
                usuarioRepository
        ).findById(
                usuarioId
        );

        verify(
                hospedeRepository,
                never()
        ).save(
                any(Hospede.class)
        );
    }

    @Test
    void deveBloquearVinculoQuandoUsuarioNaoForHospede() {

        Long hospedeId = 1L;
        UUID usuarioId = UUID.randomUUID();

        Hospede hospede =
                new Hospede();

        hospede.setId(
                hospedeId
        );

        Usuario usuario =
                new Usuario();

        usuario.setId(
                usuarioId
        );

        usuario.setPerfil(
                PerfilUsuario.ADMIN
        );

        when(
                hospedeRepository.findById(
                        hospedeId
                )
        ).thenReturn(
                Optional.of(
                        hospede
                )
        );

        when(
                usuarioRepository.findById(
                        usuarioId
                )
        ).thenReturn(
                Optional.of(
                        usuario
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        hospedeService
                                .vincularUsuarioAoHospede(
                                        hospedeId,
                                        usuarioId
                                )
        );

        verify(
                hospedeRepository,
                never()
        ).save(
                any(Hospede.class)
        );
    }

    @Test
    void deveBloquearNovoVinculoQuandoHospedeJaPossuirUsuario() {

        Long hospedeId = 1L;
        UUID usuarioAtualId =
                UUID.randomUUID();

        UUID novoUsuarioId =
                UUID.randomUUID();

        Usuario usuarioAtual =
                new Usuario();

        usuarioAtual.setId(
                usuarioAtualId
        );

        usuarioAtual.setPerfil(
                PerfilUsuario.HOSPEDE
        );

        Hospede hospede =
                new Hospede();

        hospede.setId(
                hospedeId
        );

        hospede.setUsuario(
                usuarioAtual
        );

        when(
                hospedeRepository.findById(
                        hospedeId
                )
        ).thenReturn(
                Optional.of(
                        hospede
                )
        );

        assertThrows(
                HospedeJaVinculadoException.class,
                () ->
                        hospedeService
                                .vincularUsuarioAoHospede(
                                        hospedeId,
                                        novoUsuarioId
                                )
        );

        verifyNoInteractions(
                usuarioRepository
        );

        verify(
                hospedeRepository,
                never()
        ).save(
                any(Hospede.class)
        );
    }

    @Test
    void deveBuscarApenasHospedesDoUsuarioAutenticado() {

        UUID usuarioId = UUID.randomUUID();
        String email = "hospede@beattrips.com";

        Usuario usuario =
                new Usuario();

        usuario.setId(usuarioId);
        usuario.setEmail(email);
        usuario.setPerfil(
                PerfilUsuario.HOSPEDE
        );

        Hospede hospede2027 =
                new Hospede();

        hospede2027.setId(10L);
        hospede2027.setNomeCompleto(
                "Lucas Cesar"
        );
        hospede2027.setUsuario(
                usuario
        );

        Hospede hospede2028 =
                new Hospede();

        hospede2028.setId(20L);
        hospede2028.setNomeCompleto(
                "Lucas Cesar"
        );
        hospede2028.setUsuario(
                usuario
        );

        HospedeResponse response2027 =
                new HospedeResponse();

        response2027.setId(10L);
        response2027.setNomeCompleto(
                "Lucas Cesar"
        );

        HospedeResponse response2028 =
                new HospedeResponse();

        response2028.setId(20L);
        response2028.setNomeCompleto(
                "Lucas Cesar"
        );

        when(
                usuarioRepository.findByEmail(
                        email
                )
        ).thenReturn(
                Optional.of(
                        usuario
                )
        );

        when(
                hospedeRepository.findByUsuarioId(
                        usuarioId
                )
        ).thenReturn(
                List.of(
                        hospede2027,
                        hospede2028
                )
        );

        when(
                hospedeMapper.paraResponse(
                        hospede2027
                )
        ).thenReturn(
                response2027
        );

        when(
                hospedeMapper.paraResponse(
                        hospede2028
                )
        ).thenReturn(
                response2028
        );

        List<HospedeResponse> resultado =
                hospedeService
                        .buscarHospedesDoUsuario(
                                email
                        );

        assertEquals(
                2,
                resultado.size()
        );

        assertEquals(
                10L,
                resultado.get(0)
                        .getId()
        );

        assertEquals(
                20L,
                resultado.get(1)
                        .getId()
        );

        verify(
                usuarioRepository
        ).findByEmail(
                email
        );

        verify(
                hospedeRepository
        ).findByUsuarioId(
                usuarioId
        );
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioAutenticadoNaoExistir() {

        String email =
                "inexistente@beattrips.com";

        when(
                usuarioRepository.findByEmail(
                        email
                )
        ).thenReturn(
                Optional.empty()
        );

        assertThrows(
                UsuarioNaoEncontradoException.class,
                () ->
                        hospedeService
                                .buscarHospedesDoUsuario(
                                        email
                                )
        );

        verifyNoInteractions(
                hospedeRepository
        );
    }

    @Test
    void deveBloquearConsultaQuandoUsuarioNaoForHospede() {

        String email =
                "admin@beattrips.com";

        Usuario usuario =
                new Usuario();

        usuario.setEmail(
                email
        );

        usuario.setPerfil(
                PerfilUsuario.ADMIN
        );

        when(
                usuarioRepository.findByEmail(
                        email
                )
        ).thenReturn(
                Optional.of(
                        usuario
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        hospedeService
                                .buscarHospedesDoUsuario(
                                        email
                                )
        );

        verifyNoInteractions(
                hospedeRepository
        );
    }

    @Test
    void deveRetornarListaVaziaQuandoUsuarioNaoPossuirHospedeVinculado() {

        UUID usuarioId = UUID.randomUUID();
        String email = "hospede@beattrips.com";

        Usuario usuario =
                new Usuario();

        usuario.setId(
                usuarioId
        );

        usuario.setEmail(
                email
        );

        usuario.setPerfil(
                PerfilUsuario.HOSPEDE
        );

        when(
                usuarioRepository.findByEmail(
                        email
                )
        ).thenReturn(
                Optional.of(
                        usuario
                )
        );

        when(
                hospedeRepository.findByUsuarioId(
                        usuarioId
                )
        ).thenReturn(
                List.of()
        );

        List<HospedeResponse> resultado =
                hospedeService
                        .buscarHospedesDoUsuario(
                                email
                        );

        assertNotNull(
                resultado
        );

        assertTrue(
                resultado.isEmpty()
        );

        verify(
                usuarioRepository
        ).findByEmail(
                email
        );

        verify(
                hospedeRepository
        ).findByUsuarioId(
                usuarioId
        );
    }

    @Test
    void deveRetornarAcessoBthsVinculadoComSucesso() {

        Long hospedeId = 1L;
        UUID usuarioId = UUID.randomUUID();

        Usuario usuario =
                new Usuario();

        usuario.setId(usuarioId);
        usuario.setEmail(
                "hospede@beattrips.com"
        );
        usuario.setAtivo(true);
        usuario.setPerfil(
                PerfilUsuario.HOSPEDE
        );

        Hospede hospede =
                new Hospede();

        hospede.setId(hospedeId);
        hospede.setUsuario(usuario);

        when(
                hospedeRepository.findById(
                        hospedeId
                )
        ).thenReturn(
                Optional.of(
                        hospede
                )
        );

        HospedeAcessoBthsResponse resultado =
                hospedeService
                        .buscarAcessoBths(
                                hospedeId
                        );

        assertNotNull(
                resultado
        );

        assertTrue(
                resultado.isVinculado()
        );

        assertEquals(
                usuarioId,
                resultado.getUsuarioId()
        );

        assertEquals(
                "hospede@beattrips.com",
                resultado.getEmail()
        );

        assertTrue(
                resultado.isAtivo()
        );
    }

    @Test
    void deveRetornarAcessoBthsNaoVinculado() {

        Long hospedeId = 1L;

        Hospede hospede =
                new Hospede();

        hospede.setId(
                hospedeId
        );

        when(
                hospedeRepository.findById(
                        hospedeId
                )
        ).thenReturn(
                Optional.of(
                        hospede
                )
        );

        HospedeAcessoBthsResponse resultado =
                hospedeService
                        .buscarAcessoBths(
                                hospedeId
                        );

        assertNotNull(
                resultado
        );

        assertFalse(
                resultado.isVinculado()
        );

        assertNull(
                resultado.getUsuarioId()
        );

        assertNull(
                resultado.getEmail()
        );

        assertFalse(
                resultado.isAtivo()
        );
    }

    @Test
    void deveLancarExcecaoAoBuscarAcessoBthsDeHospedeInexistente() {

        Long hospedeId = 999L;

        when(
                hospedeRepository.findById(
                        hospedeId
                )
        ).thenReturn(
                Optional.empty()
        );

        assertThrows(
                HospedeNaoEncontradoException.class,
                () ->
                        hospedeService
                                .buscarAcessoBths(
                                        hospedeId
                                )
        );
    }

    @Test
    void deveCriarNovoUsuarioHospedeEVincularAoHospede() {

        Long hospedeId = 1L;
        UUID usuarioId = UUID.randomUUID();

        Hospede hospede =
                new Hospede();

        hospede.setId(hospedeId);
        hospede.setNomeCompleto(
                "Lucas Cesar"
        );

        HospedeCriarAcessoBthsRequest request =
                new HospedeCriarAcessoBthsRequest();

        request.setEmail(
                "lucas@beattrips.com"
        );

        request.setSenhaTemporaria(
                "Senha123!"
        );

        Usuario usuarioSalvo =
                new Usuario();

        usuarioSalvo.setId(
                usuarioId
        );

        usuarioSalvo.setNome(
                "Lucas Cesar"
        );

        usuarioSalvo.setEmail(
                "lucas@beattrips.com"
        );

        usuarioSalvo.setSenha(
                "senha-criptografada"
        );

        usuarioSalvo.setPerfil(
                PerfilUsuario.HOSPEDE
        );

        usuarioSalvo.setAtivo(
                true
        );

        when(
                hospedeRepository.findById(
                        hospedeId
                )
        ).thenReturn(
                Optional.of(
                        hospede
                )
        );

        when(
                usuarioRepository.findByEmail(
                        "lucas@beattrips.com"
                )
        ).thenReturn(
                Optional.empty()
        );

        when(
                passwordEncoder.encode(
                        "Senha123!"
                )
        ).thenReturn(
                "senha-criptografada"
        );

        when(
                usuarioRepository.save(
                        any(Usuario.class)
                )
        ).thenReturn(
                usuarioSalvo
        );

        when(
                hospedeRepository.save(
                        hospede
                )
        ).thenReturn(
                hospede
        );

        HospedeAcessoBthsResponse resultado =
                hospedeService
                        .criarOuVincularAcessoBths(
                                hospedeId,
                                request
                        );

        assertNotNull(
                resultado
        );

        assertTrue(
                resultado.isVinculado()
        );

        assertEquals(
                usuarioId,
                resultado.getUsuarioId()
        );

        assertEquals(
                "lucas@beattrips.com",
                resultado.getEmail()
        );

        assertTrue(
                resultado.isAtivo()
        );

        assertEquals(
                usuarioSalvo,
                hospede.getUsuario()
        );

        verify(
                passwordEncoder
        ).encode(
                "Senha123!"
        );

        verify(
                usuarioRepository
        ).save(
                any(Usuario.class)
        );

        verify(
                hospedeRepository
        ).save(
                hospede
        );
    }

    @Test
    void deveReutilizarUsuarioHospedeExistenteEVincularAoHospede() {

        Long hospedeId = 1L;
        UUID usuarioId = UUID.randomUUID();

        Hospede hospede =
                new Hospede();

        hospede.setId(hospedeId);
        hospede.setNomeCompleto(
                "Lucas Cesar"
        );

        HospedeCriarAcessoBthsRequest request =
                new HospedeCriarAcessoBthsRequest();

        request.setEmail(
                "lucas@beattrips.com"
        );

        request.setSenhaTemporaria(
                "Senha123!"
        );

        Usuario usuarioExistente =
                new Usuario();

        usuarioExistente.setId(
                usuarioId
        );

        usuarioExistente.setNome(
                "Lucas Cesar"
        );

        usuarioExistente.setEmail(
                "lucas@beattrips.com"
        );

        usuarioExistente.setPerfil(
                PerfilUsuario.HOSPEDE
        );

        usuarioExistente.setAtivo(
                true
        );

        when(
                hospedeRepository.findById(
                        hospedeId
                )
        ).thenReturn(
                Optional.of(
                        hospede
                )
        );

        when(
                usuarioRepository.findByEmail(
                        "lucas@beattrips.com"
                )
        ).thenReturn(
                Optional.of(
                        usuarioExistente
                )
        );

        when(
                hospedeRepository.save(
                        hospede
                )
        ).thenReturn(
                hospede
        );

        HospedeAcessoBthsResponse resultado =
                hospedeService
                        .criarOuVincularAcessoBths(
                                hospedeId,
                                request
                        );

        assertNotNull(
                resultado
        );

        assertTrue(
                resultado.isVinculado()
        );

        assertEquals(
                usuarioId,
                resultado.getUsuarioId()
        );

        assertEquals(
                "lucas@beattrips.com",
                resultado.getEmail()
        );

        assertTrue(
                resultado.isAtivo()
        );

        assertEquals(
                usuarioExistente,
                hospede.getUsuario()
        );

        verify(
                usuarioRepository,
                never()
        ).save(
                any(Usuario.class)
        );

        verifyNoInteractions(
                passwordEncoder
        );

        verify(
                hospedeRepository
        ).save(
                hospede
        );
    }

    @Test
    void deveBloquearCriacaoDeAcessoQuandoEmailPertencerAAdminOuStaff() {

        Long hospedeId = 1L;

        Hospede hospede =
                new Hospede();

        hospede.setId(hospedeId);

        HospedeCriarAcessoBthsRequest request =
                new HospedeCriarAcessoBthsRequest();

        request.setEmail(
                "admin@beattrips.com"
        );

        request.setSenhaTemporaria(
                "Senha123!"
        );

        Usuario usuarioExistente =
                new Usuario();

        usuarioExistente.setEmail(
                "admin@beattrips.com"
        );

        usuarioExistente.setPerfil(
                PerfilUsuario.ADMIN
        );

        when(
                hospedeRepository.findById(
                        hospedeId
                )
        ).thenReturn(
                Optional.of(
                        hospede
                )
        );

        when(
                usuarioRepository.findByEmail(
                        "admin@beattrips.com"
                )
        ).thenReturn(
                Optional.of(
                        usuarioExistente
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        hospedeService
                                .criarOuVincularAcessoBths(
                                        hospedeId,
                                        request
                                )
        );

        verify(
                hospedeRepository,
                never()
        ).save(
                any(Hospede.class)
        );

        verify(
                usuarioRepository,
                never()
        ).save(
                any(Usuario.class)
        );

        verifyNoInteractions(
                passwordEncoder
        );
    }

    @Test
    void deveBloquearCriacaoDeAcessoQuandoHospedeJaPossuirUsuario() {

        Long hospedeId = 1L;

        Usuario usuarioAtual =
                new Usuario();

        usuarioAtual.setId(
                UUID.randomUUID()
        );

        usuarioAtual.setPerfil(
                PerfilUsuario.HOSPEDE
        );

        Hospede hospede =
                new Hospede();

        hospede.setId(
                hospedeId
        );

        hospede.setUsuario(
                usuarioAtual
        );

        HospedeCriarAcessoBthsRequest request =
                new HospedeCriarAcessoBthsRequest();

        request.setEmail(
                "novo@beattrips.com"
        );

        request.setSenhaTemporaria(
                "Senha123!"
        );

        when(
                hospedeRepository.findById(
                        hospedeId
                )
        ).thenReturn(
                Optional.of(
                        hospede
                )
        );

        assertThrows(
                HospedeJaVinculadoException.class,
                () ->
                        hospedeService
                                .criarOuVincularAcessoBths(
                                        hospedeId,
                                        request
                                )
        );

        verifyNoInteractions(
                usuarioRepository
        );

        verifyNoInteractions(
                passwordEncoder
        );

        verify(
                hospedeRepository,
                never()
        ).save(
                any(Hospede.class)
        );
    }

    @Test
    void deveLancarExcecaoAoCriarAcessoParaHospedeInexistente() {

        Long hospedeId = 999L;

        HospedeCriarAcessoBthsRequest request =
                new HospedeCriarAcessoBthsRequest();

        request.setEmail(
                "hospede@beattrips.com"
        );

        request.setSenhaTemporaria(
                "Senha123!"
        );

        when(
                hospedeRepository.findById(
                        hospedeId
                )
        ).thenReturn(
                Optional.empty()
        );

        assertThrows(
                HospedeNaoEncontradoException.class,
                () ->
                        hospedeService
                                .criarOuVincularAcessoBths(
                                        hospedeId,
                                        request
                                )
        );

        verifyNoInteractions(
                usuarioRepository
        );

        verifyNoInteractions(
                passwordEncoder
        );
    }

}