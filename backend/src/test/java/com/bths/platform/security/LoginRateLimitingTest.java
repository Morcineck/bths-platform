package com.bths.platform.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LoginRateLimitingTest {

    private static final String SENHA_CERTA = "senha-certa";
    private static final String SENHA_ERRADA = "senha-errada";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UsuarioDetailsService usuarioDetailsService;

    @BeforeEach
    void configurarUsuarios() {
        String hash = new BCryptPasswordEncoder().encode(SENHA_CERTA);

        when(usuarioDetailsService.loadUserByUsername(anyString())).thenAnswer(invocacao -> {
            String email = invocacao.getArgument(0);
            if (email.startsWith("existe")) {
                UserDetails usuario = User.withUsername(email)
                        .password(hash)
                        .roles("HOSPEDE")
                        .build();
                return usuario;
            }
            throw new UsernameNotFoundException("Usuário não encontrado");
        });

        when(jwtService.gerarToken(any())).thenReturn("token-de-teste");
    }

    private ResultActions login(String email, String senha) throws Exception {
        return mockMvc.perform(
                post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"senha\":\"" + senha + "\"}")
        );
    }

    private void falhar(String email, int vezes) throws Exception {
        for (int i = 0; i < vezes; i++) {
            login(email, SENHA_ERRADA).andExpect(status().isUnauthorized());
        }
    }

    @Test
    void deveBloquearAposCincoFalhasERetornar429() throws Exception {
        String email = "existe-bloqueio@exemplo.com";

        falhar(email, 5);

        login(email, SENHA_ERRADA)
                .andExpect(status().isTooManyRequests())
                .andExpect(header().string("Retry-After", matchesPattern("\\d+")))
                .andExpect(header().doesNotExist("Set-Cookie"))
                .andExpect(jsonPath("$.status").value(429))
                .andExpect(content().string(not(containsString(email))));
    }

    @Test
    void naoDeveAutenticarUsuarioBloqueadoNemComASenhaCorreta() throws Exception {
        String email = "existe-senha-certa@exemplo.com";

        falhar(email, 5);

        login(email, SENHA_CERTA)
                .andExpect(status().isTooManyRequests())
                .andExpect(header().doesNotExist("Set-Cookie"));
    }

    @Test
    void naoDeveAfetarOutroEmailQuandoUmEmailForBloqueado() throws Exception {
        String bloqueado = "existe-a@exemplo.com";
        String livre = "existe-b@exemplo.com";

        falhar(bloqueado, 5);

        login(bloqueado, SENHA_ERRADA).andExpect(status().isTooManyRequests());
        login(livre, SENHA_ERRADA).andExpect(status().isUnauthorized());
        login(livre, SENHA_CERTA).andExpect(status().isOk());
    }

    @Test
    void deveZerarAContagemQuandoOLoginForBemSucedido() throws Exception {
        String email = "existe-zera@exemplo.com";

        falhar(email, 4);
        login(email, SENHA_CERTA).andExpect(status().isOk());

        falhar(email, 4);
    }

    @Test
    void deveResponderIgualParaEmailExistenteEInexistente() throws Exception {
        String existente = "existe-igual@exemplo.com";
        String inexistente = "inexistente-igual@exemplo.com";

        falhar(existente, 5);
        falhar(inexistente, 5);

        String corpoExistente = login(existente, SENHA_ERRADA)
                .andExpect(status().isTooManyRequests())
                .andReturn().getResponse().getContentAsString();

        String corpoInexistente = login(inexistente, SENHA_ERRADA)
                .andExpect(status().isTooManyRequests())
                .andReturn().getResponse().getContentAsString();

        assertEquals(corpoExistente, corpoInexistente);
    }
}