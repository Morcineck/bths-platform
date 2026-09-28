package com.bths.platform.app;

import com.bths.platform.hospede.HospedeService;
import com.bths.platform.hospede.dto.HospedeResponse;
import com.bths.platform.security.JwtService;
import com.bths.platform.security.UsuarioDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

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
}