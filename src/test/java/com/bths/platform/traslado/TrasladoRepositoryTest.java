package com.bths.platform.traslado;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import com.bths.platform.hospede.Hospede;
import com.bths.platform.hospede.HospedeRepository;
import com.bths.platform.hospede.enums.StatusCheckIn;
import com.bths.platform.traslado.enums.StatusTraslado;
import com.bths.platform.traslado.enums.TipoTraslado;
import com.bths.platform.viagem.Viagem;
import com.bths.platform.viagem.ViagemRepository;
import com.bths.platform.viagem.enums.StatusViagem;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
class TrasladoRepositoryTest {

    @Autowired
    private TrasladoRepository trasladoRepository;

    @Autowired
    private ViagemRepository viagemRepository;

    @Autowired
    private HospedeRepository hospedeRepository;

    @Test
    void deveCarregarTrasladoRepository() {

        assertThat(trasladoRepository)
                .isNotNull();
    }

    @Test
    void deveBuscarSomenteProximosTrasladosAguardando() {

        LocalDateTime agora = LocalDateTime.of(
                2026, 9, 18, 12, 0
        );

        Viagem viagem =
                criarViagem("Tomorrowland Brasil 2027");

        Hospede hospede =
                criarHospede(
                        viagem,
                        "Hospede Teste",
                        "12345678901"
                );

        criarTraslado(
                viagem,
                hospede,
                StatusTraslado.AGUARDANDO,
                agora.minusHours(2)
        );

        Traslado futuro =
                criarTraslado(
                        viagem,
                        hospede,
                        StatusTraslado.AGUARDANDO,
                        agora.plusHours(2)
                );

        List<Traslado> resultado =
                trasladoRepository.buscarProximosTraslados(
                        viagem.getId(),
                        StatusTraslado.AGUARDANDO,
                        agora,
                        PageRequest.of(0, 5)
                );

        assertThat(resultado)
                .hasSize(1);

        assertThat(resultado.get(0).getId())
                .isEqualTo(futuro.getId());
    }

    @Test
    void deveBuscarSomenteTrasladosComStatusAguardando() {

        LocalDateTime agora = LocalDateTime.of(
                2026, 9, 18, 12, 0
        );

        Viagem viagem =
                criarViagem("Tomorrowland Brasil 2027");

        Hospede hospede =
                criarHospede(
                        viagem,
                        "Hospede Teste",
                        "12345678901"
                );

        Traslado aguardando =
                criarTraslado(
                        viagem,
                        hospede,
                        StatusTraslado.AGUARDANDO,
                        agora.plusHours(1)
                );

        criarTraslado(
                viagem,
                hospede,
                StatusTraslado.CONCLUIDO,
                agora.plusHours(2)
        );

        List<Traslado> resultado =
                trasladoRepository.buscarProximosTraslados(
                        viagem.getId(),
                        StatusTraslado.AGUARDANDO,
                        agora,
                        PageRequest.of(0, 5)
                );

        assertThat(resultado)
                .hasSize(1);

        assertThat(resultado.get(0).getId())
                .isEqualTo(aguardando.getId());

        assertThat(resultado.get(0).getStatus())
                .isEqualTo(StatusTraslado.AGUARDANDO);
    }

    private Viagem criarViagem(String nome) {

        Viagem viagem = new Viagem();
        viagem.setNome(nome);
        viagem.setEvento("Tomorrowland Brasil");
        viagem.setDataInicio(LocalDate.of(2027, 4, 30));
        viagem.setDataFim(LocalDate.of(2027, 5, 2));
        viagem.setStatus(StatusViagem.PLANEJADA);

        return viagemRepository.save(viagem);
    }

    private Hospede criarHospede(
            Viagem viagem,
            String nome,
            String cpf
    ) {

        Hospede hospede = new Hospede();
        hospede.setNomeCompleto(nome);
        hospede.setCpf(cpf);
        hospede.setStatusCheckIn(StatusCheckIn.PENDENTE);
        hospede.setViagem(viagem);

        return hospedeRepository.save(hospede);
    }

    private Traslado criarTraslado(
            Viagem viagem,
            Hospede hospede,
            StatusTraslado status,
            LocalDateTime dataHoraPrevista
    ) {

        Traslado traslado = new Traslado();
        traslado.setHospede(hospede);
        traslado.setViagem(viagem);
        traslado.setTipo(TipoTraslado.AEROPORTO_PARA_HOSPEDAGEM);
        traslado.setStatus(status);
        traslado.setDataHoraPrevista(dataHoraPrevista);
        traslado.setLocalOrigem("Aeroporto");
        traslado.setLocalDestino("Hospedagem");

        return trasladoRepository.save(traslado);
    }

    @Test
    void deveBuscarSomenteTrasladosDaViagemInformada() {

        LocalDateTime agora = LocalDateTime.of(
                2026, 9, 18, 12, 0
        );

        Viagem viagemPrincipal =
                criarViagem("Tomorrowland Brasil 2027");

        Viagem outraViagem =
                criarViagem("Outra Viagem");

        Hospede hospedePrincipal =
                criarHospede(
                        viagemPrincipal,
                        "Hospede Principal",
                        "12345678901"
                );

        Hospede outroHospede =
                criarHospede(
                        outraViagem,
                        "Outro Hospede",
                        "10987654321"
                );

        Traslado trasladoPrincipal =
                criarTraslado(
                        viagemPrincipal,
                        hospedePrincipal,
                        StatusTraslado.AGUARDANDO,
                        agora.plusHours(1)
                );

        criarTraslado(
                outraViagem,
                outroHospede,
                StatusTraslado.AGUARDANDO,
                agora.plusHours(2)
        );

        List<Traslado> resultado =
                trasladoRepository.buscarProximosTraslados(
                        viagemPrincipal.getId(),
                        StatusTraslado.AGUARDANDO,
                        agora,
                        PageRequest.of(0, 5)
                );

        assertThat(resultado)
                .hasSize(1);

        assertThat(resultado.get(0).getId())
                .isEqualTo(trasladoPrincipal.getId());

        assertThat(resultado.get(0).getViagem().getId())
                .isEqualTo(viagemPrincipal.getId());
    }

    @Test
    void deveOrdenarProximosTrasladosPorDataHoraPrevista() {

        LocalDateTime agora = LocalDateTime.of(
                2026, 9, 18, 12, 0
        );

        Viagem viagem =
                criarViagem("Tomorrowland Brasil 2027");

        Hospede hospede =
                criarHospede(
                        viagem,
                        "Hospede Teste",
                        "12345678901"
                );

        Traslado terceiro =
                criarTraslado(
                        viagem,
                        hospede,
                        StatusTraslado.AGUARDANDO,
                        agora.plusHours(3)
                );

        Traslado primeiro =
                criarTraslado(
                        viagem,
                        hospede,
                        StatusTraslado.AGUARDANDO,
                        agora.plusHours(1)
                );

        Traslado segundo =
                criarTraslado(
                        viagem,
                        hospede,
                        StatusTraslado.AGUARDANDO,
                        agora.plusHours(2)
                );

        List<Traslado> resultado =
                trasladoRepository.buscarProximosTraslados(
                        viagem.getId(),
                        StatusTraslado.AGUARDANDO,
                        agora,
                        PageRequest.of(0, 5)
                );

        assertThat(resultado)
                .hasSize(3);

        assertThat(resultado)
                .extracting(Traslado::getId)
                .containsExactly(
                        primeiro.getId(),
                        segundo.getId(),
                        terceiro.getId()
                );
    }

    @Test
    void deveLimitarResultadoAosCincoProximosTraslados() {

        LocalDateTime agora = LocalDateTime.of(
                2026, 9, 18, 12, 0
        );

        Viagem viagem =
                criarViagem("Tomorrowland Brasil 2027");

        Hospede hospede =
                criarHospede(
                        viagem,
                        "Hospede Teste",
                        "12345678901"
                );

        Traslado primeiro = criarTraslado(
                viagem,
                hospede,
                StatusTraslado.AGUARDANDO,
                agora.plusHours(1)
        );

        Traslado segundo = criarTraslado(
                viagem,
                hospede,
                StatusTraslado.AGUARDANDO,
                agora.plusHours(2)
        );

        Traslado terceiro = criarTraslado(
                viagem,
                hospede,
                StatusTraslado.AGUARDANDO,
                agora.plusHours(3)
        );

        Traslado quarto = criarTraslado(
                viagem,
                hospede,
                StatusTraslado.AGUARDANDO,
                agora.plusHours(4)
        );

        Traslado quinto = criarTraslado(
                viagem,
                hospede,
                StatusTraslado.AGUARDANDO,
                agora.plusHours(5)
        );

        criarTraslado(
                viagem,
                hospede,
                StatusTraslado.AGUARDANDO,
                agora.plusHours(6)
        );

        List<Traslado> resultado =
                trasladoRepository.buscarProximosTraslados(
                        viagem.getId(),
                        StatusTraslado.AGUARDANDO,
                        agora,
                        PageRequest.of(0, 5)
                );

        assertThat(resultado)
                .hasSize(5);

        assertThat(resultado)
                .extracting(Traslado::getId)
                .containsExactly(
                        primeiro.getId(),
                        segundo.getId(),
                        terceiro.getId(),
                        quarto.getId(),
                        quinto.getId()
                );
    }

}