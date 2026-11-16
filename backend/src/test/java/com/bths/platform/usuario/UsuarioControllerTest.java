package com.bths.platform.usuario;

import com.bths.platform.security.JwtService;
import com.bths.platform.security.UsuarioDetailsService;
import com.bths.platform.usuario.dto.UsuarioResponse;
import com.bths.platform.usuario.enums.PerfilUsuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.bths.platform.usuario.exception.OperacaoUsuarioNaoPermitidaException;
import com.bths.platform.usuario.exception.PerfilUsuarioInvalidoException;
import java.util.List;
import java.util.UUID;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsuarioController.class)
@AutoConfigureMockMvc(addFilters = false)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService usuarioService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UsuarioDetailsService usuarioDetailsService;

    @Test
    void deveRetornar400QuandoCamposObrigatoriosNaoForemInformados()
            throws Exception {

        String json = """
            {
            }
            """;

        mockMvc.perform(
                        post("/api/usuarios")
                                .contentType("application/json")
                                .content(json)
                )
                .andExpect(status().isBadRequest());

        verify(usuarioService, never())
                .cadastrarUsuario(any());
    }

    @Test
    void deveRetornar400QuandoEmailForInvalido() throws Exception {

        String json = """
        {
            "nome": "Robson",
            "email": "email-invalido",
            "senha": "senha123",
            "perfil": "ADMIN"
        }
        """;

        mockMvc.perform(
                        post("/api/usuarios")
                                .contentType("application/json")
                                .content(json)
                )
                .andExpect(status().isBadRequest());

        verify(usuarioService, never())
                .cadastrarUsuario(any());
    }

    @Test
    void deveCadastrarUsuarioERetornar201() throws Exception {

        UsuarioResponse response = new UsuarioResponse();
        response.setNome("Robson");
        response.setPerfil(PerfilUsuario.ADMIN);
        response.setAtivo(true);

        when(usuarioService.cadastrarUsuario(any()))
                .thenReturn(response);

        String json = """
        {
            "nome": "Robson",
            "email": "robson@email.com",
            "senha": "senha123",
            "perfil": "ADMIN"
        }
        """;

        mockMvc.perform(
                        post("/api/usuarios")
                                .contentType("application/json")
                                .content(json)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Robson"))
                .andExpect(jsonPath("$.perfil").value("ADMIN"))
                .andExpect(jsonPath("$.ativo").value(true));


        verify(usuarioService)
                .cadastrarUsuario(any());
    }

    @Test
    void deveRetornar400QuandoSenhaTiverMenosDe8Caracteres() throws Exception {

        String json = """
        {
            "nome": "Robson",
            "email": "robson@email.com",
            "senha": "1234567",
            "perfil": "STAFF"
        }
        """;

        mockMvc.perform(
                        post("/api/usuarios")
                                .contentType("application/json")
                                .content(json)
                )
                .andExpect(status().isBadRequest());

        verify(usuarioService, never()).cadastrarUsuario(any());
    }

    @Test
    void deveListarEquipeERetornar200() throws Exception {
        UsuarioResponse staff = new UsuarioResponse();
        staff.setNome("Staff QA");
        staff.setEmail("staff@beattrips.com");
        staff.setPerfil(PerfilUsuario.STAFF);
        staff.setAtivo(true);

        when(usuarioService.listarEquipe()).thenReturn(List.of(staff));

        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Staff QA"))
                .andExpect(jsonPath("$[0].email").value("staff@beattrips.com"));
    }

    @Test
    void deveAtualizarUsuarioERetornar200() throws Exception {
        UUID id = UUID.randomUUID();
        UsuarioResponse response = new UsuarioResponse();
        response.setNome("Staff Editado");

        when(usuarioService.atualizarUsuario(eq(id), any())).thenReturn(response);

        String json = """
        {
            "nome": "Staff Editado",
            "email": "staff@beattrips.com",
            "perfil": "STAFF"
        }
        """;

        mockMvc.perform(put("/api/usuarios/" + id)
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Staff Editado"));
    }

    @Test
    void deveRetornar409QuandoOperacaoNaoForPermitida() throws Exception {
        UUID id = UUID.randomUUID();

        when(usuarioService.atualizarUsuario(eq(id), any()))
                .thenThrow(new OperacaoUsuarioNaoPermitidaException(
                        "O sistema precisa manter pelo menos um ADMIN ativo."));

        String json = """
        {
            "nome": "Admin",
            "email": "admin@beattrips.com",
            "perfil": "STAFF"
        }
        """;

        mockMvc.perform(put("/api/usuarios/" + id)
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensagem")
                        .value("O sistema precisa manter pelo menos um ADMIN ativo."));
    }

    @Test
    void deveRetornar400QuandoPerfilForHospede() throws Exception {
        when(usuarioService.cadastrarUsuario(any()))
                .thenThrow(new PerfilUsuarioInvalidoException(
                        "Contas de hóspede são gerenciadas pelo acesso BTHS no detalhe do hóspede."));

        String json = """
        {
            "nome": "Hóspede",
            "email": "hospede@beattrips.com",
            "senha": "senha123",
            "perfil": "HOSPEDE"
        }
        """;

        mockMvc.perform(post("/api/usuarios")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isBadRequest());
    }
}