package com.bths.platform.app;

import com.bths.platform.alocacao.enums.TipoCama;
import com.bths.platform.app.dto.*;
import com.bths.platform.hospede.HospedeService;
import com.bths.platform.hospede.dto.HospedeResponse;
import com.bths.platform.hospede.enums.StatusCheckIn;
import com.bths.platform.quarto.enums.TipoQuarto;
import com.bths.platform.security.JwtService;
import com.bths.platform.security.UsuarioDetailsService;
import com.bths.platform.traslado.enums.Aeroporto;
import com.bths.platform.traslado.enums.StatusTraslado;
import com.bths.platform.traslado.enums.TipoTraslado;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AppHospedeAuthorizationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HospedeService hospedeService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UsuarioDetailsService usuarioDetailsService;

    @MockitoBean
    private AppHospedeService appHospedeService;

    @Test
    void devePermitirHospedeAcessarPropriosHospedes()
            throws Exception {

        UserDetails hospede = User
                .withUsername("hospede@beattrips.com")
                .password("senha")
                .roles("HOSPEDE")
                .build();

        when(
                jwtService.extrairEmail(
                        "token-hospede"
                )
        ).thenReturn(
                "hospede@beattrips.com"
        );

        when(
                usuarioDetailsService.loadUserByUsername(
                        "hospede@beattrips.com"
                )
        ).thenReturn(
                hospede
        );

        when(
                jwtService.tokenValido(
                        "token-hospede",
                        hospede
                )
        ).thenReturn(
                true
        );

        HospedeResponse response =
                new HospedeResponse();

        response.setId(
                10L
        );

        response.setNomeCompleto(
                "Lucas Cesar"
        );

        when(
                hospedeService.buscarHospedesDoUsuario(
                        "hospede@beattrips.com"
                )
        ).thenReturn(
                List.of(
                        response
                )
        );

        mockMvc.perform(
                        get(
                                "/api/app/hospedes"
                        )
                                .header(
                                        "Authorization",
                                        "Bearer token-hospede"
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$[0].id")
                                .value(10L)
                )
                .andExpect(
                        jsonPath("$[0].nomeCompleto")
                                .value(
                                        "Lucas Cesar"
                                )
                );
    }

    @Test
    void deveBloquearAdminAoAcessarAreaHospede()
            throws Exception {

        UserDetails admin = User
                .withUsername("admin@beattrips.com")
                .password("senha")
                .roles("ADMIN")
                .build();

        when(
                jwtService.extrairEmail(
                        "token-admin"
                )
        ).thenReturn(
                "admin@beattrips.com"
        );

        when(
                usuarioDetailsService.loadUserByUsername(
                        "admin@beattrips.com"
                )
        ).thenReturn(
                admin
        );

        when(
                jwtService.tokenValido(
                        "token-admin",
                        admin
                )
        ).thenReturn(
                true
        );

        mockMvc.perform(
                        get(
                                "/api/app/hospedes"
                        )
                                .header(
                                        "Authorization",
                                        "Bearer token-admin"
                                )
                )
                .andExpect(
                        status().isForbidden()
                );
    }

    @Test
    void deveBloquearStaffAoAcessarAreaHospede()
            throws Exception {

        UserDetails staff = User
                .withUsername("staff@beattrips.com")
                .password("senha")
                .roles("STAFF")
                .build();

        when(
                jwtService.extrairEmail(
                        "token-staff"
                )
        ).thenReturn(
                "staff@beattrips.com"
        );

        when(
                usuarioDetailsService.loadUserByUsername(
                        "staff@beattrips.com"
                )
        ).thenReturn(
                staff
        );

        when(
                jwtService.tokenValido(
                        "token-staff",
                        staff
                )
        ).thenReturn(
                true
        );

        mockMvc.perform(
                        get(
                                "/api/app/hospedes"
                        )
                                .header(
                                        "Authorization",
                                        "Bearer token-staff"
                                )
                )
                .andExpect(
                        status().isForbidden()
                );
    }

    @Test
    void deveRetornar401AoAcessarAreaHospedeSemAutenticacao()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/app/hospedes"
                        )
                )
                .andExpect(
                        status().isUnauthorized()
                );
    }

    @Test
    void devePermitirHospedeAcessarMinhaViagem()
            throws Exception {

        UserDetails hospede = User
                .withUsername("hospede@beattrips.com")
                .password("senha")
                .roles("HOSPEDE")
                .build();

        when(
                jwtService.extrairEmail(
                        "token-hospede"
                )
        ).thenReturn(
                "hospede@beattrips.com"
        );

        when(
                usuarioDetailsService.loadUserByUsername(
                        "hospede@beattrips.com"
                )
        ).thenReturn(
                hospede
        );

        when(
                jwtService.tokenValido(
                        "token-hospede",
                        hospede
                )
        ).thenReturn(
                true
        );

        MinhaViagemResponse response =
                new MinhaViagemResponse();

        response.setHospedeId(10L);
        response.setHospedeNome("Lucas Cesar");

        response.setViagemId(20L);
        response.setViagemNome(
                "Tomorrowland Brasil 2027"
        );
        response.setEvento(
                "Tomorrowland Brasil"
        );

        response.setDataInicio(
                LocalDate.of(
                        2027,
                        4,
                        30
                )
        );

        response.setDataFim(
                LocalDate.of(
                        2027,
                        5,
                        2
                )
        );

        response.setEndereco(
                "Chácara Beat Trips"
        );
        response.setCidade(
                "Alumínio"
        );
        response.setEstado(
                "SP"
        );

        response.setQuartoId(30L);
        response.setQuartoNome(
                "Suíte 01"
        );

        when(
                appHospedeService.buscarMinhaViagem(
                        "hospede@beattrips.com"
                )
        ).thenReturn(
                response
        );

        mockMvc.perform(
                        get(
                                "/api/app/viagem"
                        )
                                .header(
                                        "Authorization",
                                        "Bearer token-hospede"
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.hospedeId")
                                .value(10L)
                )
                .andExpect(
                        jsonPath("$.hospedeNome")
                                .value("Lucas Cesar")
                )
                .andExpect(
                        jsonPath("$.viagemId")
                                .value(20L)
                )
                .andExpect(
                        jsonPath("$.viagemNome")
                                .value(
                                        "Tomorrowland Brasil 2027"
                                )
                )
                .andExpect(
                        jsonPath("$.evento")
                                .value(
                                        "Tomorrowland Brasil"
                                )
                )
                .andExpect(
                        jsonPath("$.dataInicio")
                                .value("2027-04-30")
                )
                .andExpect(
                        jsonPath("$.dataFim")
                                .value("2027-05-02")
                )
                .andExpect(
                        jsonPath("$.cidade")
                                .value("Alumínio")
                )
                .andExpect(
                        jsonPath("$.estado")
                                .value("SP")
                )
                .andExpect(
                        jsonPath("$.quartoId")
                                .value(30L)
                )
                .andExpect(
                        jsonPath("$.quartoNome")
                                .value("Suíte 01")
                );
    }

    @Test
    void deveRetornarMinhaViagemSemQuartoQuandoAindaNaoExisteAlocacao()
            throws Exception {

        UserDetails hospede = User
                .withUsername("hospede@beattrips.com")
                .password("senha")
                .roles("HOSPEDE")
                .build();

        when(
                jwtService.extrairEmail("token-hospede")
        ).thenReturn(
                "hospede@beattrips.com"
        );

        when(
                usuarioDetailsService.loadUserByUsername(
                        "hospede@beattrips.com"
                )
        ).thenReturn(
                hospede
        );

        when(
                jwtService.tokenValido(
                        "token-hospede",
                        hospede
                )
        ).thenReturn(
                true
        );

        MinhaViagemResponse response =
                new MinhaViagemResponse();

        response.setHospedeId(10L);
        response.setHospedeNome("Lucas Cesar");
        response.setViagemId(20L);
        response.setViagemNome(
                "Tomorrowland Brasil 2027"
        );

        response.setQuartoId(null);
        response.setQuartoNome(null);

        when(
                appHospedeService.buscarMinhaViagem(
                        "hospede@beattrips.com"
                )
        ).thenReturn(
                response
        );

        mockMvc.perform(
                        get("/api/app/viagem")
                                .header(
                                        "Authorization",
                                        "Bearer token-hospede"
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.viagemId")
                                .value(20L)
                )
                .andExpect(
                        jsonPath("$.quartoId")
                                .doesNotExist()
                )
                .andExpect(
                        jsonPath("$.quartoNome")
                                .doesNotExist()
                );
    }

    @Test
    void deveRetornar204QuandoHospedeNaoPossuiViagemVinculada()
            throws Exception {

        UserDetails hospede = User
                .withUsername("hospede@beattrips.com")
                .password("senha")
                .roles("HOSPEDE")
                .build();

        when(
                jwtService.extrairEmail("token-hospede")
        ).thenReturn(
                "hospede@beattrips.com"
        );

        when(
                usuarioDetailsService.loadUserByUsername(
                        "hospede@beattrips.com"
                )
        ).thenReturn(
                hospede
        );

        when(
                jwtService.tokenValido(
                        "token-hospede",
                        hospede
                )
        ).thenReturn(
                true
        );

        when(
                appHospedeService.buscarMinhaViagem(
                        "hospede@beattrips.com"
                )
        ).thenReturn(
                null
        );

        mockMvc.perform(
                        get("/api/app/viagem")
                                .header(
                                        "Authorization",
                                        "Bearer token-hospede"
                                )
                )
                .andExpect(
                        status().isNoContent()
                );
    }

    @Test
    void deveBloquearAdminAoAcessarMinhaViagem()
            throws Exception {

        UserDetails admin = User
                .withUsername("admin@beattrips.com")
                .password("senha")
                .roles("ADMIN")
                .build();

        when(
                jwtService.extrairEmail("token-admin")
        ).thenReturn(
                "admin@beattrips.com"
        );

        when(
                usuarioDetailsService.loadUserByUsername(
                        "admin@beattrips.com"
                )
        ).thenReturn(
                admin
        );

        when(
                jwtService.tokenValido(
                        "token-admin",
                        admin
                )
        ).thenReturn(
                true
        );

        mockMvc.perform(
                        get("/api/app/viagem")
                                .header(
                                        "Authorization",
                                        "Bearer token-admin"
                                )
                )
                .andExpect(
                        status().isForbidden()
                );
    }

    @Test
    void deveBloquearStaffAoAcessarMinhaViagem()
            throws Exception {

        UserDetails staff = User
                .withUsername("staff@beattrips.com")
                .password("senha")
                .roles("STAFF")
                .build();

        when(
                jwtService.extrairEmail("token-staff")
        ).thenReturn(
                "staff@beattrips.com"
        );

        when(
                usuarioDetailsService.loadUserByUsername(
                        "staff@beattrips.com"
                )
        ).thenReturn(
                staff
        );

        when(
                jwtService.tokenValido(
                        "token-staff",
                        staff
                )
        ).thenReturn(
                true
        );

        mockMvc.perform(
                        get("/api/app/viagem")
                                .header(
                                        "Authorization",
                                        "Bearer token-staff"
                                )
                )
                .andExpect(
                        status().isForbidden()
                );
    }

    @Test
    void devePermitirHospedeAcessarPropriosTraslados()
            throws Exception {

        UserDetails hospede = User
                .withUsername("hospede@beattrips.com")
                .password("senha")
                .roles("HOSPEDE")
                .build();

        when(
                jwtService.extrairEmail(
                        "token-hospede"
                )
        ).thenReturn(
                "hospede@beattrips.com"
        );

        when(
                usuarioDetailsService.loadUserByUsername(
                        "hospede@beattrips.com"
                )
        ).thenReturn(
                hospede
        );

        when(
                jwtService.tokenValido(
                        "token-hospede",
                        hospede
                )
        ).thenReturn(
                true
        );

        MeuTrasladoResponse traslado =
                new MeuTrasladoResponse();

        traslado.setId(30L);

        traslado.setTipo(
                TipoTraslado.AEROPORTO_PARA_HOSPEDAGEM
        );

        traslado.setDataHoraPrevista(
                LocalDateTime.of(
                        2027,
                        4,
                        29,
                        16,
                        0
                )
        );

        traslado.setLocalOrigem(
                "Terminal 2 - Guarulhos"
        );

        traslado.setLocalDestino(
                "Chácara Beat Trips"
        );

        traslado.setAeroporto(
                Aeroporto.GRU
        );

        traslado.setNumeroVoo(
                "LA1234"
        );

        traslado.setCompanhiaAerea(
                "LATAM"
        );

        traslado.setStatus(
                StatusTraslado.EM_ANDAMENTO
        );

        when(
                appHospedeService.buscarMeusTraslados(
                        "hospede@beattrips.com"
                )
        ).thenReturn(
                List.of(traslado)
        );

        mockMvc.perform(
                        get(
                                "/api/app/traslados"
                        )
                                .header(
                                        "Authorization",
                                        "Bearer token-hospede"
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$[0].id")
                                .value(30L)
                )
                .andExpect(
                        jsonPath("$[0].tipo")
                                .value(
                                        "AEROPORTO_PARA_HOSPEDAGEM"
                                )
                )
                .andExpect(
                        jsonPath("$[0].dataHoraPrevista")
                                .value(
                                        "2027-04-29T16:00:00"
                                )
                )
                .andExpect(
                        jsonPath("$[0].localOrigem")
                                .value(
                                        "Terminal 2 - Guarulhos"
                                )
                )
                .andExpect(
                        jsonPath("$[0].localDestino")
                                .value(
                                        "Chácara Beat Trips"
                                )
                )
                .andExpect(
                        jsonPath("$[0].aeroporto")
                                .value("GRU")
                )
                .andExpect(
                        jsonPath("$[0].numeroVoo")
                                .value("LA1234")
                )
                .andExpect(
                        jsonPath("$[0].companhiaAerea")
                                .value("LATAM")
                )
                .andExpect(
                        jsonPath("$[0].status")
                                .value("EM_ANDAMENTO")
                );
    }

    @Test
    void deveRetornarListaVaziaQuandoHospedeNaoPossuiTraslados()
            throws Exception {

        UserDetails hospede = User
                .withUsername("hospede@beattrips.com")
                .password("senha")
                .roles("HOSPEDE")
                .build();

        when(
                jwtService.extrairEmail(
                        "token-hospede"
                )
        ).thenReturn(
                "hospede@beattrips.com"
        );

        when(
                usuarioDetailsService.loadUserByUsername(
                        "hospede@beattrips.com"
                )
        ).thenReturn(
                hospede
        );

        when(
                jwtService.tokenValido(
                        "token-hospede",
                        hospede
                )
        ).thenReturn(
                true
        );

        when(
                appHospedeService.buscarMeusTraslados(
                        "hospede@beattrips.com"
                )
        ).thenReturn(
                List.of()
        );

        mockMvc.perform(
                        get(
                                "/api/app/traslados"
                        )
                                .header(
                                        "Authorization",
                                        "Bearer token-hospede"
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$")
                                .isArray()
                )
                .andExpect(
                        jsonPath("$")
                                .isEmpty()
                );
    }

    @Test
    void deveBloquearAdminAoAcessarMeusTraslados()
            throws Exception {

        UserDetails admin = User
                .withUsername("admin@beattrips.com")
                .password("senha")
                .roles("ADMIN")
                .build();

        when(
                jwtService.extrairEmail(
                        "token-admin"
                )
        ).thenReturn(
                "admin@beattrips.com"
        );

        when(
                usuarioDetailsService.loadUserByUsername(
                        "admin@beattrips.com"
                )
        ).thenReturn(
                admin
        );

        when(
                jwtService.tokenValido(
                        "token-admin",
                        admin
                )
        ).thenReturn(
                true
        );

        mockMvc.perform(
                        get(
                                "/api/app/traslados"
                        )
                                .header(
                                        "Authorization",
                                        "Bearer token-admin"
                                )
                )
                .andExpect(
                        status().isForbidden()
                );
    }

    @Test
    void deveBloquearStaffAoAcessarMeusTraslados()
            throws Exception {

        UserDetails staff = User
                .withUsername("staff@beattrips.com")
                .password("senha")
                .roles("STAFF")
                .build();

        when(
                jwtService.extrairEmail(
                        "token-staff"
                )
        ).thenReturn(
                "staff@beattrips.com"
        );

        when(
                usuarioDetailsService.loadUserByUsername(
                        "staff@beattrips.com"
                )
        ).thenReturn(
                staff
        );

        when(
                jwtService.tokenValido(
                        "token-staff",
                        staff
                )
        ).thenReturn(
                true
        );

        mockMvc.perform(
                        get(
                                "/api/app/traslados"
                        )
                                .header(
                                        "Authorization",
                                        "Bearer token-staff"
                                )
                )
                .andExpect(
                        status().isForbidden()
                );
    }

    @Test
    void deveRetornar401AoAcessarMeusTrasladosSemAutenticacao()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/app/traslados"
                        )
                )
                .andExpect(
                        status().isUnauthorized()
                );
    }

    @Test
    void devePermitirHospedeAcessarMeuCheckIn()
            throws Exception {

        UserDetails hospede = User
                .withUsername("hospede@beattrips.com")
                .password("senha")
                .roles("HOSPEDE")
                .build();

        when(
                jwtService.extrairEmail(
                        "token-hospede"
                )
        ).thenReturn(
                "hospede@beattrips.com"
        );

        when(
                usuarioDetailsService.loadUserByUsername(
                        "hospede@beattrips.com"
                )
        ).thenReturn(
                hospede
        );

        when(
                jwtService.tokenValido(
                        "token-hospede",
                        hospede
                )
        ).thenReturn(
                true
        );

        MeuCheckInResponse response =
                new MeuCheckInResponse();

        response.setHospedeNome(
                "Lucas Cesar"
        );

        response.setStatusCheckIn(
                StatusCheckIn.PENDENTE
        );

        when(
                appHospedeService.buscarMeuCheckIn(
                        "hospede@beattrips.com"
                )
        ).thenReturn(
                response
        );

        mockMvc.perform(
                        get(
                                "/api/app/check-in"
                        )
                                .header(
                                        "Authorization",
                                        "Bearer token-hospede"
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.hospedeNome")
                                .value("Lucas Cesar")
                )
                .andExpect(
                        jsonPath("$.statusCheckIn")
                                .value("PENDENTE")
                );
    }

    @Test
    void deveRetornar204QuandoHospedeNaoPossuiCheckInVinculado()
            throws Exception {

        UserDetails hospede = User
                .withUsername("hospede@beattrips.com")
                .password("senha")
                .roles("HOSPEDE")
                .build();

        when(
                jwtService.extrairEmail(
                        "token-hospede"
                )
        ).thenReturn(
                "hospede@beattrips.com"
        );

        when(
                usuarioDetailsService.loadUserByUsername(
                        "hospede@beattrips.com"
                )
        ).thenReturn(
                hospede
        );

        when(
                jwtService.tokenValido(
                        "token-hospede",
                        hospede
                )
        ).thenReturn(
                true
        );

        when(
                appHospedeService.buscarMeuCheckIn(
                        "hospede@beattrips.com"
                )
        ).thenReturn(
                null
        );

        mockMvc.perform(
                        get(
                                "/api/app/check-in"
                        )
                                .header(
                                        "Authorization",
                                        "Bearer token-hospede"
                                )
                )
                .andExpect(
                        status().isNoContent()
                );
    }

    @Test
    void deveBloquearAdminAoAcessarMeuCheckIn()
            throws Exception {

        UserDetails admin = User
                .withUsername("admin@beattrips.com")
                .password("senha")
                .roles("ADMIN")
                .build();

        when(
                jwtService.extrairEmail(
                        "token-admin"
                )
        ).thenReturn(
                "admin@beattrips.com"
        );

        when(
                usuarioDetailsService.loadUserByUsername(
                        "admin@beattrips.com"
                )
        ).thenReturn(
                admin
        );

        when(
                jwtService.tokenValido(
                        "token-admin",
                        admin
                )
        ).thenReturn(
                true
        );

        mockMvc.perform(
                        get(
                                "/api/app/check-in"
                        )
                                .header(
                                        "Authorization",
                                        "Bearer token-admin"
                                )
                )
                .andExpect(
                        status().isForbidden()
                );
    }

    @Test
    void deveBloquearStaffAoAcessarMeuCheckIn()
            throws Exception {

        UserDetails staff = User
                .withUsername("staff@beattrips.com")
                .password("senha")
                .roles("STAFF")
                .build();

        when(
                jwtService.extrairEmail(
                        "token-staff"
                )
        ).thenReturn(
                "staff@beattrips.com"
        );

        when(
                usuarioDetailsService.loadUserByUsername(
                        "staff@beattrips.com"
                )
        ).thenReturn(
                staff
        );

        when(
                jwtService.tokenValido(
                        "token-staff",
                        staff
                )
        ).thenReturn(
                true
        );

        mockMvc.perform(
                        get(
                                "/api/app/check-in"
                        )
                                .header(
                                        "Authorization",
                                        "Bearer token-staff"
                                )
                )
                .andExpect(
                        status().isForbidden()
                );
    }

    @Test
    void deveRetornar401AoAcessarMeuCheckInSemAutenticacao()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/app/check-in"
                        )
                )
                .andExpect(
                        status().isUnauthorized()
                );
    }

    @Test
    void devePermitirHospedeAcessarProprioQrCode()
            throws Exception {

        UserDetails hospede = User
                .withUsername("hospede@beattrips.com")
                .password("senha")
                .roles("HOSPEDE")
                .build();

        when(
                jwtService.extrairEmail(
                        "token-hospede"
                )
        ).thenReturn(
                "hospede@beattrips.com"
        );

        when(
                usuarioDetailsService.loadUserByUsername(
                        "hospede@beattrips.com"
                )
        ).thenReturn(
                hospede
        );

        when(
                jwtService.tokenValido(
                        "token-hospede",
                        hospede
                )
        ).thenReturn(
                true
        );

        byte[] imagem =
                new byte[]{1, 2, 3, 4};

        when(
                appHospedeService.buscarMeuQrCode(
                        "hospede@beattrips.com"
                )
        ).thenReturn(
                imagem
        );

        mockMvc.perform(
                        get(
                                "/api/app/check-in/qr"
                        )
                                .header(
                                        "Authorization",
                                        "Bearer token-hospede"
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        content().contentType(
                                "image/png"
                        )
                )
                .andExpect(
                        content().bytes(
                                imagem
                        )
                );
    }

    @Test
    void deveRetornar204QuandoHospedeNaoPossuiQrCode()
            throws Exception {

        UserDetails hospede = User
                .withUsername("hospede@beattrips.com")
                .password("senha")
                .roles("HOSPEDE")
                .build();

        when(
                jwtService.extrairEmail(
                        "token-hospede"
                )
        ).thenReturn(
                "hospede@beattrips.com"
        );

        when(
                usuarioDetailsService.loadUserByUsername(
                        "hospede@beattrips.com"
                )
        ).thenReturn(
                hospede
        );

        when(
                jwtService.tokenValido(
                        "token-hospede",
                        hospede
                )
        ).thenReturn(
                true
        );

        when(
                appHospedeService.buscarMeuQrCode(
                        "hospede@beattrips.com"
                )
        ).thenReturn(
                null
        );

        mockMvc.perform(
                        get(
                                "/api/app/check-in/qr"
                        )
                                .header(
                                        "Authorization",
                                        "Bearer token-hospede"
                                )
                )
                .andExpect(
                        status().isNoContent()
                );
    }

    @Test
    void deveBloquearAdminAoAcessarQrCodeDoHospede()
            throws Exception {

        UserDetails admin = User
                .withUsername("admin@beattrips.com")
                .password("senha")
                .roles("ADMIN")
                .build();

        when(
                jwtService.extrairEmail(
                        "token-admin"
                )
        ).thenReturn(
                "admin@beattrips.com"
        );

        when(
                usuarioDetailsService.loadUserByUsername(
                        "admin@beattrips.com"
                )
        ).thenReturn(
                admin
        );

        when(
                jwtService.tokenValido(
                        "token-admin",
                        admin
                )
        ).thenReturn(
                true
        );

        mockMvc.perform(
                        get(
                                "/api/app/check-in/qr"
                        )
                                .header(
                                        "Authorization",
                                        "Bearer token-admin"
                                )
                )
                .andExpect(
                        status().isForbidden()
                );
    }

    @Test
    void deveBloquearStaffAoAcessarQrCodeDoHospede()
            throws Exception {

        UserDetails staff = User
                .withUsername("staff@beattrips.com")
                .password("senha")
                .roles("STAFF")
                .build();

        when(
                jwtService.extrairEmail(
                        "token-staff"
                )
        ).thenReturn(
                "staff@beattrips.com"
        );

        when(
                usuarioDetailsService.loadUserByUsername(
                        "staff@beattrips.com"
                )
        ).thenReturn(
                staff
        );

        when(
                jwtService.tokenValido(
                        "token-staff",
                        staff
                )
        ).thenReturn(
                true
        );

        mockMvc.perform(
                        get(
                                "/api/app/check-in/qr"
                        )
                                .header(
                                        "Authorization",
                                        "Bearer token-staff"
                                )
                )
                .andExpect(
                        status().isForbidden()
                );
    }

    @Test
    void deveRetornar401AoAcessarQrCodeSemAutenticacao()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/app/check-in/qr"
                        )
                )
                .andExpect(
                        status().isUnauthorized()
                );
    }

    @Test
    void devePermitirHospedeAcessarMeuQuarto()
            throws Exception {

        UserDetails hospede = User
                .withUsername("hospede@beattrips.com")
                .password("senha")
                .roles("HOSPEDE")
                .build();

        when(
                jwtService.extrairEmail(
                        "token-hospede"
                )
        ).thenReturn(
                "hospede@beattrips.com"
        );

        when(
                usuarioDetailsService.loadUserByUsername(
                        "hospede@beattrips.com"
                )
        ).thenReturn(
                hospede
        );

        when(
                jwtService.tokenValido(
                        "token-hospede",
                        hospede
                )
        ).thenReturn(
                true
        );

        MeuQuartoResponse response =
                new MeuQuartoResponse();

        response.setQuartoId(
                30L
        );

        response.setQuartoNome(
                "Suíte 01"
        );

        response.setQuartoTipo(
                TipoQuarto.SUITE
        );

        response.setTipoCama(
                TipoCama.CASAL
        );

        response.setCapacidade(
                4
        );

        response.setViagemId(
                20L
        );

        response.setViagemNome(
                "Tomorrowland Brasil 2027"
        );

        when(
                appHospedeService.buscarMeuQuarto(
                        "hospede@beattrips.com"
                )
        ).thenReturn(
                response
        );

        mockMvc.perform(
                        get(
                                "/api/app/quarto"
                        )
                                .header(
                                        "Authorization",
                                        "Bearer token-hospede"
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.quartoId")
                                .value(30L)
                )
                .andExpect(
                        jsonPath("$.quartoNome")
                                .value("Suíte 01")
                )
                .andExpect(
                        jsonPath("$.quartoTipo")
                                .value("SUITE")
                )

                .andExpect(
                        jsonPath("$.tipoCama")
                                .value("CASAL")
                )
                .andExpect(
                        jsonPath("$.capacidade")
                                .value(4)
                )
                .andExpect(
                        jsonPath("$.viagemId")
                                .value(20L)
                )
                .andExpect(
                        jsonPath("$.viagemNome")
                                .value(
                                        "Tomorrowland Brasil 2027"
                                )
                );
    }

    @Test
    void devePermitirHospedeAcessarMinhaHospedagem()
            throws Exception {

        UserDetails hospede = User
                .withUsername("hospede@beattrips.com")
                .password("senha")
                .roles("HOSPEDE")
                .build();

        when(
                jwtService.extrairEmail(
                        "token-hospede"
                )
        ).thenReturn(
                "hospede@beattrips.com"
        );

        when(
                usuarioDetailsService.loadUserByUsername(
                        "hospede@beattrips.com"
                )
        ).thenReturn(
                hospede
        );

        when(
                jwtService.tokenValido(
                        "token-hospede",
                        hospede
                )
        ).thenReturn(
                true
        );

        MinhaHospedagemResponse response =
                new MinhaHospedagemResponse();

        response.setHospedagemId(
                40L
        );

        response.setNome(
                "Chácara Beat Trips"
        );

        response.setEndereco(
                "Estrada Exemplo, 100"
        );

        response.setCidade(
                "Alumínio"
        );

        response.setEstado(
                "SP"
        );

        response.setWifiNome(
                "Beat Trips"
        );

        response.setWifiSenha(
                "senha123"
        );

        response.setViagemId(
                20L
        );

        response.setViagemNome(
                "Tomorrowland Brasil 2027"
        );

        when(
                appHospedeService.buscarMinhaHospedagem(
                        "hospede@beattrips.com"
                )
        ).thenReturn(
                response
        );

        mockMvc.perform(
                        get(
                                "/api/app/hospedagem"
                        )
                                .header(
                                        "Authorization",
                                        "Bearer token-hospede"
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.hospedagemId")
                                .value(40L)
                )
                .andExpect(
                        jsonPath("$.nome")
                                .value(
                                        "Chácara Beat Trips"
                                )
                )
                .andExpect(
                        jsonPath("$.cidade")
                                .value(
                                        "Alumínio"
                                )
                )
                .andExpect(
                        jsonPath("$.estado")
                                .value(
                                        "SP"
                                )
                )
                .andExpect(
                        jsonPath("$.wifiNome")
                                .value(
                                        "Beat Trips"
                                )
                )
                .andExpect(
                        jsonPath("$.wifiSenha")
                                .value(
                                        "senha123"
                                )
                )
                .andExpect(
                        jsonPath("$.viagemId")
                                .value(20L)
                )
                .andExpect(
                        jsonPath("$.viagemNome")
                                .value(
                                        "Tomorrowland Brasil 2027"
                                )
                );
    }

    @Test
    void deveRetornar204QuandoHospedagemAindaNaoEstiverDefinida()
            throws Exception {

        UserDetails hospede = User
                .withUsername("hospede@beattrips.com")
                .password("senha")
                .roles("HOSPEDE")
                .build();

        when(
                jwtService.extrairEmail(
                        "token-hospede"
                )
        ).thenReturn(
                "hospede@beattrips.com"
        );

        when(
                usuarioDetailsService.loadUserByUsername(
                        "hospede@beattrips.com"
                )
        ).thenReturn(
                hospede
        );

        when(
                jwtService.tokenValido(
                        "token-hospede",
                        hospede
                )
        ).thenReturn(
                true
        );

        when(
                appHospedeService.buscarMinhaHospedagem(
                        "hospede@beattrips.com"
                )
        ).thenReturn(
                null
        );

        mockMvc.perform(
                        get(
                                "/api/app/hospedagem"
                        )
                                .header(
                                        "Authorization",
                                        "Bearer token-hospede"
                                )
                )
                .andExpect(
                        status().isNoContent()
                );
    }

    @Test
    void deveBloquearAdminAoAcessarMinhaHospedagem()
            throws Exception {

        UserDetails admin = User
                .withUsername("admin@beattrips.com")
                .password("senha")
                .roles("ADMIN")
                .build();

        when(
                jwtService.extrairEmail(
                        "token-admin"
                )
        ).thenReturn(
                "admin@beattrips.com"
        );

        when(
                usuarioDetailsService.loadUserByUsername(
                        "admin@beattrips.com"
                )
        ).thenReturn(
                admin
        );

        when(
                jwtService.tokenValido(
                        "token-admin",
                        admin
                )
        ).thenReturn(
                true
        );

        mockMvc.perform(
                        get(
                                "/api/app/hospedagem"
                        )
                                .header(
                                        "Authorization",
                                        "Bearer token-admin"
                                )
                )
                .andExpect(
                        status().isForbidden()
                );
    }

    @Test
    void deveRetornar401AoAcessarMinhaHospedagemSemAutenticacao()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/app/hospedagem"
                        )
                )
                .andExpect(
                        status().isUnauthorized()
                );
    }


    @Test
    void deveRetornar204QuandoHospedeAindaNaoPossuiQuarto()
            throws Exception {

        UserDetails hospede = User
                .withUsername("hospede@beattrips.com")
                .password("senha")
                .roles("HOSPEDE")
                .build();

        when(
                jwtService.extrairEmail(
                        "token-hospede"
                )
        ).thenReturn(
                "hospede@beattrips.com"
        );

        when(
                usuarioDetailsService.loadUserByUsername(
                        "hospede@beattrips.com"
                )
        ).thenReturn(
                hospede
        );

        when(
                jwtService.tokenValido(
                        "token-hospede",
                        hospede
                )
        ).thenReturn(
                true
        );

        when(
                appHospedeService.buscarMeuQuarto(
                        "hospede@beattrips.com"
                )
        ).thenReturn(
                null
        );

        mockMvc.perform(
                        get(
                                "/api/app/quarto"
                        )
                                .header(
                                        "Authorization",
                                        "Bearer token-hospede"
                                )
                )
                .andExpect(
                        status().isNoContent()
                );
    }

    @Test
    void deveBloquearAdminAoAcessarMeuQuarto()
            throws Exception {

        UserDetails admin = User
                .withUsername("admin@beattrips.com")
                .password("senha")
                .roles("ADMIN")
                .build();

        when(
                jwtService.extrairEmail(
                        "token-admin"
                )
        ).thenReturn(
                "admin@beattrips.com"
        );

        when(
                usuarioDetailsService.loadUserByUsername(
                        "admin@beattrips.com"
                )
        ).thenReturn(
                admin
        );

        when(
                jwtService.tokenValido(
                        "token-admin",
                        admin
                )
        ).thenReturn(
                true
        );

        mockMvc.perform(
                        get(
                                "/api/app/quarto"
                        )
                                .header(
                                        "Authorization",
                                        "Bearer token-admin"
                                )
                )
                .andExpect(
                        status().isForbidden()
                );
    }

    @Test
    void deveBloquearStaffAoAcessarMeuQuarto()
            throws Exception {

        UserDetails staff = User
                .withUsername("staff@beattrips.com")
                .password("senha")
                .roles("STAFF")
                .build();

        when(
                jwtService.extrairEmail(
                        "token-staff"
                )
        ).thenReturn(
                "staff@beattrips.com"
        );

        when(
                usuarioDetailsService.loadUserByUsername(
                        "staff@beattrips.com"
                )
        ).thenReturn(
                staff
        );

        when(
                jwtService.tokenValido(
                        "token-staff",
                        staff
                )
        ).thenReturn(
                true
        );

        mockMvc.perform(
                        get(
                                "/api/app/quarto"
                        )
                                .header(
                                        "Authorization",
                                        "Bearer token-staff"
                                )
                )
                .andExpect(
                        status().isForbidden()
                );
    }

    @Test
    void deveRetornar401AoAcessarMeuQuartoSemAutenticacao()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/api/app/quarto"
                        )
                )
                .andExpect(
                        status().isUnauthorized()
                );
    }


}