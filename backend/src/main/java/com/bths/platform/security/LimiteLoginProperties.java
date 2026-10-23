package com.bths.platform.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.Duration;

@ConfigurationProperties(prefix = "bths.auth.limite-login")
public record LimiteLoginProperties(
        @DefaultValue("5") int maximoFalhas,
        @DefaultValue("15m") Duration janela,
        @DefaultValue("5m") Duration bloqueioInicial,
        @DefaultValue("1h") Duration bloqueioMaximo
) {
    public LimiteLoginProperties {
        if (maximoFalhas < 1) {
            throw new IllegalArgumentException(
                    "bths.auth.limite-login.maximo-falhas deve ser maior ou igual a 1."
            );
        }
        if (janela == null || janela.isZero() || janela.isNegative()) {
            throw new IllegalArgumentException(
                    "bths.auth.limite-login.janela deve ser maior que zero."
            );
        }
        if (bloqueioInicial == null || bloqueioInicial.isZero() || bloqueioInicial.isNegative()) {
            throw new IllegalArgumentException(
                    "bths.auth.limite-login.bloqueio-inicial deve ser maior que zero."
            );
        }
        if (bloqueioMaximo == null || bloqueioMaximo.compareTo(bloqueioInicial) < 0) {
            throw new IllegalArgumentException(
                    "bths.auth.limite-login.bloqueio-maximo deve ser maior ou igual ao bloqueio inicial."
            );
        }
    }
}
