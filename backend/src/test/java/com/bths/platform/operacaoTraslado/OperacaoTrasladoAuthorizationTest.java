package com.bths.platform.operacaoTraslado;

import com.bths.platform.operacaoTraslado.dto.OperacaoTrasladoResponse;
import com.bths.platform.operacaoTraslado.dto.OperacaoTrasladoStatusRequest;
import com.bths.platform.security.JwtService;
import com.bths.platform.security.UsuarioDetailsService;
import com.bths.platform.traslado.enums.StatusTraslado;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OperacaoTrasladoAuthorizationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OperacaoTrasladoService operacaoTrasladoService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UsuarioDetailsService usuarioDetailsService;

    @Test
    void devePermitirStaffAtualizarStatusDaOperacao()
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
        ).thenReturn(staff);

        when(
                jwtService.tokenValido(
                        "token-staff",
                        staff
                )
        ).thenReturn(true);

        OperacaoTrasladoResponse response =
                new OperacaoTrasladoResponse();

        response.setId(100L);
        response.setStatus(
                StatusTraslado.EM_ANDAMENTO
        );

        when(
                operacaoTrasladoService.atualizarStatus(
                        eq(100L),
                        any(
                                OperacaoTrasladoStatusRequest.class
                        )
                )
        ).thenReturn(response);

        mockMvc.perform(
                        patch(
                                "/api/traslados/operacoes/100/status"
                        )
                                .with(csrf())
                                .header(
                                        "Authorization",
                                        "Bearer token-staff"
                                )
                                .contentType(
                                        "application/json"
                                )
                                .content("""
                                        {
                                          "status": "EM_ANDAMENTO"
                                        }
                                        """)
                )
                .andExpect(
                        status().isOk()
                );
    }

    @Test
    void deveBloquearStaffAoEditarEstruturaDaOperacao()
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
        ).thenReturn(staff);

        when(
                jwtService.tokenValido(
                        "token-staff",
                        staff
                )
        ).thenReturn(true);

        mockMvc.perform(
                        put(
                                "/api/traslados/operacoes/100"
                        )
                                .with(csrf())
                                .header(
                                        "Authorization",
                                        "Bearer token-staff"
                                )
                                .contentType(
                                        "application/json"
                                )
                                .content("{}")
                )
                .andExpect(
                        status().isForbidden()
                );
    }

    @Test
    void deveBloquearHospedeAoAtualizarStatusDaOperacao()
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
        ).thenReturn(hospede);

        when(
                jwtService.tokenValido(
                        "token-hospede",
                        hospede
                )
        ).thenReturn(true);

        mockMvc.perform(
                        patch(
                                "/api/traslados/operacoes/100/status"
                        )
                                .with(csrf())
                                .header(
                                        "Authorization",
                                        "Bearer token-hospede"
                                )
                                .contentType(
                                        "application/json"
                                )
                                .content("""
                                        {
                                          "status": "EM_ANDAMENTO"
                                        }
                                        """)
                )
                .andExpect(
                        status().isForbidden()
                );
    }
}