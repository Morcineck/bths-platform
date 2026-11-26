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

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AutorizacaoPerfilIntegrationTest {

    private static final String SENHA = "SenhaTeste@123";

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
    void prepararViagem() {

        Viagem viagem = new Viagem();
        viagem.setNome("Viagem Autorizacao BTHS");
        viagem.setEvento("Evento Teste");
        viagem.setDataInicio(LocalDate.of(2027, 4, 30));
        viagem.setDataFim(LocalDate.of(2027, 5, 2));
        viagem.setStatus(StatusViagem.PLANEJADA);

        viagemId = viagemRepository.save(viagem).getId();
    }

    @Test
    void adminDeveAcessarMotoristasETraslados() throws Exception {

        Cookie jwt = autenticar(PerfilUsuario.ADMIN);

        mockMvc.perform(
                get("/api/motoristas").cookie(jwt)
        ).andExpect(status().isOk());

        mockMvc.perform(
                get("/api/traslados/viagem/{id}", viagemId)
                        .cookie(jwt)
        ).andExpect(status().isOk());
    }

    @Test
    void staffDeveAcessarTrasladosMasNaoMotoristas()
            throws Exception {

        Cookie jwt = autenticar(PerfilUsuario.STAFF);

        mockMvc.perform(
                get("/api/traslados/viagem/{id}", viagemId)
                        .cookie(jwt)
        ).andExpect(status().isOk());

        mockMvc.perform(
                get("/api/motoristas").cookie(jwt)
        ).andExpect(status().isForbidden());
    }

    @Test
    void hospedeNaoDeveAcessarRecursosAdministrativos()
            throws Exception {

        Cookie jwt = autenticar(PerfilUsuario.HOSPEDE);

        mockMvc.perform(
                get("/api/motoristas").cookie(jwt)
        ).andExpect(status().isForbidden());

        mockMvc.perform(
                get("/api/traslados/viagem/{id}", viagemId)
                        .cookie(jwt)
        ).andExpect(status().isForbidden());
    }

    @Test
    void usuarioNaoAutenticadoDeveReceber401()
            throws Exception {

        mockMvc.perform(
                get("/api/motoristas")
        ).andExpect(status().isUnauthorized());

        mockMvc.perform(
                get("/api/traslados/viagem/{id}", viagemId)
        ).andExpect(status().isUnauthorized());
    }

    private Cookie autenticar(PerfilUsuario perfil)
            throws Exception {

        String email = "autorizacao-"
                + perfil.name().toLowerCase()
                + "@beattrips.test";

        Usuario usuario = new Usuario();
        usuario.setNome("Usuario Teste " + perfil.name());
        usuario.setEmail(email);
        usuario.setSenha(passwordEncoder.encode(SENHA));
        usuario.setPerfil(perfil);
        usuario.setAtivo(true);

        usuarioRepository.save(usuario);

        MvcResult csrfResult = mockMvc.perform(
                get("/api/auth/csrf")
        ).andExpect(status().isOk()).andReturn();

        Cookie csrf = csrfResult.getResponse()
                .getCookie("XSRF-TOKEN");

        assertNotNull(csrf);

        String loginJson = """
                {
                  "email": "%s",
                  "senha": "%s"
                }
                """.formatted(email, SENHA);

        MvcResult loginResult = mockMvc.perform(
                post("/api/auth/login")
                        .cookie(csrf)
                        .header("X-XSRF-TOKEN", csrf.getValue())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson)
        ).andExpect(status().isOk()).andReturn();

        Cookie jwt = loginResult.getResponse()
                .getCookie("BTHS_TOKEN");

        assertNotNull(jwt);

        return jwt;
    }
}
