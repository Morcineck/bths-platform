package com.bths.platform.alocacao.exception;

public class HospedeJaAlocadoException extends RuntimeException {
    public HospedeJaAlocadoException(String mensagem) {
        super(mensagem);
    }
}
