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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "bths.auth.cookie-secure=false",
        "bths.auth.cookie-same-site=Strict",
        "bths.auth.duracao-sessao=30m"
})
@AutoConfigureMockMvc
class AuthCookieConfiguravelTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UsuarioDetailsService usuarioDetailsService;

    @Test
    void deveAplicarSameSiteESemSecureConfigurados()
            throws Exception {

        mockMvc.perform(
                        post("/api/auth/logout")
                                .with(csrf())
                )
                .andExpect(status().isNoContent())
                .andExpect(header().string(
                        "Set-Cookie",
                        allOf(
                                containsString("SameSite=Strict"),
                                not(containsString("Secure"))
                        )
                ));
    }

    @Test
    void deveAplicarDuracaoConfiguradaAoCookieDeLogin()
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
                        containsString("Max-Age=1800")
                ));
    }
}