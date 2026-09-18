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
}