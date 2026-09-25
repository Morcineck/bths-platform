package com.bths.platform.operacaoTraslado.execepion;

public class OperacaoTrasladoNaoEncontradaException
        extends RuntimeException {

    public OperacaoTrasladoNaoEncontradaException(
            String mensagem
    ) {
        super(mensagem);
    }
}
