package com.bths.platform.security;

import com.bths.platform.security.dto.LoginResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UsuarioDetailsService usuarioDetailsService;

    @Test
    void deveRealizarLogoutERemoverCookie()
            throws Exception {

        mockMvc.perform(
                        post("/api/auth/logout")
                                .with(csrf())
                )
                .andExpect(status().isNoContent())
                .andExpect(header().string(
                        "Set-Cookie",
                        allOf(
                                containsString("BTHS_TOKEN="),
                                containsString("HttpOnly"),
                                containsString("SameSite=Lax"),
                                containsString("Path=/"),
                                containsString("Max-Age=0")
                        )
                ))
                .andExpect(content().string(""));
    }

    @Test
    void deveLimparCacheEArmazenamentoDoNavegadorNoLogout()
            throws Exception {

        mockMvc.perform(
                        post("/api/auth/logout")
                                .with(csrf())
                )
                .andExpect(status().isNoContent())
                .andExpect(header().string(
                        "Clear-Site-Data",
                        "\"cache\", \"storage\""
                ));
    }

    @Test
    void deveRemoverCookieMesmoSemUsuarioAutenticado()
            throws Exception {

        mockMvc.perform(
                        post("/api/auth/logout")
                                .with(csrf())
                )
                .andExpect(status().isNoContent())
                .andExpect(header().string(
                        "Set-Cookie",
                        allOf(
                                containsString("BTHS_TOKEN="),
                                containsString("Max-Age=0")
                        )
                ));
    }

    @Test
    void deveExigirCsrfParaLogout()
            throws Exception {

        mockMvc.perform(
                        post("/api/auth/logout")
                )
                .andExpect(status().isForbidden())
                .andExpect(header().string(
                        "Set-Cookie",
                        not(
                                containsString(
                                        "BTHS_TOKEN="
                                )
                        )
                ));
    }

    @Test
    void deveMarcarCookieDeLogoutComoSecurePorPadrao()
            throws Exception {

        mockMvc.perform(
                        post("/api/auth/logout")
                                .with(csrf())
                )
                .andExpect(status().isNoContent())
                .andExpect(header().string(
                        "Set-Cookie",
                        containsString("Secure")
                ));
    }

    @Test
    void deveMarcarCookieDeLoginComoSecurePorPadrao()
            throws Exception {

        when(authService.login(any()))
                .thenReturn(new LoginResponse("token-de-teste"));

        mockMvc.perform(
                        post("/api/auth/login")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"email\":\"admin@beattrips.com\",\"senha\":\"qualquer\"}")
                )
                .andExpect(status().isOk())
                .andExpect(header().string(
                        "Set-Cookie",
                        allOf(
                                containsString("BTHS_TOKEN=token-de-teste"),
                                containsString("HttpOnly"),
                                containsString("Secure"),
                                containsString("SameSite=Lax")
                        )
                ));
    }
}