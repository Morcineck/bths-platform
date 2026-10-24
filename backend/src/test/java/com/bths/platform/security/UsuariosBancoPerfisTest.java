package com.bths.platform.security;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

class UsuariosBancoPerfisTest {

    private Properties carregar(String arquivo) throws IOException {
        return PropertiesLoaderUtils.loadProperties(new ClassPathResource(arquivo));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "application-staging.properties",
            "application-prod.properties"
    })
    void deveUsarUsuarioDeMigracaoSeparadoDoUsuarioDaAplicacao(String arquivo)
            throws IOException {

        Properties propriedades = carregar(arquivo);

        assertThat(propriedades.getProperty("spring.datasource.username"))
                .isEqualTo("${DB_USERNAME}");
        assertThat(propriedades.getProperty("spring.datasource.password"))
                .isEqualTo("${DB_PASSWORD}");

        assertThat(propriedades.getProperty("spring.flyway.url"))
                .isEqualTo("${BTHS_DB_URL}");
        assertThat(propriedades.getProperty("spring.flyway.user"))
                .isEqualTo("${BTHS_DB_MIGRACAO_USUARIO}");
        assertThat(propriedades.getProperty("spring.flyway.password"))
                .isEqualTo("${BTHS_DB_MIGRACAO_SENHA}");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "application-staging.properties",
            "application-prod.properties"
    })
    void naoDeveVersionarCredenciaisNosPerfis(String arquivo) throws IOException {

        Properties propriedades = carregar(arquivo);

        propriedades.forEach((chave, valor) -> {
            String nome = chave.toString().toLowerCase();
            if (nome.contains("password") || nome.contains("secret")) {
                assertThat(valor.toString())
                        .as("A propriedade %s deve vir de variável de ambiente", chave)
                        .startsWith("${");
            }
        });
    }
}