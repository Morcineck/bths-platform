package com.bths.platform.security;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AutenticacaoPropertiesTest {

    private AutenticacaoProperties vincular(Map<String, String> propriedades) {
        Binder binder = new Binder(new MapConfigurationPropertySource(propriedades));
        return binder.bindOrCreate("bths.auth", AutenticacaoProperties.class);
    }

    @Test
    void deveUsarValoresSegurosQuandoNadaForConfigurado() {
        AutenticacaoProperties propriedades = vincular(Map.of());

        assertThat(propriedades.cookieSecure()).isTrue();
        assertThat(propriedades.cookieSameSite()).isEqualTo("Lax");
    }

    @Test
    void deveAceitarConfiguracaoExplicita() {
        AutenticacaoProperties propriedades = vincular(Map.of(
                "bths.auth.cookie-secure", "false",
                "bths.auth.cookie-same-site", "Strict"
        ));

        assertThat(propriedades.cookieSecure()).isFalse();
        assertThat(propriedades.cookieSameSite()).isEqualTo("Strict");
    }

    @Test
    void deveRejeitarSameSiteInvalido() {
        assertThatThrownBy(() -> new AutenticacaoProperties(true, "Qualquer"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cookie-same-site");
    }

    @Test
    void deveRejeitarSameSiteNoneSemSecure() {
        assertThatThrownBy(() -> new AutenticacaoProperties(false, "None"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cookie-secure");
    }

    @Test
    void deveAceitarSameSiteNoneComSecure() {
        AutenticacaoProperties propriedades = new AutenticacaoProperties(true, "None");

        assertThat(propriedades.cookieSameSite()).isEqualTo("None");
    }
}