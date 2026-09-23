package com.bths.platform.motorista.exception;

public class MotoristaNaoEncontradoException extends RuntimeException {
    public MotoristaNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
