package com.bths.platform.exception;

public class ViagemNaoEncontradaException extends RuntimeException {
    public ViagemNaoEncontradaException(String mensagem) {
        super(mensagem);
    }
}
