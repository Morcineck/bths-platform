package com.bths.platform.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ViagemNaoEncontradaException.class)
    public ResponseEntity<Map<String, Object>> tratarViagemNaoEncontrada(
            ViagemNaoEncontradaException exception) {

        Map<String, Object> erro = Map.of(
                "status", HttpStatus.NOT_FOUND.value(),
                "erro", "Not Found",
                "mensagem", exception.getMessage()
        );;
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(erro);
    }

}
