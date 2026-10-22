package com.bths.platform.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "bths.auth.cookie-secure=false",
        "bths.auth.cookie-same-site=Strict"
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
}