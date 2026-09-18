package com.bths.platform.exception;

public class EmailUsuarioJaCadastradoException extends RuntimeException {
    public EmailUsuarioJaCadastradoException(String mensagem) {
        super(mensagem);
    }
}
