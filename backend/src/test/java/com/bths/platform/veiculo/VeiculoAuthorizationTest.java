package com.bths.platform.veiculo;

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

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class VeiculoAuthorizationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VeiculoService veiculoService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UsuarioDetailsService usuarioDetailsService;

    @Test
    void deveRetornar401QuandoAcessarVeiculosSemAutenticacao()
            throws Exception {

        mockMvc.perform(
                        get("/api/veiculos")
                )
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(
                        "application/json"
                ))
                .andExpect(jsonPath("$.erro")
                        .value("Unauthorized"))
                .andExpect(jsonPath("$.mensagem")
                        .value(
                                "Autenticação necessária para acessar este recurso!"
                        ))
                .andExpect(jsonPath("$.status")
                        .value(401));
    }

    @Test
    void deveRetornar403QuandoStaffAcessarVeiculos()
            throws Exception {

        UserDetails staff = User
                .withUsername("staff@beattrips.com")
                .password("senha")
                .roles("STAFF")
                .build();

        when(jwtService.extrairEmail("token-staff"))
                .thenReturn("staff@beattrips.com");

        when(usuarioDetailsService.loadUserByUsername(
                "staff@beattrips.com"
        )).thenReturn(staff);

        when(jwtService.tokenValido(
                "token-staff",
                staff
        )).thenReturn(true);

        mockMvc.perform(
                        get("/api/veiculos")
                                .header(
                                        "Authorization",
                                        "Bearer token-staff"
                                )
                )
                .andExpect(status().isForbidden())
                .andExpect(content().contentTypeCompatibleWith(
                        "application/json"
                ))
                .andExpect(jsonPath("$.erro")
                        .value("Forbidden"))
                .andExpect(jsonPath("$.mensagem")
                        .value(
                                "Você não possui permissão para acessar este recurso!"
                        ))
                .andExpect(jsonPath("$.status")
                        .value(403));
    }

    @Test
    void devePermitirAdminAcessarVeiculos()
            throws Exception {

        UserDetails admin = User
                .withUsername("admin@beattrips.com")
                .password("senha")
                .roles("ADMIN")
                .build();

        when(jwtService.extrairEmail("token-admin"))
                .thenReturn("admin@beattrips.com");

        when(usuarioDetailsService.loadUserByUsername(
                "admin@beattrips.com"
        )).thenReturn(admin);

        when(jwtService.tokenValido(
                "token-admin",
                admin
        )).thenReturn(true);

        when(veiculoService.listarVeiculos())
                .thenReturn(Collections.emptyList());

        mockMvc.perform(
                        get("/api/veiculos")
                                .header(
                                        "Authorization",
                                        "Bearer token-admin"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }
}