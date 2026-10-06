package com.bths.platform.app;

import com.bths.platform.agenda.AgendaViagem;
import com.bths.platform.agenda.AgendaViagemRepository;
import com.bths.platform.agenda.enums.TipoAgendaViagem;
import com.bths.platform.alocacao.AlocacaoQuarto;
import com.bths.platform.alocacao.AlocacaoQuartoRepository;
import com.bths.platform.alocacao.enums.TipoCama;
import com.bths.platform.app.dto.*;
import com.bths.platform.hospedagem.Hospedagem;
import com.bths.platform.hospede.Hospede;
import com.bths.platform.hospede.HospedeRepository;
import com.bths.platform.hospede.enums.StatusCheckIn;
import com.bths.platform.motorista.Motorista;
import com.bths.platform.operacaoTraslado.OperacaoTraslado;
import com.bths.platform.qrcode.QrCodeGeradorService;
import com.bths.platform.quarto.Quarto;
import com.bths.platform.quarto.enums.TipoQuarto;
import com.bths.platform.traslado.Traslado;
import com.bths.platform.traslado.TrasladoRepository;
import com.bths.platform.traslado.enums.Aeroporto;
import com.bths.platform.traslado.enums.StatusTraslado;
import com.bths.platform.traslado.enums.TipoTraslado;
import com.bths.platform.usuario.Usuario;
import com.bths.platform.usuario.UsuarioRepository;
import com.bths.platform.usuario.enums.PerfilUsuario;
import com.bths.platform.usuario.exception.UsuarioNaoEncontradoException;
import com.bths.platform.veiculo.Veiculo;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

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

    @Mock
    private AgendaViagemRepository agendaViagemRepository;

    @BeforeEach
    void setUp() {
        appHospedeService = new AppHospedeService(
                usuarioRepository,
                hospedeRepository,
                alocacaoQuartoRepository,
                trasladoRepository,
                qrCodeGeradorService,
                agendaViagemRepository
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

    @Test
    void deveExporMotoristaVeiculoEOrientacaoDoTrasladoIndividual() {

        String email = "hospede@bths.com";
        UUID usuarioId = UUID.randomUUID();

        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setEmail(email);
        usuario.setPerfil(PerfilUsuario.HOSPEDE);

        Hospede hospede = new Hospede();
        hospede.setId(20L);

        Motorista motorista =
                new Motorista();

        motorista.setNomeCompleto(
                "Anderson Reis"
        );

        Veiculo veiculo =
                new Veiculo();

        veiculo.setModelo(
                "Van Sprinter"
        );

        veiculo.setPlaca(
                "FKZ7A32"
        );

        Traslado traslado =
                new Traslado();

        traslado.setId(30L);
        traslado.setHospede(hospede);

        traslado.setTipo(
                TipoTraslado.AEROPORTO_PARA_HOSPEDAGEM
        );

        traslado.setStatus(
                StatusTraslado.AGUARDANDO
        );

        traslado.setDataHoraPrevista(
                LocalDateTime.of(
                        2027,
                        4,
                        29,
                        13,
                        0
                )
        );

        traslado.setLocalOrigem(
                "Aeroporto de Guarulhos"
        );

        traslado.setLocalDestino(
                "Hospedagem Beat Trips"
        );

        traslado.setMotorista(
                motorista
        );

        traslado.setVeiculo(
                veiculo
        );

        traslado.setOrientacaoHospede(
                "Motorista aguardando próximo à saída H."
        );

        when(
                usuarioRepository.findByEmail(email)
        ).thenReturn(
                Optional.of(usuario)
        );

        when(
                hospedeRepository.findByUsuarioId(
                        usuarioId
                )
        ).thenReturn(
                List.of(hospede)
        );

        when(
                trasladoRepository.findByHospedeId(
                        20L
                )
        ).thenReturn(
                List.of(traslado)
        );

        List<MeuTrasladoResponse> response =
                appHospedeService.buscarMeusTraslados(
                        email
                );

        MeuTrasladoResponse meuTraslado =
                response.get(0);

        assertEquals(
                "Anderson Reis",
                meuTraslado.getMotoristaNome()
        );

        assertEquals(
                "Van Sprinter",
                meuTraslado.getVeiculoModelo()
        );

        assertEquals(
                "FKZ7A32",
                meuTraslado.getVeiculoPlaca()
        );

        assertEquals(
                "Motorista aguardando próximo à saída H.",
                meuTraslado.getOrientacaoHospede()
        );
    }

    @Test
    void devePriorizarMotoristaVeiculoEOrientacaoDaOperacao() {

        String email = "hospede@bths.com";
        UUID usuarioId = UUID.randomUUID();

        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setEmail(email);
        usuario.setPerfil(PerfilUsuario.HOSPEDE);

        Hospede hospede = new Hospede();
        hospede.setId(20L);

        Motorista motoristaIndividual =
                new Motorista();

        motoristaIndividual.setNomeCompleto(
                "Motorista antigo"
        );

        Veiculo veiculoIndividual =
                new Veiculo();

        veiculoIndividual.setModelo(
                "Veículo antigo"
        );

        veiculoIndividual.setPlaca(
                "ABC1D23"
        );

        Motorista motoristaOperacao =
                new Motorista();

        motoristaOperacao.setNomeCompleto(
                "Anderson Reis"
        );

        Veiculo veiculoOperacao =
                new Veiculo();

        veiculoOperacao.setModelo(
                "Van Sprinter"
        );

        veiculoOperacao.setPlaca(
                "FKZ7A32"
        );

        Traslado traslado = new Traslado();

        traslado.setId(30L);
        traslado.setHospede(hospede);

        traslado.setMotorista(
                motoristaIndividual
        );

        traslado.setVeiculo(
                veiculoIndividual
        );

        traslado.setOrientacaoHospede(
                "Orientação antiga."
        );

        OperacaoTraslado operacao =
                new OperacaoTraslado();

        operacao.setTipo(
                TipoTraslado.AEROPORTO_PARA_HOSPEDAGEM
        );

        operacao.setAeroporto(
                Aeroporto.GRU
        );

        operacao.setStatus(
                StatusTraslado.AGUARDANDO
        );

        operacao.setDataHoraPrevista(
                LocalDateTime.of(
                        2027,
                        4,
                        29,
                        13,
                        0
                )
        );

        operacao.setLocalOrigem(
                "Aeroporto de Guarulhos"
        );

        operacao.setLocalDestino(
                "Hospedagem Beat Trips"
        );

        operacao.setMotorista(
                motoristaOperacao
        );

        operacao.setVeiculo(
                veiculoOperacao
        );

        operacao.setOrientacaoHospede(
                "Aguarde próximo à saída H."
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
                hospedeRepository.findByUsuarioId(
                        usuarioId
                )
        ).thenReturn(
                List.of(hospede)
        );

        when(
                trasladoRepository.findByHospedeId(
                        20L
                )
        ).thenReturn(
                List.of(traslado)
        );

        List<MeuTrasladoResponse> response =
                appHospedeService.buscarMeusTraslados(
                        email
                );

        MeuTrasladoResponse meuTraslado =
                response.get(0);

        assertEquals(
                "Anderson Reis",
                meuTraslado.getMotoristaNome()
        );

        assertEquals(
                "Van Sprinter",
                meuTraslado.getVeiculoModelo()
        );

        assertEquals(
                "FKZ7A32",
                meuTraslado.getVeiculoPlaca()
        );

        assertEquals(
                "Aguarde próximo à saída H.",
                meuTraslado.getOrientacaoHospede()
        );
    }

    @Test
    void deveBuscarMeuQuartoQuandoHospedePossuiAlocacao() {

        String email = "hospede@bths.com";
        UUID usuarioId = UUID.randomUUID();

        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setEmail(email);
        usuario.setPerfil(PerfilUsuario.HOSPEDE);

        Viagem viagem = new Viagem();
        viagem.setId(10L);
        viagem.setNome("Tomorrowland Brasil 2027");

        Hospede hospede = new Hospede();
        hospede.setId(20L);
        hospede.setNomeCompleto("Robson");
        hospede.setViagem(viagem);

        Quarto quarto = new Quarto();
        quarto.setId(30L);
        quarto.setNome("Suíte 01");
        quarto.setTipo(
                TipoQuarto.SUITE
        );
        quarto.setCapacidade(
                4
        );

        AlocacaoQuarto alocacao =
                new AlocacaoQuarto();

        alocacao.setHospede(
                hospede
        );

        alocacao.setViagem(
                viagem
        );

        alocacao.setQuarto(
                quarto
        );

        alocacao.setTipoCama(
                TipoCama.CASAL
        );

        when(
                usuarioRepository.findByEmail(
                        email
                )
        ).thenReturn(
                Optional.of(usuario)
        );

        when(
                hospedeRepository.findByUsuarioId(
                        usuarioId
                )
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

        MeuQuartoResponse response =
                appHospedeService.buscarMeuQuarto(
                        email
                );

        assertNotNull(
                response
        );

        assertEquals(
                30L,
                response.getQuartoId()
        );

        assertEquals(
                "Suíte 01",
                response.getQuartoNome()
        );

        assertEquals(
                TipoQuarto.SUITE,
                response.getQuartoTipo()
        );

        assertEquals(
                TipoCama.CASAL,
                response.getTipoCama()
        );

        assertEquals(
                4,
                response.getCapacidade()
        );

        assertEquals(
                10L,
                response.getViagemId()
        );

        assertEquals(
                "Tomorrowland Brasil 2027",
                response.getViagemNome()
        );
    }

    @Test
    void deveBuscarMinhaHospedagemQuandoQuartoPossuiHospedagem() {

        String email = "hospede@bths.com";
        UUID usuarioId = UUID.randomUUID();

        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setEmail(email);
        usuario.setPerfil(PerfilUsuario.HOSPEDE);

        Viagem viagem = new Viagem();
        viagem.setId(10L);
        viagem.setNome("Tomorrowland Brasil 2027");

        Hospede hospede = new Hospede();
        hospede.setId(20L);
        hospede.setNomeCompleto("Robson");
        hospede.setViagem(viagem);

        Hospedagem hospedagem =
                new Hospedagem();

        hospedagem.setId(40L);
        hospedagem.setNome(
                "Chácara Beat Trips"
        );
        hospedagem.setEndereco(
                "Estrada Exemplo, 100"
        );
        hospedagem.setCidade(
                "Alumínio"
        );
        hospedagem.setEstado(
                "SP"
        );
        hospedagem.setWifiNome(
                "Beat Trips"
        );
        hospedagem.setWifiSenha(
                "senha123"
        );
        hospedagem.setViagem(
                viagem
        );

        Quarto quarto = new Quarto();
        quarto.setId(30L);
        quarto.setNome("Suíte 01");
        quarto.setHospedagem(
                hospedagem
        );

        AlocacaoQuarto alocacao =
                new AlocacaoQuarto();

        alocacao.setHospede(
                hospede
        );
        alocacao.setViagem(
                viagem
        );
        alocacao.setQuarto(
                quarto
        );

        when(
                usuarioRepository.findByEmail(
                        email
                )
        ).thenReturn(
                Optional.of(usuario)
        );

        when(
                hospedeRepository.findByUsuarioId(
                        usuarioId
                )
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

        MinhaHospedagemResponse response =
                appHospedeService
                        .buscarMinhaHospedagem(
                                email
                        );

        assertNotNull(
                response
        );

        assertEquals(
                40L,
                response.getHospedagemId()
        );

        assertEquals(
                "Chácara Beat Trips",
                response.getNome()
        );

        assertEquals(
                "Estrada Exemplo, 100",
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
                "Beat Trips",
                response.getWifiNome()
        );

        assertEquals(
                "senha123",
                response.getWifiSenha()
        );

        assertEquals(
                10L,
                response.getViagemId()
        );

        assertEquals(
                "Tomorrowland Brasil 2027",
                response.getViagemNome()
        );
    }

    @Test
    void deveRetornarNullQuandoHospedeAindaNaoPossuiAlocacaoParaHospedagem() {

        String email = "hospede@bths.com";
        UUID usuarioId = UUID.randomUUID();

        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setEmail(email);
        usuario.setPerfil(PerfilUsuario.HOSPEDE);

        Viagem viagem = new Viagem();
        viagem.setId(10L);

        Hospede hospede = new Hospede();
        hospede.setId(20L);
        hospede.setViagem(
                viagem
        );

        when(
                usuarioRepository.findByEmail(
                        email
                )
        ).thenReturn(
                Optional.of(usuario)
        );

        when(
                hospedeRepository.findByUsuarioId(
                        usuarioId
                )
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

        MinhaHospedagemResponse response =
                appHospedeService
                        .buscarMinhaHospedagem(
                                email
                        );

        assertNull(
                response
        );
    }

    @Test
    void deveRetornarNullQuandoQuartoAindaNaoPossuiHospedagem() {

        String email = "hospede@bths.com";
        UUID usuarioId = UUID.randomUUID();

        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setEmail(email);
        usuario.setPerfil(PerfilUsuario.HOSPEDE);

        Viagem viagem = new Viagem();
        viagem.setId(10L);

        Hospede hospede = new Hospede();
        hospede.setId(20L);
        hospede.setViagem(
                viagem
        );

        Quarto quarto = new Quarto();
        quarto.setId(30L);

        AlocacaoQuarto alocacao =
                new AlocacaoQuarto();

        alocacao.setHospede(
                hospede
        );
        alocacao.setViagem(
                viagem
        );
        alocacao.setQuarto(
                quarto
        );

        when(
                usuarioRepository.findByEmail(
                        email
                )
        ).thenReturn(
                Optional.of(usuario)
        );

        when(
                hospedeRepository.findByUsuarioId(
                        usuarioId
                )
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

        MinhaHospedagemResponse response =
                appHospedeService
                        .buscarMinhaHospedagem(
                                email
                        );

        assertNull(
                response
        );
    }

    @Test
    void deveRetornarNullQuandoHospedeAindaNaoPossuiQuarto() {

        String email = "hospede@bths.com";
        UUID usuarioId = UUID.randomUUID();

        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setEmail(email);
        usuario.setPerfil(PerfilUsuario.HOSPEDE);

        Viagem viagem = new Viagem();
        viagem.setId(10L);
        viagem.setNome("Tomorrowland Brasil 2027");

        Hospede hospede = new Hospede();
        hospede.setId(20L);
        hospede.setNomeCompleto("Robson");
        hospede.setViagem(
                viagem
        );

        when(
                usuarioRepository.findByEmail(
                        email
                )
        ).thenReturn(
                Optional.of(usuario)
        );

        when(
                hospedeRepository.findByUsuarioId(
                        usuarioId
                )
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

        MeuQuartoResponse response =
                appHospedeService.buscarMeuQuarto(
                        email
                );

        assertNull(
                response
        );
    }

    @Test
    void deveBuscarMinhaTimelineComItensVisiveisEAtivos() {

        String email = "hospede@bths.com";
        UUID usuarioId = UUID.randomUUID();

        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setEmail(email);
        usuario.setPerfil(
                PerfilUsuario.HOSPEDE
        );

        Viagem viagem = new Viagem();
        viagem.setId(10L);
        viagem.setNome(
                "Tomorrowland Brasil 2027"
        );

        Hospede hospede = new Hospede();
        hospede.setId(20L);
        hospede.setNomeCompleto(
                "Robson"
        );
        hospede.setViagem(
                viagem
        );

        AgendaViagem dia1 =
                new AgendaViagem();

        dia1.setId(100L);
        dia1.setTitulo(
                "Festival — Dia 1"
        );
        dia1.setDescricao(
                "Primeiro dia do festival"
        );
        dia1.setDataHoraInicio(
                LocalDateTime.of(
                        2027,
                        4,
                        30,
                        13,
                        0
                )
        );
        dia1.setTipo(
                TipoAgendaViagem.FESTIVAL
        );
        dia1.setOrdem(1);
        dia1.setAtivo(true);
        dia1.setVisivelHospede(true);
        dia1.setViagem(
                viagem
        );

        AgendaViagem dia2 =
                new AgendaViagem();

        dia2.setId(101L);
        dia2.setTitulo(
                "Festival — Dia 2"
        );
        dia2.setDataHoraInicio(
                LocalDateTime.of(
                        2027,
                        5,
                        1,
                        13,
                        0
                )
        );
        dia2.setTipo(
                TipoAgendaViagem.FESTIVAL
        );
        dia2.setOrdem(2);
        dia2.setAtivo(true);
        dia2.setVisivelHospede(true);
        dia2.setViagem(
                viagem
        );

        when(
                usuarioRepository.findByEmail(
                        email
                )
        ).thenReturn(
                Optional.of(usuario)
        );

        when(
                hospedeRepository.findByUsuarioId(
                        usuarioId
                )
        ).thenReturn(
                List.of(hospede)
        );

        when(
                agendaViagemRepository
                        .findByViagemIdAndAtivoTrueAndVisivelHospedeTrueOrderByOrdemAscDataHoraInicioAsc(
                                10L
                        )
        ).thenReturn(
                List.of(
                        dia1,
                        dia2
                )
        );

        List<MinhaTimelineResponse> response =
                appHospedeService.buscarMinhaTimeline(
                        email
                );

        assertNotNull(
                response
        );

        assertEquals(
                2,
                response.size()
        );

        MinhaTimelineResponse primeiro =
                response.get(0);

        assertEquals(
                100L,
                primeiro.getId()
        );

        assertEquals(
                "Festival — Dia 1",
                primeiro.getTitulo()
        );

        assertEquals(
                "Primeiro dia do festival",
                primeiro.getDescricao()
        );

        assertEquals(
                LocalDateTime.of(
                        2027,
                        4,
                        30,
                        13,
                        0
                ),
                primeiro.getDataHoraInicio()
        );

        assertEquals(
                TipoAgendaViagem.FESTIVAL,
                primeiro.getTipo()
        );

        MinhaTimelineResponse segundo =
                response.get(1);

        assertEquals(
                "Festival — Dia 2",
                segundo.getTitulo()
        );
    }

    @Test
    void deveRetornarListaVaziaQuandoHospedeNaoPossuirItensNaTimeline() {

        String email = "hospede@bths.com";
        UUID usuarioId = UUID.randomUUID();

        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setEmail(email);
        usuario.setPerfil(
                PerfilUsuario.HOSPEDE
        );

        Viagem viagem = new Viagem();
        viagem.setId(10L);
        viagem.setNome(
                "Tomorrowland Brasil 2027"
        );

        Hospede hospede = new Hospede();
        hospede.setId(20L);
        hospede.setViagem(
                viagem
        );

        when(
                usuarioRepository.findByEmail(
                        email
                )
        ).thenReturn(
                Optional.of(usuario)
        );

        when(
                hospedeRepository.findByUsuarioId(
                        usuarioId
                )
        ).thenReturn(
                List.of(hospede)
        );

        when(
                agendaViagemRepository
                        .findByViagemIdAndAtivoTrueAndVisivelHospedeTrueOrderByOrdemAscDataHoraInicioAsc(
                                10L
                        )
        ).thenReturn(
                List.of()
        );

        List<MinhaTimelineResponse> response =
                appHospedeService.buscarMinhaTimeline(
                        email
                );

        assertNotNull(
                response
        );

        assertTrue(
                response.isEmpty()
        );
    }

    @Test
    void deveBloquearTimelineQuandoUsuarioNaoForHospede() {

        String email = "admin@bths.com";
        UUID usuarioId =
                UUID.randomUUID();

        Usuario usuario =
                new Usuario();

        usuario.setId(
                usuarioId
        );

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
                Optional.of(usuario)
        );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                appHospedeService
                                        .buscarMinhaTimeline(
                                                email
                                        )
                );

        assertEquals(
                "A consulta é permitida apenas para usuários com perfil HOSPEDE.",
                exception.getMessage()
        );
    }


    @Test
    void deveRetornarNullNoMeuQuartoQuandoUsuarioNaoPossuirHospedeVinculado() {

        String email = "semvinculo@beattrips.com";
        UUID usuarioId =
                UUID.randomUUID();

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
                Optional.of(usuario)
        );

        when(
                hospedeRepository.findByUsuarioId(
                        usuarioId
                )
        ).thenReturn(
                List.of()
        );

        MeuQuartoResponse response =
                appHospedeService.buscarMeuQuarto(
                        email
                );

        assertNull(
                response
        );

        verify(
                alocacaoQuartoRepository,
                never()
        ).findByHospedeIdAndViagemId(
                anyLong(),
                anyLong()
        );
    }

    @Test
    void deveRetornarTimelineVaziaQuandoUsuarioNaoPossuirHospedeVinculado() {

        String email = "semvinculo@beattrips.com";
        UUID usuarioId =
                UUID.randomUUID();

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
                Optional.of(usuario)
        );

        when(
                hospedeRepository.findByUsuarioId(
                        usuarioId
                )
        ).thenReturn(
                List.of()
        );

        List<MinhaTimelineResponse> response =
                appHospedeService.buscarMinhaTimeline(
                        email
                );

        assertNotNull(
                response
        );

        assertTrue(
                response.isEmpty()
        );

        verifyNoInteractions(
                agendaViagemRepository
        );
    }

}