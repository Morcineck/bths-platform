package com.bths.platform.dashboard;

import com.bths.platform.dashboard.dto.DashboardResponse;
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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DashboardSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DashboardService dashboardService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UsuarioDetailsService usuarioDetailsService;



    @Test
    void devePermitirAcessoAoDashboardParaAdmin() throws Exception {

        String token = "token-admin";
        String email = "admin@bths.com";

        UserDetails admin = User.builder()
                .username(email)
                .password("senha")
                .roles("ADMIN")
                .build();

        DashboardResponse response = new DashboardResponse();
        response.setViagemId(1L);

        when(jwtService.extrairEmail(token))
                .thenReturn(email);

        when(usuarioDetailsService.loadUserByUsername(email))
                .thenReturn(admin);

        when(jwtService.tokenValido(token, admin))
                .thenReturn(true);

        when(dashboardService.buscarDashboard(1L))
                .thenReturn(response);

        mockMvc.perform(
                        get("/api/dashboard/viagens/1")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.viagemId").value(1));
    }

    @Test
    void devePermitirAcessoAoDashboardParaStaff() throws Exception {

        String token = "token-staff";
        String email = "staff@bths.com";

        UserDetails staff = User.builder()
                .username(email)
                .password("senha")
                .roles("STAFF")
                .build();

        DashboardResponse response = new DashboardResponse();
        response.setViagemId(1L);

        when(jwtService.extrairEmail(token))
                .thenReturn(email);

        when(usuarioDetailsService.loadUserByUsername(email))
                .thenReturn(staff);

        when(jwtService.tokenValido(token, staff))
                .thenReturn(true);

        when(dashboardService.buscarDashboard(1L))
                .thenReturn(response);

        mockMvc.perform(
                        get("/api/dashboard/viagens/1")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.viagemId").value(1));
    }

    @Test
    void deveNegarAcessoAoDashboardParaHospede() throws Exception {

        String token = "token-hospede";
        String email = "hospede@bths.com";

        UserDetails hospede = User.builder()
                .username(email)
                .password("senha")
                .roles("HOSPEDE")
                .build();

        when(jwtService.extrairEmail(token))
                .thenReturn(email);

        when(usuarioDetailsService.loadUserByUsername(email))
                .thenReturn(hospede);

        when(jwtService.tokenValido(token, hospede))
                .thenReturn(true);

        mockMvc.perform(
                        get("/api/dashboard/viagens/1")
                                .header(
                                        "Authorization",
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.erro")
                        .value("Forbidden"))
                .andExpect(jsonPath("$.mensagem")
                        .value("Você não possui permissão para acessar este recurso!"))
                .andExpect(jsonPath("$.status")
                        .value(403));
    }

    @Test
    void deveNegarAcessoAoDashboardSemJwt() throws Exception {

        mockMvc.perform(
                        get("/api/dashboard/viagens/1")
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.erro")
                        .value("Unauthorized"))
                .andExpect(jsonPath("$.mensagem")
                        .value("Autenticação necessária para acessar este recurso!"))
                .andExpect(jsonPath("$.status")
                        .value(401));
    }
}