package com.bths.platform.app;

import com.bths.platform.alocacao.AlocacaoQuarto;
import com.bths.platform.alocacao.AlocacaoQuartoRepository;
import com.bths.platform.app.dto.MinhaViagemResponse;
import com.bths.platform.hospede.Hospede;
import com.bths.platform.hospede.HospedeRepository;
import com.bths.platform.quarto.Quarto;
import com.bths.platform.usuario.Usuario;
import com.bths.platform.usuario.UsuarioRepository;
import com.bths.platform.usuario.enums.PerfilUsuario;
import com.bths.platform.usuario.exception.UsuarioNaoEncontradoException;
import com.bths.platform.viagem.Viagem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppHospedeServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private HospedeRepository hospedeRepository;

    @Mock
    private AlocacaoQuartoRepository alocacaoQuartoRepository;

    private AppHospedeService appHospedeService;

    @BeforeEach
    void setUp() {
        appHospedeService = new AppHospedeService(
                usuarioRepository,
                hospedeRepository,
                alocacaoQuartoRepository
        );
    }

    @Test
    void deveBuscarMinhaViagemComQuartoQuandoHospedePossuiAlocacao() {

        String email = "hospede@bths.com";
        UUID usuarioId = UUID.randomUUID();

        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setEmail(email);
        usuario.setPerfil(PerfilUsuario.HOSPEDE);

        Viagem viagem = new Viagem();
        viagem.setId(10L);
        viagem.setNome("Tomorrowland Brasil 2027");
        viagem.setEvento("Tomorrowland Brasil");
        viagem.setDataInicio(
                LocalDate.of(2027, 4, 30)
        );
        viagem.setDataFim(
                LocalDate.of(2027, 5, 2)
        );
        viagem.setEndereco("Chácara Beat Trips");
        viagem.setCidade("Alumínio");
        viagem.setEstado("SP");

        Hospede hospede = new Hospede();
        hospede.setId(20L);
        hospede.setNomeCompleto("Robson");
        hospede.setViagem(viagem);

        Quarto quarto = new Quarto();
        quarto.setId(30L);
        quarto.setNome("Suíte 01");

        AlocacaoQuarto alocacao =
                new AlocacaoQuarto();

        alocacao.setHospede(hospede);
        alocacao.setViagem(viagem);
        alocacao.setQuarto(quarto);

        when(
                usuarioRepository.findByEmail(email)
        ).thenReturn(
                Optional.of(usuario)
        );

        when(
                hospedeRepository.findByUsuarioId(usuarioId)
        ).thenReturn(
                List.of(hospede)
        );

        when(
                alocacaoQuartoRepository
                        .findByHospedeIdAndViagemId(
                                20L,
                                10L
                        )
        ).thenReturn(
                Optional.of(alocacao)
        );

        MinhaViagemResponse response =
                appHospedeService.buscarMinhaViagem(
                        email
                );

        assertNotNull(response);

        assertEquals(
                20L,
                response.getHospedeId()
        );

        assertEquals(
                "Robson",
                response.getHospedeNome()
        );

        assertEquals(
                10L,
                response.getViagemId()
        );

        assertEquals(
                "Tomorrowland Brasil 2027",
                response.getViagemNome()
        );

        assertEquals(
                "Tomorrowland Brasil",
                response.getEvento()
        );

        assertEquals(
                LocalDate.of(2027, 4, 30),
                response.getDataInicio()
        );

        assertEquals(
                LocalDate.of(2027, 5, 2),
                response.getDataFim()
        );

        assertEquals(
                "Chácara Beat Trips",
                response.getEndereco()
        );

        assertEquals(
                "Alumínio",
                response.getCidade()
        );

        assertEquals(
                "SP",
                response.getEstado()
        );

        assertEquals(
                30L,
                response.getQuartoId()
        );

        assertEquals(
                "Suíte 01",
                response.getQuartoNome()
        );
    }

    @Test
    void deveBuscarMinhaViagemSemQuartoQuandoHospedeAindaNaoPossuiAlocacao() {

        String email = "hospede@bths.com";
        UUID usuarioId = UUID.randomUUID();

        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setEmail(email);
        usuario.setPerfil(PerfilUsuario.HOSPEDE);

        Viagem viagem = new Viagem();
        viagem.setId(10L);
        viagem.setNome("Tomorrowland Brasil 2027");
        viagem.setEvento("Tomorrowland Brasil");
        viagem.setDataInicio(
                LocalDate.of(2027, 4, 30)
        );
        viagem.setDataFim(
                LocalDate.of(2027, 5, 2)
        );
        viagem.setEndereco("Chácara Beat Trips");
        viagem.setCidade("Alumínio");
        viagem.setEstado("SP");

        Hospede hospede = new Hospede();
        hospede.setId(20L);
        hospede.setNomeCompleto("Robson");
        hospede.setViagem(viagem);

        when(
                usuarioRepository.findByEmail(email)
        ).thenReturn(
                Optional.of(usuario)
        );

        when(
                hospedeRepository.findByUsuarioId(usuarioId)
        ).thenReturn(
                List.of(hospede)
        );

        when(
                alocacaoQuartoRepository
                        .findByHospedeIdAndViagemId(
                                20L,
                                10L
                        )
        ).thenReturn(
                Optional.empty()
        );

        MinhaViagemResponse response =
                appHospedeService.buscarMinhaViagem(
                        email
                );

        assertNotNull(response);

        assertEquals(
                20L,
                response.getHospedeId()
        );

        assertEquals(
                10L,
                response.getViagemId()
        );

        assertEquals(
                "Tomorrowland Brasil 2027",
                response.getViagemNome()
        );

        assertNull(
                response.getQuartoId()
        );

        assertNull(
                response.getQuartoNome()
        );
    }

    @Test
    void deveBloquearConsultaQuandoUsuarioNaoForHospede() {

        String email = "admin@bths.com";
        UUID usuarioId = UUID.randomUUID();

        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setEmail(email);
        usuario.setPerfil(PerfilUsuario.ADMIN);

        when(
                usuarioRepository.findByEmail(email)
        ).thenReturn(
                Optional.of(usuario)
        );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> appHospedeService.buscarMinhaViagem(
                                email
                        )
                );

        assertEquals(
                "A consulta é permitida apenas para usuários com perfil HOSPEDE.",
                exception.getMessage()
        );
    }


    @Test
    void deveRetornarNullQuandoUsuarioNaoPossuiHospedeVinculado() {

        String email = "hospede@bths.com";
        UUID usuarioId = UUID.randomUUID();

        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setEmail(email);
        usuario.setPerfil(PerfilUsuario.HOSPEDE);

        when(
                usuarioRepository.findByEmail(email)
        ).thenReturn(
                Optional.of(usuario)
        );

        when(
                hospedeRepository.findByUsuarioId(usuarioId)
        ).thenReturn(
                List.of()
        );

        MinhaViagemResponse response =
                appHospedeService.buscarMinhaViagem(
                        email
                );

        assertNull(response);
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoForEncontrado() {

        String email = "inexistente@bths.com";

        when(
                usuarioRepository.findByEmail(email)
        ).thenReturn(
                Optional.empty()
        );

        UsuarioNaoEncontradoException exception =
                assertThrows(
                        UsuarioNaoEncontradoException.class,
                        () -> appHospedeService.buscarMinhaViagem(
                                email
                        )
                );

        assertEquals(
                "Usuário não encontrado!",
                exception.getMessage()
        );
    }



}