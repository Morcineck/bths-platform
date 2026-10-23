package com.bths.platform.security;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.security.SecureRandom;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ValidadorSegredoJwtTest {

    private static final String SEGREDO_FRACO =
            "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=";

    private final ApplicationContextRunner contexto = new ApplicationContextRunner()
            .withUserConfiguration(ValidadorSegredoJwt.class);

    private static String segredoAleatorio(int bytes) {
        byte[] valor = new byte[bytes];
        new SecureRandom().nextBytes(valor);
        return Base64.getEncoder().encodeToString(valor);
    }

    @Test
    void deveAceitarSegredoAleatorioDe256Bits() {
        assertThatCode(() -> ValidadorSegredoJwt.validar(segredoAleatorio(32)))
                .doesNotThrowAnyException();
    }

    @Test
    void deveRejeitarSegredoAusente() {
        assertThatThrownBy(() -> ValidadorSegredoJwt.validar(null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("JWT_SECRET");

        assertThatThrownBy(() -> ValidadorSegredoJwt.validar("  "))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("JWT_SECRET");
    }

    @Test
    void deveRejeitarSegredoQueNaoEBase64() {
        assertThatThrownBy(() -> ValidadorSegredoJwt.validar("SEU_VALOR_GERADO"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Base64");
    }

    @Test
    void deveRejeitarSegredoCurto() {
        assertThatThrownBy(() -> ValidadorSegredoJwt.validar(segredoAleatorio(16)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("256 bits");
    }

    @Test
    void deveRejeitarSegredoComBaixaEntropia() {
        assertThatThrownBy(() -> ValidadorSegredoJwt.validar(SEGREDO_FRACO))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("entropia");
    }

    @Test
    void naoDeveRevelarOSegredoNaMensagemDeErro() {
        assertThatThrownBy(() -> ValidadorSegredoJwt.validar(SEGREDO_FRACO))
                .hasMessageNotContaining(SEGREDO_FRACO);
    }

    @Test
    void naoDeveIniciarEmStagingComSegredoFraco() {
        contexto
                .withPropertyValues(
                        "spring.profiles.active=staging",
                        "JWT_SECRET=" + SEGREDO_FRACO
                )
                .run(aplicacao -> assertThat(aplicacao).hasFailed());
    }

    @Test
    void naoDeveIniciarEmProducaoSemSegredo() {
        contexto
                .withPropertyValues("spring.profiles.active=prod")
                .run(aplicacao -> assertThat(aplicacao).hasFailed());
    }

    @Test
    void deveIniciarEmStagingComSegredoForte() {
        contexto
                .withPropertyValues(
                        "spring.profiles.active=staging",
                        "JWT_SECRET=" + segredoAleatorio(32)
                )
                .run(aplicacao -> assertThat(aplicacao).hasNotFailed());
    }

    @Test
    void naoDeveExigirSegredoForteForaDeStagingEProducao() {
        contexto
                .withPropertyValues("JWT_SECRET=" + SEGREDO_FRACO)
                .run(aplicacao -> {
                    assertThat(aplicacao).hasNotFailed();
                    assertThat(aplicacao).doesNotHaveBean(ValidadorSegredoJwt.class);
                });
    }
}