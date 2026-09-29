package com.bths.platform.app;

import com.bths.platform.alocacao.AlocacaoQuarto;
import com.bths.platform.alocacao.AlocacaoQuartoRepository;
import com.bths.platform.app.dto.MeuCheckInResponse;
import com.bths.platform.app.dto.MeuTrasladoResponse;
import com.bths.platform.app.dto.MinhaViagemResponse;
import com.bths.platform.hospede.Hospede;
import com.bths.platform.hospede.HospedeRepository;
import com.bths.platform.hospede.enums.StatusCheckIn;
import com.bths.platform.operacaoTraslado.OperacaoTraslado;
import com.bths.platform.qrcode.QrCodeGeradorService;
import com.bths.platform.quarto.Quarto;
import com.bths.platform.traslado.Traslado;
import com.bths.platform.traslado.TrasladoRepository;
import com.bths.platform.traslado.enums.Aeroporto;
import com.bths.platform.traslado.enums.StatusTraslado;
import com.bths.platform.traslado.enums.TipoTraslado;
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
import java.time.LocalDateTime;
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

    @Mock
    private TrasladoRepository trasladoRepository;

    @Mock
    private QrCodeGeradorService qrCodeGeradorService;

    @BeforeEach
    void setUp() {
        appHospedeService = new AppHospedeService(
                usuarioRepository,
                hospedeRepository,
                alocacaoQuartoRepository,
                trasladoRepository,
                qrCodeGeradorService
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

    @Test
    void deveBuscarTrasladoIndividualDoHospede() {

        String email = "hospede@bths.com";
        UUID usuarioId = UUID.randomUUID();

        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setEmail(email);
        usuario.setPerfil(PerfilUsuario.HOSPEDE);

        Hospede hospede = new Hospede();
        hospede.setId(20L);
        hospede.setNomeCompleto("Robson");

        Traslado traslado = new Traslado();
        traslado.setId(30L);
        traslado.setHospede(hospede);
        traslado.setTipo(
                TipoTraslado.AEROPORTO_PARA_HOSPEDAGEM
        );
        traslado.setAeroporto(
                Aeroporto.GRU
        );
        traslado.setStatus(
                StatusTraslado.AGUARDANDO
        );
        traslado.setDataHoraPrevista(
                LocalDateTime.of(
                        2027,
                        4,
                        29,
                        14,
                        30
                )
        );
        traslado.setNumeroVoo("LA1234");
        traslado.setCompanhiaAerea("LATAM");
        traslado.setLocalOrigem(
                "Aeroporto de Guarulhos"
        );
        traslado.setLocalDestino(
                "Chácara Beat Trips"
        );

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
                trasladoRepository.findByHospedeId(20L)
        ).thenReturn(
                List.of(traslado)
        );

        List<MeuTrasladoResponse> response =
                appHospedeService.buscarMeusTraslados(
                        email
                );

        assertEquals(
                1,
                response.size()
        );

        MeuTrasladoResponse meuTraslado =
                response.get(0);

        assertEquals(
                30L,
                meuTraslado.getId()
        );

        assertEquals(
                TipoTraslado.AEROPORTO_PARA_HOSPEDAGEM,
                meuTraslado.getTipo()
        );

        assertEquals(
                Aeroporto.GRU,
                meuTraslado.getAeroporto()
        );

        assertEquals(
                StatusTraslado.AGUARDANDO,
                meuTraslado.getStatus()
        );

        assertEquals(
                LocalDateTime.of(
                        2027,
                        4,
                        29,
                        14,
                        30
                ),
                meuTraslado.getDataHoraPrevista()
        );

        assertEquals(
                "Aeroporto de Guarulhos",
                meuTraslado.getLocalOrigem()
        );

        assertEquals(
                "Chácara Beat Trips",
                meuTraslado.getLocalDestino()
        );

        assertEquals(
                "LA1234",
                meuTraslado.getNumeroVoo()
        );

        assertEquals(
                "LATAM",
                meuTraslado.getCompanhiaAerea()
        );
    }

    @Test
    void deveUsarDadosDaOperacaoQuandoTrasladoEstiverVinculado() {

        String email = "hospede@bths.com";
        UUID usuarioId = UUID.randomUUID();

        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setEmail(email);
        usuario.setPerfil(PerfilUsuario.HOSPEDE);

        Hospede hospede = new Hospede();
        hospede.setId(20L);

        Traslado traslado = new Traslado();
        traslado.setId(30L);
        traslado.setHospede(hospede);

        traslado.setTipo(
                TipoTraslado.AEROPORTO_PARA_HOSPEDAGEM
        );
        traslado.setAeroporto(Aeroporto.GRU);
        traslado.setStatus(StatusTraslado.AGUARDANDO);

        traslado.setDataHoraPrevista(
                LocalDateTime.of(
                        2027, 4, 29, 14, 30
                )
        );

        traslado.setLocalOrigem(
                "Origem individual"
        );

        traslado.setLocalDestino(
                "Destino individual"
        );

        traslado.setNumeroVoo("LA1234");
        traslado.setCompanhiaAerea("LATAM");

        OperacaoTraslado operacao =
                new OperacaoTraslado();

        operacao.setTipo(
                TipoTraslado.AEROPORTO_PARA_HOSPEDAGEM
        );

        operacao.setAeroporto(Aeroporto.GRU);

        operacao.setStatus(
                StatusTraslado.EM_ANDAMENTO
        );

        operacao.setDataHoraPrevista(
                LocalDateTime.of(
                        2027, 4, 29, 16, 0
                )
        );

        operacao.setLocalOrigem(
                "Terminal 2 - Guarulhos"
        );

        operacao.setLocalDestino(
                "Chácara Beat Trips"
        );

        traslado.setOperacaoTraslado(
                operacao
        );

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
                trasladoRepository.findByHospedeId(20L)
        ).thenReturn(
                List.of(traslado)
        );

        List<MeuTrasladoResponse> response =
                appHospedeService.buscarMeusTraslados(
                        email
                );

        assertEquals(1, response.size());

        MeuTrasladoResponse meuTraslado =
                response.get(0);

        // Dados operacionais devem vir da operação.
        assertEquals(
                StatusTraslado.EM_ANDAMENTO,
                meuTraslado.getStatus()
        );

        assertEquals(
                LocalDateTime.of(
                        2027, 4, 29, 16, 0
                ),
                meuTraslado.getDataHoraPrevista()
        );

        assertEquals(
                "Terminal 2 - Guarulhos",
                meuTraslado.getLocalOrigem()
        );

        assertEquals(
                "Chácara Beat Trips",
                meuTraslado.getLocalDestino()
        );

        // Dados pessoais do voo continuam no traslado.
        assertEquals(
                "LA1234",
                meuTraslado.getNumeroVoo()
        );

        assertEquals(
                "LATAM",
                meuTraslado.getCompanhiaAerea()
        );
    }

    @Test
    void deveRetornarListaVaziaQuandoUsuarioNaoPossuiHospedeVinculadoAoBuscarTraslados() {

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

        List<MeuTrasladoResponse> response =
                appHospedeService.buscarMeusTraslados(
                        email
                );

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    void deveBloquearBuscaDeTrasladosQuandoUsuarioNaoForHospede() {

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
                        () -> appHospedeService
                                .buscarMeusTraslados(
                                        email
                                )
                );

        assertEquals(
                "A consulta é permitida apenas para usuários com perfil HOSPEDE.",
                exception.getMessage()
        );
    }

    @Test
    void deveBuscarMeuCheckInPendente() {

        String email = "hospede@bths.com";
        UUID usuarioId = UUID.randomUUID();

        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setEmail(email);
        usuario.setPerfil(PerfilUsuario.HOSPEDE);

        Hospede hospede = new Hospede();
        hospede.setNomeCompleto("Robson");
        hospede.setStatusCheckIn(
                StatusCheckIn.PENDENTE
        );

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

        MeuCheckInResponse response =
                appHospedeService.buscarMeuCheckIn(
                        email
                );

        assertNotNull(response);

        assertEquals(
                "Robson",
                response.getHospedeNome()
        );

        assertEquals(
                StatusCheckIn.PENDENTE,
                response.getStatusCheckIn()
        );

        assertNull(
                response.getDataHoraCheckIn()
        );
    }

    @Test
    void deveBuscarMeuCheckInRealizado() {

        String email = "hospede@bths.com";
        UUID usuarioId = UUID.randomUUID();

        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setEmail(email);
        usuario.setPerfil(PerfilUsuario.HOSPEDE);

        LocalDateTime dataHoraCheckIn =
                LocalDateTime.of(
                        2027,
                        4,
                        30,
                        14,
                        30
                );

        Hospede hospede = new Hospede();
        hospede.setNomeCompleto("Robson");
        hospede.setStatusCheckIn(
                StatusCheckIn.REALIZADO
        );
        hospede.setDataHoraCheckIn(
                dataHoraCheckIn
        );

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

        MeuCheckInResponse response =
                appHospedeService.buscarMeuCheckIn(
                        email
                );

        assertNotNull(response);

        assertEquals(
                StatusCheckIn.REALIZADO,
                response.getStatusCheckIn()
        );

        assertEquals(
                dataHoraCheckIn,
                response.getDataHoraCheckIn()
        );
    }

    @Test
    void deveBuscarMeuCheckInNaoCompareceu() {

        String email = "hospede@bths.com";
        UUID usuarioId = UUID.randomUUID();

        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setEmail(email);
        usuario.setPerfil(PerfilUsuario.HOSPEDE);

        Hospede hospede = new Hospede();
        hospede.setNomeCompleto("Robson");
        hospede.setStatusCheckIn(
                StatusCheckIn.NAO_COMPARECEU
        );

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

        MeuCheckInResponse response =
                appHospedeService.buscarMeuCheckIn(
                        email
                );

        assertNotNull(response);

        assertEquals(
                StatusCheckIn.NAO_COMPARECEU,
                response.getStatusCheckIn()
        );
    }

    @Test
    void deveRetornarNullQuandoUsuarioNaoPossuiHospedeVinculadoAoBuscarCheckIn() {

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

        MeuCheckInResponse response =
                appHospedeService.buscarMeuCheckIn(
                        email
                );

        assertNull(response);
    }

    @Test
    void deveGerarQrCodeDoProprioHospede() {

        String email = "hospede@bths.com";
        UUID usuarioId = UUID.randomUUID();
        String codigoCheckIn =
                "codigo-seguro-do-hospede";

        byte[] imagemEsperada =
                new byte[]{1, 2, 3, 4};

        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setEmail(email);
        usuario.setPerfil(PerfilUsuario.HOSPEDE);

        Hospede hospede = new Hospede();
        hospede.setId(20L);
        hospede.setCodigoCheckIn(
                codigoCheckIn
        );

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
                qrCodeGeradorService.gerarQRCode(
                        codigoCheckIn
                )
        ).thenReturn(
                imagemEsperada
        );

        byte[] response =
                appHospedeService.buscarMeuQrCode(
                        email
                );

        assertNotNull(response);

        assertArrayEquals(
                imagemEsperada,
                response
        );
    }

    @Test
    void deveRetornarNullQuandoHospedeNaoPossuiCodigoCheckIn() {

        String email = "hospede@bths.com";
        UUID usuarioId = UUID.randomUUID();

        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setEmail(email);
        usuario.setPerfil(PerfilUsuario.HOSPEDE);

        Hospede hospede = new Hospede();
        hospede.setId(20L);
        hospede.setCodigoCheckIn(null);

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

        byte[] response =
                appHospedeService.buscarMeuQrCode(
                        email
                );

        assertNull(response);
    }

    @Test
    void deveRetornarNullAoBuscarQrQuandoUsuarioNaoPossuiHospedeVinculado() {

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

        byte[] response =
                appHospedeService.buscarMeuQrCode(
                        email
                );

        assertNull(response);
    }

    @Test
    void deveBloquearBuscaDeCheckInQuandoUsuarioNaoForHospede() {

        String email = "admin@bths.com";

        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setPerfil(PerfilUsuario.ADMIN);

        when(
                usuarioRepository.findByEmail(email)
        ).thenReturn(
                Optional.of(usuario)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> appHospedeService.buscarMeuCheckIn(
                        email
                )
        );
    }

    @Test
    void deveBloquearBuscaDeQrCodeQuandoUsuarioNaoForHospede() {

        String email = "staff@bths.com";

        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setPerfil(PerfilUsuario.STAFF);

        when(
                usuarioRepository.findByEmail(email)
        ).thenReturn(
                Optional.of(usuario)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> appHospedeService.buscarMeuQrCode(
                        email
                )
        );
    }



}