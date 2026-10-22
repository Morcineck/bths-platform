package com.bths.platform.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.regex.Pattern;

@ConfigurationProperties(prefix = "bths.cors")
public record CorsProperties(List<String> allowedOrigins) {

    private static final Pattern ORIGEM_VALIDA = Pattern.compile("^https?://[^/*\\s,]+$");

    public CorsProperties {
        List<String> orignes = allowedOrigins == null
                ? List.of()
                : allowedOrigins.stream()
                .map(String::trim)
                .filter(origem -> !origem.isEmpty())
                .toList();

        for (String origem : orignes) {
            if (!ORIGEM_VALIDA.matcher(origem).matches()) {
                throw new IllegalArgumentException(
                        "bths.cors.allowed-origins contém uma origem inválida: '" + origem
                                + "'. Use esquema, host e porta opcional, sem barra final nem curinga."
                );
            }
        }

        allowedOrigins = orignes;
    }
}
