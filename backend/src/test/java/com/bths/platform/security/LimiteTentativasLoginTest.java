package com.bths.platform.security;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class LimiteTentativasLoginTest {

    private static final String EMAIL = "hospede@exemplo.com";

    private final RelogioMutavel relogio = new RelogioMutavel();

    private LimiteTentativasLogin criar() {
        return criar(new LimiteLoginProperties(
                5, Duration.ofMinutes(15), Duration.ofMinutes(5), Duration.ofHours(1)));
    }

    private LimiteTentativasLogin criar(LimiteLoginProperties propriedades) {
        return new LimiteTentativasLogin(propriedades, relogio);
    }

    private Optional<Duration> falhar(LimiteTentativasLogin limite, String chave, int vezes) {
        Optional<Duration> ultimo = Optional.empty();
        for (int i = 0; i < vezes; i++) {
            ultimo = limite.registrarFalha(chave);
        }
        return ultimo;
    }

    @Test
    void naoDeveBloquearAntesDoLimiteDeFalhas() {
        LimiteTentativasLogin limite = criar();

        Optional<Duration> bloqueio = falhar(limite, EMAIL, 4);

        assertThat(bloqueio).isEmpty();
        assertThat(limite.tempoRestanteDeBloqueio(EMAIL)).isEmpty();
    }

    @Test
    void deveBloquearNaQuintaFalha() {
        LimiteTentativasLogin limite = criar();

        Optional<Duration> bloqueio = falhar(limite, EMAIL, 5);

        assertThat(bloqueio).contains(Duration.ofMinutes(5));
        assertThat(limite.tempoRestanteDeBloqueio(EMAIL)).contains(Duration.ofMinutes(5));
    }

    @Test
    void deveLiberarAposOFimDoBloqueio() {
        LimiteTentativasLogin limite = criar();
        falhar(limite, EMAIL, 5);

        relogio.avancar(Duration.ofMinutes(5));

        assertThat(limite.tempoRestanteDeBloqueio(EMAIL)).isEmpty();
    }

    @Test
    void deveDobrarOBloqueioNaReincidencia() {
        LimiteTentativasLogin limite = criar();
        falhar(limite, EMAIL, 5);
        relogio.avancar(Duration.ofMinutes(5));

        Optional<Duration> segundoBloqueio = falhar(limite, EMAIL, 5);

        assertThat(segundoBloqueio).contains(Duration.ofMinutes(10));
    }

    @Test
    void deveLimitarOBloqueioAoMaximo() {
        LimiteTentativasLogin limite = criar(new LimiteLoginProperties(
                2, Duration.ofMinutes(15), Duration.ofMinutes(5), Duration.ofMinutes(8)));
        falhar(limite, EMAIL, 2);
        relogio.avancar(Duration.ofMinutes(5));

        Optional<Duration> segundoBloqueio = falhar(limite, EMAIL, 2);

        assertThat(segundoBloqueio).contains(Duration.ofMinutes(8));
    }

    @Test
    void naoDeveSomarFalhasForaDaJanela() {
        LimiteTentativasLogin limite = criar();
        falhar(limite, EMAIL, 4);

        relogio.avancar(Duration.ofMinutes(16));
        Optional<Duration> bloqueio = falhar(limite, EMAIL, 4);

        assertThat(bloqueio).isEmpty();
        assertThat(limite.tempoRestanteDeBloqueio(EMAIL)).isEmpty();
    }

    @Test
    void deveZerarAContagemQuandoOLoginForBemSucedido() {
        LimiteTentativasLogin limite = criar();
        falhar(limite, EMAIL, 4);

        limite.registrarSucesso(EMAIL);
        Optional<Duration> bloqueio = falhar(limite, EMAIL, 4);

        assertThat(bloqueio).isEmpty();
    }

    @Test
    void deveManterContagemIndependentePorChave() {
        LimiteTentativasLogin limite = criar();

        falhar(limite, EMAIL, 5);

        assertThat(limite.tempoRestanteDeBloqueio(EMAIL)).isPresent();
        assertThat(limite.tempoRestanteDeBloqueio("outro@exemplo.com")).isEmpty();
    }

    @Test
    void naoDeveEstenderOBloqueioQuandoHaFalhaDuranteOBloqueio() {
        LimiteTentativasLogin limite = criar();
        falhar(limite, EMAIL, 5);
        relogio.avancar(Duration.ofMinutes(2));

        Optional<Duration> novoBloqueio = limite.registrarFalha(EMAIL);

        assertThat(novoBloqueio).isEmpty();
        assertThat(limite.tempoRestanteDeBloqueio(EMAIL)).contains(Duration.ofMinutes(3));
    }

    @Test
    void deveEsquecerAReincidenciaDepoisDoTempoMaximo() {
        LimiteTentativasLogin limite = criar();
        falhar(limite, EMAIL, 5);
        relogio.avancar(Duration.ofMinutes(5).plus(Duration.ofHours(1)));

        Optional<Duration> bloqueio = falhar(limite, EMAIL, 5);

        assertThat(bloqueio).contains(Duration.ofMinutes(5));
    }

    @Test
    void deveNormalizarAChaveDoEmail() {
        assertThat(LimiteTentativasLogin.normalizarChave("  Hospede@Exemplo.COM "))
                .isEqualTo("hospede@exemplo.com");
        assertThat(LimiteTentativasLogin.normalizarChave(null)).isEmpty();
    }

    private static class RelogioMutavel extends Clock {

        private Instant instante = Instant.parse("2026-10-22T12:00:00Z");

        void avancar(Duration duracao) {
            instante = instante.plus(duracao);
        }

        @Override
        public ZoneId getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return instante;
        }
    }

    @Test
    void deveLimitarOTamanhoDaChave() {
        String enorme = "a".repeat(5_000) + "@exemplo.com";

        assertThat(LimiteTentativasLogin.normalizarChave(enorme)).hasSize(254);
    }
}