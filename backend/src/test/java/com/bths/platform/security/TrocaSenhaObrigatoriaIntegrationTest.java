package com.bths.platform.security;

import com.bths.platform.usuario.Usuario;
import com.bths.platform.usuario.UsuarioRepository;
import com.bths.platform.usuario.enums.PerfilUsuario;
import com.bths.platform.viagem.Viagem;
import com.bths.platform.viagem.ViagemRepository;
import com.bths.platform.viagem.enums.StatusViagem;
import jakarta.servlet.http.Cookie;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TrocaSenhaObrigatoriaIntegrationTest {

    private static final String EMAIL = "troca-senha@beattrips.test";
    private static final String SENHA_TEMPORARIA = "12345678901";
    private static final String NOVA_SENHA = "NovaSenha@2027";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ViagemRepository viagemRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Long viagemId;

    @BeforeEach
    void preparar() {

        Viagem viagem = new Viagem();
        viagem.setNome("Viagem Troca Senha BTHS");
        viagem.setEvento("Evento Teste");
        viagem.setDataInicio(LocalDate.of(2027, 4, 30));
        viagem.setDataFim(LocalDate.of(2027, 5, 2));
        viagem.setStatus(StatusViagem.PLANEJADA);

        viagemId = viagemRepository.save(viagem).getId();

        Usuario usuario = new Usuario();
        usuario.setNome("Usuario Troca Senha");
        usuario.setEmail(EMAIL);
        usuario.setSenha(passwordEncoder.encode(SENHA_TEMPORARIA));
        usuario.setPerfil(PerfilUsuario.STAFF);
        usuario.setAtivo(true);
        usuario.setTrocaSenhaObrigatoria(true);

        usuarioRepository.save(usuario);
    }

    @Test
    void comTrocaPendenteDeveBloquearRotasForaDeAuth() throws Exception {

        Cookie jwt = login(SENHA_TEMPORARIA);

        mockMvc.perform(
                get("/api/traslados/viagem/{id}", viagemId).cookie(jwt)
        ).andExpect(status().isForbidden());

        mockMvc.perform(
                get("/api/auth/me").cookie(jwt)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trocaSenhaObrigatoria").value(true));
    }

    @Test
    void depoisDaTrocaDeveLiberarAcessoEInvalidarSenhaAntiga() throws Exception {

        Cookie jwt = login(SENHA_TEMPORARIA);

        trocarSenha(jwt, SENHA_TEMPORARIA, NOVA_SENHA)
                .andExpect(status().isNoContent());

        mockMvc.perform(
                get("/api/traslados/viagem/{id}", viagemId).cookie(jwt)
        ).andExpect(status().isOk());

        mockMvc.perform(
                get("/api/auth/me").cookie(jwt)
        ).andExpect(jsonPath("$.trocaSenhaObrigatoria").value(false));

        assertFalse(usuarioRepository.findByEmail(EMAIL).orElseThrow().isTrocaSenhaObrigatoria());

        Cookie csrf = csrf();

        mockMvc.perform(
                post("/api/auth/login")
                        .cookie(csrf)
                        .header("X-XSRF-TOKEN", csrf.getValue())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson(SENHA_TEMPORARIA))
        ).andExpect(status().isUnauthorized());
    }

    @Test
    void deveRecusarSenhaAtualIncorreta() throws Exception {

        Cookie jwt = login(SENHA_TEMPORARIA);

        trocarSenha(jwt, "senhaErrada123", NOVA_SENHA)
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRecusarNovaSenhaIgualAAtual() throws Exception {

        Cookie jwt = login(SENHA_TEMPORARIA);

        trocarSenha(jwt, SENHA_TEMPORARIA, SENHA_TEMPORARIA)
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRecusarNovaSenhaCurta() throws Exception {

        Cookie jwt = login(SENHA_TEMPORARIA);

        trocarSenha(jwt, SENHA_TEMPORARIA, "curta")
                .andExpect(status().isBadRequest());
    }

    private ResultActions trocarSenha(
            Cookie jwt,
            String senhaAtual,
            String novaSenha
    ) throws Exception {

        Cookie csrf = csrf();

        String json = """
                {
                  "senhaAtual": "%s",
                  "novaSenha": "%s"
                }
                """.formatted(senhaAtual, novaSenha);

        return mockMvc.perform(
                put("/api/auth/senha")
                        .cookie(jwt, csrf)
                        .header("X-XSRF-TOKEN", csrf.getValue())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        );
    }

    private Cookie login(String senha) throws Exception {

        Cookie csrf = csrf();

        MvcResult resultado = mockMvc.perform(
                post("/api/auth/login")
                        .cookie(csrf)
                        .header("X-XSRF-TOKEN", csrf.getValue())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson(senha))
        ).andExpect(status().isOk()).andReturn();

        Cookie jwt = resultado.getResponse().getCookie("BTHS_TOKEN");

        assertNotNull(jwt);

        return jwt;
    }

    private Cookie csrf() throws Exception {

        MvcResult resultado = mockMvc.perform(
                get("/api/auth/csrf")
        ).andExpect(status().isOk()).andReturn();

        Cookie csrf = resultado.getResponse().getCookie("XSRF-TOKEN");

        assertNotNull(csrf);

        return csrf;
    }

    private String loginJson(String senha) {

        return """
                {
                  "email": "%s",
                  "senha": "%s"
                }
                """.formatted(EMAIL, senha);
    }
}
