package com.bths.platform.aviso;

import com.bths.platform.aviso.enums.TipoAviso;
import com.bths.platform.viagem.Viagem;
import com.bths.platform.viagem.ViagemRepository;
import com.bths.platform.viagem.enums.StatusViagem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class AvisoRepositoryTest {

    @Autowired
    private AvisoRepository avisoRepository;

    @Autowired
    private ViagemRepository viagemRepository;

    private Viagem viagem;

    @BeforeEach
    void setUp() {

        viagem =
                new Viagem();

        viagem.setNome(
                "Tomorrowland Brasil 2027"
        );

        viagem.setEvento(
                "Tomorrowland Brasil"
        );

        viagem.setDataInicio(
                java.time.LocalDate.of(
                        2027,
                        4,
                        29
                )
        );

        viagem.setDataFim(
                java.time.LocalDate.of(
                        2027,
                        5,
                        4
                )
        );

        viagem.setCidade(
                "Alumínio"
        );

        viagem.setEstado(
                "SP"
        );

        viagem.setStatus(
                StatusViagem.PLANEJADA
        );

        viagem =
                viagemRepository.save(
                        viagem
                );
    }

    @Test
    void deveListarTodosOsAvisosDaViagemOrdenadosPorDataDesc() {

        Aviso antigo =
                criarAviso(
                        "Aviso antigo",
                        true,
                        LocalDateTime.of(
                                2027,
                                4,
                                28,
                                10,
                                0
                        )
                );

        Aviso recente =
                criarAviso(
                        "Aviso recente",
                        false,
                        LocalDateTime.of(
                                2027,
                                4,
                                29,
                                15,
                                0
                        )
                );

        avisoRepository.saveAll(
                List.of(
                        antigo,
                        recente
                )
        );

        List<Aviso> avisos =
                avisoRepository
                        .findByViagemIdOrderByDataPublicacaoDesc(
                                viagem.getId()
                        );

        assertEquals(
                2,
                avisos.size()
        );

        assertEquals(
                "Aviso recente",
                avisos.get(0).getTitulo()
        );

        assertEquals(
                "Aviso antigo",
                avisos.get(1).getTitulo()
        );
    }

    @Test
    void deveRetornarSomenteAvisosAtivosParaHospede() {

        Aviso ativo =
                criarAviso(
                        "Aviso ativo",
                        true,
                        LocalDateTime.of(
                                2027,
                                4,
                                29,
                                12,
                                0
                        )
                );

        Aviso inativo =
                criarAviso(
                        "Aviso inativo",
                        false,
                        LocalDateTime.of(
                                2027,
                                4,
                                29,
                                13,
                                0
                        )
                );

        avisoRepository.saveAll(
                List.of(
                        ativo,
                        inativo
                )
        );

        List<Aviso> avisos =
                avisoRepository
                        .findByViagemIdAndAtivoTrueOrderByDataPublicacaoDesc(
                                viagem.getId()
                        );

        assertEquals(
                1,
                avisos.size()
        );

        assertEquals(
                "Aviso ativo",
                avisos.get(0).getTitulo()
        );
    }

    @Test
    void deveOrdenarAvisosAtivosDoMaisRecenteParaOMaisAntigo() {

        Aviso antigo =
                criarAviso(
                        "Primeiro aviso",
                        true,
                        LocalDateTime.of(
                                2027,
                                4,
                                28,
                                8,
                                0
                        )
                );

        Aviso recente =
                criarAviso(
                        "Segundo aviso",
                        true,
                        LocalDateTime.of(
                                2027,
                                4,
                                29,
                                18,
                                0
                        )
                );

        avisoRepository.saveAll(
                List.of(
                        antigo,
                        recente
                )
        );

        List<Aviso> avisos =
                avisoRepository
                        .findByViagemIdAndAtivoTrueOrderByDataPublicacaoDesc(
                                viagem.getId()
                        );

        assertEquals(
                "Segundo aviso",
                avisos.get(0).getTitulo()
        );

        assertEquals(
                "Primeiro aviso",
                avisos.get(1).getTitulo()
        );
    }

    private Aviso criarAviso(
            String titulo,
            boolean ativo,
            LocalDateTime dataPublicacao
    ) {

        Aviso aviso =
                new Aviso();

        aviso.setTitulo(
                titulo
        );

        aviso.setMensagem(
                "Mensagem de teste"
        );

        aviso.setTipo(
                TipoAviso.INFORMATIVO
        );

        aviso.setDataPublicacao(
                dataPublicacao
        );

        aviso.setAtivo(
                ativo
        );

        aviso.setViagem(
                viagem
        );

        return aviso;
    }
}