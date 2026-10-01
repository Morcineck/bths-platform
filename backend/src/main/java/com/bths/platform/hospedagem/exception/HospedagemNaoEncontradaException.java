package com.bths.platform.hospedagem.exception;

public class HospedagemNaoEncontradaException extends RuntimeException {

    public HospedagemNaoEncontradaException(
            String mensagem
    ) {
        super(mensagem);
    }
}
