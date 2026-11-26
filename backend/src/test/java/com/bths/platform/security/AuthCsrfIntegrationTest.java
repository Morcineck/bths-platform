
package com.bths.platform.security;

import com.bths.platform.usuario.Usuario;
import com.bths.platform.usuario.UsuarioRepository;
import com.bths.platform.usuario.enums.PerfilUsuario;
import com.bths.platform.viagem.ViagemRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Base64;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthCsrfIntegrationTest {

    private static final String EMAIL = "integracao@beattrips.test";
    private static final String SENHA = "SenhaTeste@123";

    private static final String LOGIN_JSON = """
            {
              "email": "integracao@beattrips.test",
              "senha": "SenhaTeste@123"
            }
            """;

    private static final String VIAGEM_JSON = """
            {
              "nome": "Viagem Teste Integracao",
              "evento": "Evento BTHS",
              "dataInicio": "2027-04-30",
              "dataFim": "2027-05-02",
              "status": "PLANEJADA"
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ViagemRepository viagemRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${JWT_SECRET}")
    private String jwtSecret;

    private Usuario usuarioTeste;
    private Long viagemTesteId;

    @BeforeEach
    void configurarUsuario() {

        viagemTesteId = null;

        usuarioRepository.findByEmail(EMAIL)
                .ifPresent(usuarioRepository::delete);

        Usuario usuario = new Usuario();
        usuario.setNome("Usuario de Integracao");
        usuario.setEmail(EMAIL);
        usuario.setSenha(passwordEncoder.encode(SENHA));
        usuario.setPerfil(PerfilUsuario.ADMIN);
        usuario.setAtivo(true);

        usuarioTeste = usuarioRepository.save(usuario);
    }

    @AfterEach
    void limparDadosTeste() {

        if (viagemTesteId != null) {
            viagemRepository.findById(viagemTesteId)
                    .ifPresent(viagemRepository::delete);
        }

        usuarioRepository.findByEmail(EMAIL)
                .ifPresent(usuarioRepository::delete);
    }

    @Test
    void deveCriarUsuarioDeTeste() {

        assertNotNull(usuarioTeste.getId());
    }

    @Test
    void deveInicializarTokenCsrf() throws Exception {

        mockMvc.perform(get("/api/auth/csrf"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.headerName")
                        .value("X-XSRF-TOKEN"))
                .andExpect(jsonPath("$.parameterName")
                        .value("_csrf"));
    }

    @Test
    void deveRealizarLoginComCsrfReal() throws Exception {

        Cookie csrfCookie = inicializarCsrf();

        Cookie jwtCookie = realizarLogin(csrfCookie);

        assertTrue(jwtCookie.isHttpOnly());
        assertFalse(jwtCookie.getValue().isBlank());

        // Verifica o acesso autenticado
        mockMvc.perform(
                        get("/api/auth/me")
                                .cookie(jwtCookie)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(EMAIL))
                .andExpect(jsonPath("$.perfil").value("ADMIN"))
                .andExpect(jsonPath("$.nome")
                        .value("Usuario de Integracao"));

        // Inicializa um novo CSRF após a autenticação
        Cookie novoCsrfCookie = inicializarCsrf(jwtCookie);

        // Executa uma operação real de escrita
        MvcResult viagemResult = mockMvc.perform(
                        post("/api/viagens")
                                .cookie(jwtCookie, novoCsrfCookie)
                                .header(
                                        "X-XSRF-TOKEN",
                                        novoCsrfCookie.getValue()
                                )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(VIAGEM_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nome")
                        .value("Viagem Teste Integracao"))
                .andReturn();

        // Guarda o ID para remover apenas esta viagem
        viagemTesteId = ((Number) com.jayway.jsonpath.JsonPath.read(
                viagemResult.getResponse().getContentAsString(),
                "$.id"
        )).longValue();
    }

    @Test
    void deveRejeitarOperacaoSemCsrf() throws Exception {

        Cookie csrfCookie = inicializarCsrf();
        Cookie jwtCookie = realizarLogin(csrfCookie);

        // Confirma que o usuário está autenticado
        mockMvc.perform(
                        get("/api/auth/me")
                                .cookie(jwtCookie)
                )
                .andExpect(status().isOk());

        // JWT válido, mas CSRF ausente
        mockMvc.perform(
                        post("/api/viagens")
                                .cookie(jwtCookie)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(VIAGEM_JSON)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void deveRejeitarOperacaoComCsrfInvalido() throws Exception {

        Cookie csrfCookie = inicializarCsrf();
        Cookie jwtCookie = realizarLogin(csrfCookie);

        Cookie novoCsrfCookie = inicializarCsrf(jwtCookie);

        // JWT e cookie CSRF válidos, cabeçalho inválido
        mockMvc.perform(
                        post("/api/viagens")
                                .cookie(jwtCookie, novoCsrfCookie)
                                .header(
                                        "X-XSRF-TOKEN",
                                        "token-csrf-invalido"
                                )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(VIAGEM_JSON)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void deveRealizarLogoutERemoverCookieJwt() throws Exception {

        Cookie csrfCookie = inicializarCsrf();
        Cookie jwtCookie = realizarLogin(csrfCookie);

        Cookie novoCsrfCookie = inicializarCsrf(jwtCookie);

        // Realiza logout com CSRF válido
        MvcResult logoutResult = mockMvc.perform(
                        post("/api/auth/logout")
                                .cookie(jwtCookie, novoCsrfCookie)
                                .header(
                                        "X-XSRF-TOKEN",
                                        novoCsrfCookie.getValue()
                                )
                )
                .andExpect(status().isNoContent())
                .andReturn();

        Cookie cookieRemovido = logoutResult.getResponse()
                .getCookie("BTHS_TOKEN");

        assertNotNull(cookieRemovido);
        assertEquals(0, cookieRemovido.getMaxAge());
        assertTrue(cookieRemovido.getValue().isEmpty());

        // Simula o navegador após remover o JWT
        mockMvc.perform(
                        get("/api/auth/me")
                )
                .andExpect(status().isUnauthorized());

        // Inicializa novo CSRF e realiza outro login
        Cookie csrfNovoLogin = inicializarCsrf();
        Cookie novoJwtCookie = realizarLogin(csrfNovoLogin);

        assertFalse(novoJwtCookie.getValue().isBlank());

        // Confirma a recuperação do acesso
        mockMvc.perform(
                        get("/api/auth/me")
                                .cookie(novoJwtCookie)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(EMAIL))
                .andExpect(jsonPath("$.perfil").value("ADMIN"));
    }

    @Test
    void deveRetornar401ComJwtExpirado() throws Exception {

        byte[] chaveDecodificada =
                Base64.getDecoder().decode(jwtSecret);

        long agora = System.currentTimeMillis();

        String tokenExpirado = Jwts.builder()
                .subject(EMAIL)
                .issuedAt(new Date(agora - 120_000))
                .expiration(new Date(agora - 60_000))
                .signWith(Keys.hmacShaKeyFor(chaveDecodificada))
                .compact();

        Cookie jwtCookie = new Cookie(
                "BTHS_TOKEN",
                tokenExpirado
        );

        mockMvc.perform(
                        get("/api/auth/me")
                                .cookie(jwtCookie)
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.erro")
                        .value("Unauthorized"))
                .andExpect(jsonPath("$.status")
                        .value(401));
    }

    // Métodos auxiliares

    private Cookie inicializarCsrf() throws Exception {

        MvcResult result = mockMvc.perform(
                        get("/api/auth/csrf")
                )
                .andExpect(status().isOk())
                .andReturn();

        Cookie csrfCookie = result.getResponse()
                .getCookie("XSRF-TOKEN");

        assertNotNull(csrfCookie);

        return csrfCookie;
    }

    private Cookie inicializarCsrf(Cookie jwtCookie) throws Exception {

        MvcResult result = mockMvc.perform(
                        get("/api/auth/csrf")
                                .cookie(jwtCookie)
                )
                .andExpect(status().isOk())
                .andReturn();

        Cookie csrfCookie = result.getResponse()
                .getCookie("XSRF-TOKEN");

        assertNotNull(csrfCookie);

        return csrfCookie;
    }

    private Cookie realizarLogin(Cookie csrfCookie) throws Exception {

        MvcResult result = mockMvc.perform(
                        post("/api/auth/login")
                                .cookie(csrfCookie)
                                .header(
                                        "X-XSRF-TOKEN",
                                        csrfCookie.getValue()
                                )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(LOGIN_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        Cookie jwtCookie = result.getResponse()
                .getCookie("BTHS_TOKEN");

        assertNotNull(jwtCookie);

        return jwtCookie;
    }
}
