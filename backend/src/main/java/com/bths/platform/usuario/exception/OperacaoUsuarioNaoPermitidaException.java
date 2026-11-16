package com.bths.platform.usuario.exception;

public class OperacaoUsuarioNaoPermitidaException extends RuntimeException {
    public OperacaoUsuarioNaoPermitidaException(String mensagem) {
        super(mensagem);
    }
}
