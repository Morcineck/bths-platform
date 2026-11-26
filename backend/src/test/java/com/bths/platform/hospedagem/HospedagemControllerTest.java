package com.bths.platform.hospedagem;

import com.bths.platform.hospedagem.dto.HospedagemResponse;
import com.bths.platform.security.JwtService;
import com.bths.platform.security.UsuarioDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HospedagemController.class)
@AutoConfigureMockMvc(addFilters = false)
class HospedagemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HospedagemService hospedagemService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UsuarioDetailsService usuarioDetailsService;

    @Test
    void deveCadastrarHospedagemComSucesso()
            throws Exception {

        HospedagemResponse response =
                new HospedagemResponse();

        response.setId(10L);
        response.setNome(
                "Chácara Beat Trips"
        );
        response.setCidade(
                "Alumínio"
        );
        response.setEstado(
                "SP"
        );
        response.setViagemId(
                1L
        );
        response.setViagemNome(
                "Tomorrowland Brasil 2027"
        );

        when(
                hospedagemService
                        .cadastrarHospedagem(
                                any()
                        )
        ).thenReturn(
                response
        );

        String requestJson = """
            {
                "nome": "Chácara Beat Trips",
                "cidade": "Alumínio",
                "estado": "SP",
                "viagemId": 1
            }
            """;

        mockMvc.perform(
                        post("/api/hospedagens")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        requestJson
                                )
                )
                .andExpect(
                        status().isCreated()
                )
                .andExpect(
                        jsonPath("$.id")
                                .value(10)
                )
                .andExpect(
                        jsonPath("$.nome")
                                .value(
                                        "Chácara Beat Trips"
                                )
                )
                .andExpect(
                        jsonPath("$.cidade")
                                .value(
                                        "Alumínio"
                                )
                )
                .andExpect(
                        jsonPath("$.estado")
                                .value(
                                        "SP"
                                )
                )
                .andExpect(
                        jsonPath("$.viagemId")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.viagemNome")
                                .value(
                                        "Tomorrowland Brasil 2027"
                                )
                );
    }

    @Test
    void deveBuscarHospedagemPorIdComSucesso()
            throws Exception {

        HospedagemResponse response =
                new HospedagemResponse();

        response.setId(10L);
        response.setNome(
                "Chácara Beat Trips"
        );
        response.setCidade(
                "Alumínio"
        );
        response.setEstado(
                "SP"
        );
        response.setViagemId(
                1L
        );
        response.setViagemNome(
                "Tomorrowland Brasil 2027"
        );

        when(
                hospedagemService
                        .buscarHospedagemPorId(
                                10L
                        )
        ).thenReturn(
                response
        );

        mockMvc.perform(
                        get(
                                "/api/hospedagens/10"
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.id")
                                .value(10)
                )
                .andExpect(
                        jsonPath("$.nome")
                                .value(
                                        "Chácara Beat Trips"
                                )
                )
                .andExpect(
                        jsonPath("$.cidade")
                                .value(
                                        "Alumínio"
                                )
                )
                .andExpect(
                        jsonPath("$.estado")
                                .value(
                                        "SP"
                                )
                )
                .andExpect(
                        jsonPath("$.viagemId")
                                .value(1)
                );
    }

    @Test
    void deveListarHospedagensComSucesso()
            throws Exception {

        HospedagemResponse response1 =
                new HospedagemResponse();

        response1.setId(1L);
        response1.setNome(
                "Chácara Beat Trips"
        );

        HospedagemResponse response2 =
                new HospedagemResponse();

        response2.setId(2L);
        response2.setNome(
                "Hotel Parceiro"
        );

        when(
                hospedagemService
                        .listarHospedagens()
        ).thenReturn(
                List.of(
                        response1,
                        response2
                )
        );

        mockMvc.perform(
                        get(
                                "/api/hospedagens"
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.length()")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$[0].id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$[0].nome")
                                .value(
                                        "Chácara Beat Trips"
                                )
                )
                .andExpect(
                        jsonPath("$[1].id")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$[1].nome")
                                .value(
                                        "Hotel Parceiro"
                                )
                );
    }

    @Test
    void deveListarHospedagensPorViagemComSucesso()
            throws Exception {

        Long viagemId = 1L;

        HospedagemResponse response1 =
                new HospedagemResponse();

        response1.setId(1L);
        response1.setNome(
                "Chácara Beat Trips"
        );
        response1.setViagemId(
                viagemId
        );

        HospedagemResponse response2 =
                new HospedagemResponse();

        response2.setId(2L);
        response2.setNome(
                "Hotel Parceiro"
        );
        response2.setViagemId(
                viagemId
        );

        when(
                hospedagemService
                        .listarHospedagensPorViagem(
                                viagemId
                        )
        ).thenReturn(
                List.of(
                        response1,
                        response2
                )
        );

        mockMvc.perform(
                        get(
                                "/api/hospedagens/viagem/{viagemId}",
                                viagemId
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.length()")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$[0].nome")
                                .value(
                                        "Chácara Beat Trips"
                                )
                )
                .andExpect(
                        jsonPath("$[1].nome")
                                .value(
                                        "Hotel Parceiro"
                                )
                );
    }

    @Test
    void deveAtualizarHospedagemComSucesso()
            throws Exception {

        HospedagemResponse response =
                new HospedagemResponse();

        response.setId(10L);
        response.setNome(
                "Chácara Beat Trips Atualizada"
        );
        response.setCidade(
                "Alumínio"
        );
        response.setEstado(
                "SP"
        );
        response.setViagemId(
                1L
        );

        when(
                hospedagemService
                        .atualizarHospedagem(
                                any(),
                                any()
                        )
        ).thenReturn(
                response
        );

        String requestJson = """
        {
            "nome": "Chácara Beat Trips Atualizada",
            "cidade": "Alumínio",
            "estado": "SP",
            "viagemId": 1
        }
        """;

        mockMvc.perform(
                        put(
                                "/api/hospedagens/10"
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        requestJson
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.id")
                                .value(10)
                )
                .andExpect(
                        jsonPath("$.nome")
                                .value(
                                        "Chácara Beat Trips Atualizada"
                                )
                );
    }

    @Test
    void deveDeletarHospedagemComSucesso()
            throws Exception {

        mockMvc.perform(
                        delete(
                                "/api/hospedagens/10"
                        )
                )
                .andExpect(
                        status().isNoContent()
                );

        verify(
                hospedagemService
        ).deletarHospedagem(
                10L
        );
    }

    @Test
    void deveRetornar400QuandoNomeNaoForInformado()
            throws Exception {

        String requestJson = """
        {
            "cidade": "Alumínio",
            "estado": "SP",
            "viagemId": 1
        }
        """;

        mockMvc.perform(
                        post(
                                "/api/hospedagens"
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        requestJson
                                )
                )
                .andExpect(
                        status().isBadRequest()
                );
    }

    @Test
    void deveRetornar400QuandoViagemIdNaoForInformado()
            throws Exception {

        String requestJson = """
        {
            "nome": "Chácara Beat Trips",
            "cidade": "Alumínio",
            "estado": "SP"
        }
        """;

        mockMvc.perform(
                        post(
                                "/api/hospedagens"
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        requestJson
                                )
                )
                .andExpect(
                        status().isBadRequest()
                );
    }
}