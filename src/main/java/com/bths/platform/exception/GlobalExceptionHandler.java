package com.bths.platform.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
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
        );
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(erro);
    }

    @ExceptionHandler(HospedeJaCadastradoException.class)
    public ResponseEntity<Map<String, Object>> tratarHospedeJaCadastrado(
            HospedeJaCadastradoException exception
    ) {
        Map<String, Object> erro = new HashMap<>();


        erro.put("erro", "Conflict");
        erro.put("mensagem", exception.getMessage());
        erro.put("status", HttpStatus.CONFLICT.value());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(erro);
    }

    @ExceptionHandler(HospedeNaoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> tratarHospedeNaoEncontrado(
            HospedeNaoEncontradoException exception
    ) {

        Map<String, Object> erro = new HashMap<>();

        erro.put("erro", "Not Found");
        erro.put("mensagem", exception.getMessage());
        erro.put("status", HttpStatus.NOT_FOUND.value());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(erro);
    }

    @ExceptionHandler(QuartoNaoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> tratarQuartoNaoEncontrado(
            QuartoNaoEncontradoException exception
    ) {
        Map<String, Object> erro = new HashMap<>();

        erro.put("erro", "Not Found");
        erro.put("mensagem", exception.getMessage());
        erro.put("status", HttpStatus.NOT_FOUND.value());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(erro);
    }

    @ExceptionHandler(AlocacaoNaoEncontradaException.class)
    public ResponseEntity<Map<String, Object>> tratarAlocacaoNaoEncontrada(
            AlocacaoNaoEncontradaException exception
    ) {

        Map<String, Object> erro = new HashMap<>();

        erro.put("erro", "Not Found");
        erro.put("mensagem", exception.getMessage());
        erro.put("status", HttpStatus.NOT_FOUND.value());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(erro);
    }

    @ExceptionHandler(QuartoLotadoException.class)
    public ResponseEntity<Map<String, Object>> tratarQuartoLotado(
            QuartoLotadoException exception
    ) {
        Map<String, Object> erro = new HashMap<>();

        erro.put("erro", "Conflict");
        erro.put("mensagem", exception.getMessage());
        erro.put("status", HttpStatus.CONFLICT.value());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(erro);
    }

    @ExceptionHandler(HospedeJaAlocadoException.class)
    public ResponseEntity<Map<String, Object>> tratarHospedeJaAlocado(
            HospedeJaAlocadoException exception
    ) {
        Map<String, Object> erro = new HashMap<>();

        erro.put("erro", "Conflict");
        erro.put("mensagem", exception.getMessage());
        erro.put("status", HttpStatus.CONFLICT.value());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(erro);
    }

    @ExceptionHandler(ViagemIncompativelException.class)
    public ResponseEntity<Map<String, Object>> tratarViagemNaoEncontrada (
            ViagemIncompativelException exception
    ) {
        Map<String, Object> erro = new HashMap<>();

        erro.put("erro", "Conflict");
        erro.put("mensagem", exception.getMessage());
        erro.put("status", HttpStatus.CONFLICT.value());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(erro);
    }

    @ExceptionHandler(QuartoIndisponivelException.class)
public ResponseEntity<Map<String, Object>> tratarQuartoIndisponivel(
        QuartoIndisponivelException exception
    ) {

        Map<String, Object> erro = new HashMap<>();

        erro.put("erro", "Conflict");
        erro.put("mensagem", exception.getMessage());
        erro.put("status", HttpStatus.CONFLICT.value());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(erro);
    }

    @ExceptionHandler(HospedeSemAlocacaoException.class)
    public ResponseEntity<Map<String, Object>> tratarHospedeSemAlocacao(
            HospedeSemAlocacaoException exception
    ) {

        Map<String, Object> erro = new HashMap<>();

        erro.put("erro", "Conflict");
        erro.put("mensagem", exception.getMessage());
        erro.put("status", HttpStatus.CONFLICT.value());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(erro);
    }

    @ExceptionHandler(CheckInJaRealizadoException.class)
    public ResponseEntity<Map<String, Object>> tratarCheckInJaRealizado(
            CheckInJaRealizadoException exception
    ) {

        Map<String, Object> erro = new HashMap<>();

        erro.put("erro", "Conflict");
        erro.put("mensagem", exception.getMessage());
        erro.put("status", HttpStatus.CONFLICT.value());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(erro);
    }

}
