package com.bths.platform.exception;

public class MotivoCorrecaoObrigatorioException extends RuntimeException {
    public MotivoCorrecaoObrigatorioException(String mensagem) {
        super(mensagem);
    }
}
