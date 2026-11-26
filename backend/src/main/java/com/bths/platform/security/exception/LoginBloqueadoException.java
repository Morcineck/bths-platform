package com.bths.platform.security.exception;

import java.time.Duration;

public class LoginBloqueadoException extends RuntimeException {

    private final Duration tempoRestante;

    public LoginBloqueadoException(Duration tempoRestante) {
        super("Muitas tentativas de login. Tente novamente mais tarde.");
        this.tempoRestante = tempoRestante;
    }

    public Duration getTempoRestante() {
        return tempoRestante;
    }
}