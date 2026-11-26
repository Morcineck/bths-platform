package com.bths.platform.security;

import com.bths.platform.security.exception.LoginBloqueadoException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LoginBloqueadoRespostaTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UsuarioDetailsService usuarioDetailsService;

    @Test
    void deveResponder429ComRetryAfterQuandoOLoginEstiverBloqueado()
            throws Exception {

        when(authService.login(any()))
                .thenThrow(new LoginBloqueadoException(Duration.ofSeconds(180)));

        mockMvc.perform(
                        post("/api/auth/login")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"email\":\"admin@beattrips.com\",\"senha\":\"qualquer\"}")
                )
                .andExpect(status().isTooManyRequests())
                .andExpect(header().string("Retry-After", "180"))
                .andExpect(header().doesNotExist("Set-Cookie"))
                .andExpect(jsonPath("$.status").value(429))
                .andExpect(jsonPath("$.erro").value("Too Many Requests"));
    }

    @Test
    void deveArredondarORetryAfterParaCima()
            throws Exception {

        when(authService.login(any()))
                .thenThrow(new LoginBloqueadoException(Duration.ofMillis(90_500)));

        mockMvc.perform(
                        post("/api/auth/login")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"email\":\"admin@beattrips.com\",\"senha\":\"qualquer\"}")
                )
                .andExpect(status().isTooManyRequests())
                .andExpect(header().string("Retry-After", "91"));
    }
}