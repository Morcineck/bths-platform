package com.bths.platform.security;

import com.bths.platform.security.dto.LoginResponse;
import com.bths.platform.usuario.UsuarioService;
import com.bths.platform.usuario.dto.UsuarioResponse;
import com.bths.platform.usuario.enums.PerfilUsuario;
import com.bths.platform.viagem.ViagemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@SpringBootTest
@AutoConfigureMockMvc
class SecurityAuthorizationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService usuarioService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UsuarioDetailsService usuarioDetailsService;

    @MockitoBean
    private ViagemService viagemService;

    @MockitoBean
    private AuthService authService;



    @Test
    void deveRetornar401QuandoAcessarUsuariosSemAutenticacao()
            throws Exception {

        UUID id = UUID.randomUUID();

        mockMvc.perform(
                        get("/api/usuarios/{id}", id)
                )
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(
                        "application/json"
                ))
                .andExpect(jsonPath("$.erro")
                        .value("Unauthorized"))
                .andExpect(jsonPath("$.mensagem")
                        .value(
                                "Autenticação necessária para acessar este recurso!"
                        ))
                .andExpect(jsonPath("$.status")
                        .value(401));
    }

    @Test
    void deveRetornar403QuandoStaffAcessarUsuarios()
            throws Exception {

        UUID id = UUID.randomUUID();

        UserDetails staff = User
                .withUsername("staff@beattrips.com")
                .password("senha")
                .roles("STAFF")
                .build();

        when(jwtService.extrairEmail("token-staff"))
                .thenReturn("staff@beattrips.com");

        when(usuarioDetailsService.loadUserByUsername(
                "staff@beattrips.com"
        )).thenReturn(staff);

        when(jwtService.tokenValido(
                "token-staff",
                staff
        )).thenReturn(true);

        mockMvc.perform(
                        get("/api/usuarios/{id}", id)
                                .header(
                                        "Authorization",
                                        "Bearer token-staff"
                                )
                )
                .andExpect(status().isForbidden())
                .andExpect(content().contentTypeCompatibleWith(
                        "application/json"
                ))
                .andExpect(jsonPath("$.erro")
                        .value("Forbidden"))
                .andExpect(jsonPath("$.mensagem")
                        .value(
                                "Você não possui permissão para acessar este recurso!"
                        ))
                .andExpect(jsonPath("$.status")
                        .value(403));
    }

    @Test
    void devePermitirAdminAcessarUsuarios()
            throws Exception {

        UUID id = UUID.randomUUID();

        UserDetails admin = User
                .withUsername("admin@beattrips.com")
                .password("senha")
                .roles("ADMIN")
                .build();

        UsuarioResponse response = new UsuarioResponse();
        response.setId(id);
        response.setNome("Administrador Beat Trips");
        response.setPerfil(PerfilUsuario.ADMIN);
        response.setAtivo(true);

        when(jwtService.extrairEmail("token-admin"))
                .thenReturn("admin@beattrips.com");

        when(usuarioDetailsService.loadUserByUsername(
                "admin@beattrips.com"
        )).thenReturn(admin);

        when(jwtService.tokenValido(
                "token-admin",
                admin
        )).thenReturn(true);

        when(usuarioService.buscarUsuarioPorId(id))
                .thenReturn(response);

        mockMvc.perform(
                        get("/api/usuarios/{id}", id)
                                .header(
                                        "Authorization",
                                        "Bearer token-admin"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(id.toString()))
                .andExpect(jsonPath("$.nome")
                        .value("Administrador Beat Trips"))
                .andExpect(jsonPath("$.perfil")
                        .value("ADMIN"))
                .andExpect(jsonPath("$.ativo")
                        .value(true));
    }

    @Test
    void devePermitirStaffAcessarRotaOperacional()
            throws Exception {

        UserDetails staff = User
                .withUsername("staff@beattrips.com")
                .password("senha")
                .roles("STAFF")
                .build();

        when(jwtService.extrairEmail("token-staff"))
                .thenReturn("staff@beattrips.com");

        when(usuarioDetailsService.loadUserByUsername(
                "staff@beattrips.com"
        )).thenReturn(staff);

        when(jwtService.tokenValido(
                "token-staff",
                staff
        )).thenReturn(true);

        when(viagemService.listarViagens())
                .thenReturn(Collections.emptyList());

        mockMvc.perform(
                        get("/api/viagens")
                                .header(
                                        "Authorization",
                                        "Bearer token-staff"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void deveRetornar403QuandoHospedeAcessarRotaOperacional()
            throws Exception {

        UserDetails hospede = User
                .withUsername("hospede@beattrips.com")
                .password("senha")
                .roles("HOSPEDE")
                .build();

        when(jwtService.extrairEmail("token-hospede"))
                .thenReturn("hospede@beattrips.com");

        when(usuarioDetailsService.loadUserByUsername(
                "hospede@beattrips.com"
        )).thenReturn(hospede);

        when(jwtService.tokenValido(
                "token-hospede",
                hospede
        )).thenReturn(true);

        mockMvc.perform(
                        get("/api/viagens")
                                .header(
                                        "Authorization",
                                        "Bearer token-hospede"
                                )
                )
                .andExpect(status().isForbidden())
                .andExpect(content().contentTypeCompatibleWith(
                        "application/json"
                ))
                .andExpect(jsonPath("$.erro")
                        .value("Forbidden"))
                .andExpect(jsonPath("$.mensagem")
                        .value(
                                "Você não possui permissão para acessar este recurso!"
                        ))
                .andExpect(jsonPath("$.status")
                        .value(403));
    }

    @Test
    void devePermitirAcessoPublicoAoLogin()
            throws Exception {

        when(authService.login(any()))
                .thenReturn(
                        new LoginResponse("jwt-token-gerado")
                );

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                      "email": "admin@beattrips.com",
                                      "senha": "senha123"
                                    }
                                    """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token")
                        .value("jwt-token-gerado"));
    }
}
