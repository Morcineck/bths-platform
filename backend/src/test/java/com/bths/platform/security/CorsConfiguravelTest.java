package com.bths.platform.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "bths.cors.allowed-origins=https://www.exemplo.test,http://127.0.0.1:3000"
})
@AutoConfigureMockMvc
class CorsConfiguravelTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UsuarioDetailsService usuarioDetailsService;

    @Test
    void devePermitirOrigemConfigurada()
            throws Exception {

        mockMvc.perform(
                        options("/api/auth/login")
                                .header("Origin", "https://www.exemplo.test")
                                .header("Access-Control-Request-Method", "POST")
                )
                .andExpect(status().isOk())
                .andExpect(header().string(
                        "Access-Control-Allow-Origin",
                        "https://www.exemplo.test"
                ))
                .andExpect(header().string(
                        "Access-Control-Allow-Credentials",
                        "true"
                ));
    }

    @Test
    void deveRejeitarOrigemNaoConfigurada()
            throws Exception {

        mockMvc.perform(
                        options("/api/auth/login")
                                .header("Origin", "https://site-malicioso.test")
                                .header("Access-Control-Request-Method", "POST")
                )
                .andExpect(status().isForbidden())
                .andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
    }

    @Test
    void deveRejeitarOrigemDeDesenvolvimentoQuandoNaoConfigurada()
            throws Exception {

        mockMvc.perform(
                        options("/api/auth/login")
                                .header("Origin", "http://localhost:3000")
                                .header("Access-Control-Request-Method", "POST")
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void deveExporRequestIdParaOFrontend()
            throws Exception {

        mockMvc.perform(
                        post("/api/auth/logout")
                                .with(csrf())
                                .header("Origin", "http://127.0.0.1:3000")
                )
                .andExpect(status().isNoContent())
                .andExpect(header().string(
                        "Access-Control-Expose-Headers",
                        containsString("X-Request-Id")
                ));
    }
}