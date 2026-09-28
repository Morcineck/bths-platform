package com.bths.platform.hospede;

import com.bths.platform.hospede.dto.HospedeResponse;
import com.bths.platform.security.JwtService;
import com.bths.platform.security.UsuarioDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class HospedeAuthorizationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HospedeService hospedeService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UsuarioDetailsService usuarioDetailsService;

    @Test
    void devePermitirAdminVincularUsuarioAoHospede()
            throws Exception {

        Long hospedeId = 1L;
        UUID usuarioId = UUID.randomUUID();

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
        ).thenReturn(admin);

        when(
                jwtService.tokenValido(
                        "token-admin",
                        admin
                )
        ).thenReturn(true);

        HospedeResponse response =
                new HospedeResponse();

        response.setId(
                hospedeId
        );

        response.setNomeCompleto(
                "Lucas Cesar"
        );

        when(
                hospedeService.vincularUsuarioAoHospede(
                        hospedeId,
                        usuarioId
                )
        ).thenReturn(
                response
        );

        mockMvc.perform(
                        patch(
                                "/api/hospedes/{hospedeId}/usuario/{usuarioId}",
                                hospedeId,
                                usuarioId
                        )
                                .with(csrf())
                                .header(
                                        "Authorization",
                                        "Bearer token-admin"
                                )
                )
                .andExpect(
                        status().isOk()
                );
    }

    @Test
    void deveBloquearStaffAoVincularUsuarioAoHospede()
            throws Exception {

        Long hospedeId = 1L;
        UUID usuarioId = UUID.randomUUID();

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
        ).thenReturn(staff);

        when(
                jwtService.tokenValido(
                        "token-staff",
                        staff
                )
        ).thenReturn(true);

        mockMvc.perform(
                        patch(
                                "/api/hospedes/{hospedeId}/usuario/{usuarioId}",
                                hospedeId,
                                usuarioId
                        )
                                .with(csrf())
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
    void deveBloquearHospedeAoVincularUsuarioAoHospede()
            throws Exception {

        Long hospedeId = 1L;
        UUID usuarioId = UUID.randomUUID();

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
        ).thenReturn(hospede);

        when(
                jwtService.tokenValido(
                        "token-hospede",
                        hospede
                )
        ).thenReturn(true);

        mockMvc.perform(
                        patch(
                                "/api/hospedes/{hospedeId}/usuario/{usuarioId}",
                                hospedeId,
                                usuarioId
                        )
                                .with(csrf())
                                .header(
                                        "Authorization",
                                        "Bearer token-hospede"
                                )
                )
                .andExpect(
                        status().isForbidden()
                );
    }

    @Test
    void deveRetornar401AoVincularUsuarioSemAutenticacao()
            throws Exception {

        Long hospedeId = 1L;
        UUID usuarioId = UUID.randomUUID();

        mockMvc.perform(
                        patch(
                                "/api/hospedes/{hospedeId}/usuario/{usuarioId}",
                                hospedeId,
                                usuarioId
                        )
                                .with(csrf())
                )
                .andExpect(
                        status().isUnauthorized()
                );
    }
}