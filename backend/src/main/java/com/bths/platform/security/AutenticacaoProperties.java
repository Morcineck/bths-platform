package com.bths.platform.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.Set;

@ConfigurationProperties(prefix = "bths.auth")
public record AutenticacaoProperties(
        @DefaultValue("true") boolean cookieSecure,
        @DefaultValue("Lax") String cookieSameSite
) {

    private static final Set<String> SAME_SITE_VALIDOS =Set.of("Lax", "Strict", "None");

    public AutenticacaoProperties {
        if (!SAME_SITE_VALIDOS.contains(cookieSameSite)) {
            throw new IllegalArgumentException(
                    "bths.auth.cookie-same-site deve ser Lax, Strict ou None."
            );
        }
        if ("None".equals(cookieSameSite) && !cookieSecure) {
            throw new IllegalArgumentException(
                    "SameSite=None exige bths.auth.cookie-secure=true."
            );
        }
    }
}