package com.bths.platform.agenda;

import com.bths.platform.agenda.enums.TipoAgendaViagem;
import com.bths.platform.viagem.Viagem;
import com.bths.platform.viagem.ViagemRepository;
import com.bths.platform.viagem.enums.StatusViagem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class AgendaViagemRepositoryTest {

    @Autowired
    private AgendaViagemRepository agendaViagemRepository;

    @Autowired
    private ViagemRepository viagemRepository;

    @Test
    void deveListarSomenteItensAtivosEVisiveisDoHospedeEmOrdem() {

        Viagem viagem =
                criarViagem();

        AgendaViagem dia2 =
                criarItem(
                        viagem,
                        "Festival — Dia 2",
                        LocalDateTime.of(
                                2027,
                                5,
                                1,
                                13,
                                0
                        ),
                        2,
                        true,
                        true
                );

        AgendaViagem dia1 =
                criarItem(
                        viagem,
                        "Festival — Dia 1",
                        LocalDateTime.of(
                                2027,
                                4,
                                30,
                                13,
                                0
                        ),
                        1,
                        true,
                        true
                );

        AgendaViagem oculto =
                criarItem(
                        viagem,
                        "Evento interno",
                        LocalDateTime.of(
                                2027,
                                4,
                                29,
                                18,
                                0
                        ),
                        0,
                        true,
                        false
                );

        AgendaViagem inativo =
                criarItem(
                        viagem,
                        "Evento cancelado",
                        LocalDateTime.of(
                                2027,
                                4,
                                29,
                                19,
                                0
                        ),
                        0,
                        false,
                        true
                );

        agendaViagemRepository.saveAll(
                List.of(
                        dia2,
                        dia1,
                        oculto,
                        inativo
                )
        );

        List<AgendaViagem> resultado =
                agendaViagemRepository
                        .findByViagemIdAndAtivoTrueAndVisivelHospedeTrueOrderByOrdemAscDataHoraInicioAsc(
                                viagem.getId()
                        );

        assertEquals(
                2,
                resultado.size()
        );

        assertEquals(
                "Festival — Dia 1",
                resultado.get(0).getTitulo()
        );

        assertEquals(
                "Festival — Dia 2",
                resultado.get(1).getTitulo()
        );

        assertTrue(
                resultado
                        .stream()
                        .allMatch(
                                AgendaViagem::getAtivo
                        )
        );

        assertTrue(
                resultado
                        .stream()
                        .allMatch(
                                AgendaViagem::getVisivelHospede
                        )
        );
    }

    @Test
    void deveUsarDataHoraInicioComoDesempateQuandoOrdemForIgual() {

        Viagem viagem =
                criarViagem();

        AgendaViagem segundo =
                criarItem(
                        viagem,
                        "Segundo evento",
                        LocalDateTime.of(
                                2027,
                                4,
                                30,
                                18,
                                0
                        ),
                        1,
                        true,
                        true
                );

        AgendaViagem primeiro =
                criarItem(
                        viagem,
                        "Primeiro evento",
                        LocalDateTime.of(
                                2027,
                                4,
                                30,
                                12,
                                0
                        ),
                        1,
                        true,
                        true
                );

        agendaViagemRepository.saveAll(
                List.of(
                        segundo,
                        primeiro
                )
        );

        List<AgendaViagem> resultado =
                agendaViagemRepository
                        .findByViagemIdAndAtivoTrueAndVisivelHospedeTrueOrderByOrdemAscDataHoraInicioAsc(
                                viagem.getId()
                        );

        assertEquals(
                2,
                resultado.size()
        );

        assertEquals(
                "Primeiro evento",
                resultado.get(0).getTitulo()
        );

        assertEquals(
                "Segundo evento",
                resultado.get(1).getTitulo()
        );
    }

    @Test
    void deveListarAgendaCompletaDaViagemIncluindoItensOcultosEInativos() {

        Viagem viagem =
                criarViagem();

        AgendaViagem visivel =
                criarItem(
                        viagem,
                        "Festival",
                        LocalDateTime.of(
                                2027,
                                4,
                                30,
                                13,
                                0
                        ),
                        2,
                        true,
                        true
                );

        AgendaViagem oculto =
                criarItem(
                        viagem,
                        "Evento operacional",
                        LocalDateTime.of(
                                2027,
                                4,
                                29,
                                10,
                                0
                        ),
                        1,
                        true,
                        false
                );

        AgendaViagem inativo =
                criarItem(
                        viagem,
                        "Evento antigo",
                        LocalDateTime.of(
                                2027,
                                4,
                                28,
                                10,
                                0
                        ),
                        0,
                        false,
                        true
                );

        agendaViagemRepository.saveAll(
                List.of(
                        visivel,
                        oculto,
                        inativo
                )
        );

        List<AgendaViagem> resultado =
                agendaViagemRepository
                        .findByViagemIdOrderByOrdemAscDataHoraInicioAsc(
                                viagem.getId()
                        );

        assertEquals(
                3,
                resultado.size()
        );

        assertEquals(
                "Evento antigo",
                resultado.get(0).getTitulo()
        );

        assertEquals(
                "Evento operacional",
                resultado.get(1).getTitulo()
        );

        assertEquals(
                "Festival",
                resultado.get(2).getTitulo()
        );
    }

    private Viagem criarViagem() {

        Viagem viagem =
                new Viagem();

        viagem.setNome(
                "Tomorrowland Brasil 2027"
        );

        viagem.setEvento(
                "Tomorrowland Brasil"
        );

        viagem.setDataInicio(
                LocalDate.of(
                        2027,
                        4,
                        29
                )
        );

        viagem.setDataFim(
                LocalDate.of(
                        2027,
                        5,
                        4
                )
        );

        viagem.setStatus(
                StatusViagem.PLANEJADA
        );

        return viagemRepository.save(
                viagem
        );
    }

    private AgendaViagem criarItem(
            Viagem viagem,
            String titulo,
            LocalDateTime dataHoraInicio,
            Integer ordem,
            Boolean ativo,
            Boolean visivelHospede
    ) {

        AgendaViagem agenda =
                new AgendaViagem();

        agenda.setTitulo(
                titulo
        );

        agenda.setDataHoraInicio(
                dataHoraInicio
        );

        agenda.setTipo(
                TipoAgendaViagem.FESTIVAL
        );

        agenda.setOrdem(
                ordem
        );

        agenda.setAtivo(
                ativo
        );

        agenda.setVisivelHospede(
                visivelHospede
        );

        agenda.setViagem(
                viagem
        );

        return agenda;
    }
}