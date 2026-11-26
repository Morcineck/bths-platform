package com.bths.platform.security;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;

import java.time.Duration;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LimiteLoginPropertiesTest {

    private LimiteLoginProperties vincular(Map<String, String> propriedades) {
        Binder binder = new Binder(new MapConfigurationPropertySource(propriedades));
        return binder.bindOrCreate("bths.auth.limite-login", LimiteLoginProperties.class);
    }

    @Test
    void deveUsarValoresPadraoQuandoNadaForConfigurado() {
        LimiteLoginProperties propriedades = vincular(Map.of());

        assertThat(propriedades.maximoFalhas()).isEqualTo(5);
        assertThat(propriedades.janela()).isEqualTo(Duration.ofMinutes(15));
        assertThat(propriedades.bloqueioInicial()).isEqualTo(Duration.ofMinutes(5));
        assertThat(propriedades.bloqueioMaximo()).isEqualTo(Duration.ofHours(1));
    }

    @Test
    void deveAceitarConfiguracaoExplicita() {
        LimiteLoginProperties propriedades = vincular(Map.of(
                "bths.auth.limite-login.maximo-falhas", "3",
                "bths.auth.limite-login.janela", "10m",
                "bths.auth.limite-login.bloqueio-inicial", "2m",
                "bths.auth.limite-login.bloqueio-maximo", "30m"
        ));

        assertThat(propriedades.maximoFalhas()).isEqualTo(3);
        assertThat(propriedades.janela()).isEqualTo(Duration.ofMinutes(10));
        assertThat(propriedades.bloqueioInicial()).isEqualTo(Duration.ofMinutes(2));
        assertThat(propriedades.bloqueioMaximo()).isEqualTo(Duration.ofMinutes(30));
    }

    @Test
    void deveRejeitarMaximoDeFalhasMenorQueUm() {
        assertThatThrownBy(() -> new LimiteLoginProperties(
                0, Duration.ofMinutes(15), Duration.ofMinutes(5), Duration.ofHours(1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("maximo-falhas");
    }

    @Test
    void deveRejeitarJanelaZeroOuNegativa() {
        assertThatThrownBy(() -> new LimiteLoginProperties(
                5, Duration.ZERO, Duration.ofMinutes(5), Duration.ofHours(1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("janela");

        assertThatThrownBy(() -> new LimiteLoginProperties(
                5, Duration.ofMinutes(-1), Duration.ofMinutes(5), Duration.ofHours(1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("janela");
    }

    @Test
    void deveRejeitarBloqueioInicialZeroOuNegativo() {
        assertThatThrownBy(() -> new LimiteLoginProperties(
                5, Duration.ofMinutes(15), Duration.ZERO, Duration.ofHours(1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("bloqueio-inicial");
    }

    @Test
    void deveRejeitarBloqueioMaximoMenorQueOInicial() {
        assertThatThrownBy(() -> new LimiteLoginProperties(
                5, Duration.ofMinutes(15), Duration.ofMinutes(10), Duration.ofMinutes(5)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("bloqueio-maximo");
    }
}