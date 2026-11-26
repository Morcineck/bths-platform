package com.bths.platform.security;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CorsPropertiesTest {

    private CorsProperties vincular(Map<String, String> propriedades) {
        Binder binder = new Binder(new MapConfigurationPropertySource(propriedades));
        return binder.bindOrCreate("bths.cors", CorsProperties.class);
    }

    @Test
    void deveIniciarSemOrigensQuandoNadaForConfigurado() {
        assertThat(vincular(Map.of()).allowedOrigins()).isEmpty();
    }

    @Test
    void deveLerListaSeparadaPorVirgulaIgnorandoEspacos() {
        CorsProperties propriedades = vincular(Map.of(
                "bths.cors.allowed-origins",
                "https://www.beattrips.com.br, http://127.0.0.1:3000"
        ));

        assertThat(propriedades.allowedOrigins()).containsExactly(
                "https://www.beattrips.com.br",
                "http://127.0.0.1:3000"
        );
    }

    @Test
    void deveRejeitarCuringa() {
        assertThatThrownBy(() -> new CorsProperties(List.of("*")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("allowed-origins");

        assertThatThrownBy(() -> new CorsProperties(List.of("https://*.beattrips.com.br")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deveRejeitarOrigemComCaminhoOuBarraFinal() {
        assertThatThrownBy(() -> new CorsProperties(List.of("https://www.beattrips.com.br/")))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> new CorsProperties(List.of("https://www.beattrips.com.br/app")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deveRejeitarOrigemSemEsquemaHttpOuHttps() {
        assertThatThrownBy(() -> new CorsProperties(List.of("www.beattrips.com.br")))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> new CorsProperties(List.of("ftp://www.beattrips.com.br")))
                .isInstanceOf(IllegalArgumentException.class);
    }
}