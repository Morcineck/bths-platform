package com.bths.platform.handler;

import com.bths.platform.alocacao.exception.AlocacaoNaoEncontradaException;
import com.bths.platform.alocacao.exception.HospedeJaAlocadoException;
import com.bths.platform.alocacao.exception.ViagemIncompativelException;
import com.bths.platform.checkin.exception.CheckInJaRealizadoException;
import com.bths.platform.checkin.exception.HospedeJaRealizouCheckInException;
import com.bths.platform.checkin.exception.HospedeSemAlocacaoException;
import com.bths.platform.checkin.exception.NaoComparecimentoJaRegistradoException;
import com.bths.platform.hospede.exception.HospedeJaCadastradoException;
import com.bths.platform.hospede.exception.HospedeNaoEncontradoException;
import com.bths.platform.motorista.exception.MotoristaNaoEncontradoException;
import com.bths.platform.operacaoTraslado.execepion.CapacidadeVeiculoExcedidaException;
import com.bths.platform.operacaoTraslado.execepion.OperacaoTrasladoComPassageirosException;
import com.bths.platform.operacaoTraslado.execepion.OperacaoTrasladoNaoEncontradaException;
import com.bths.platform.quarto.exception.QuartoIndisponivelException;
import com.bths.platform.quarto.exception.QuartoLotadoException;
import com.bths.platform.quarto.exception.QuartoNaoEncontradoException;
import com.bths.platform.security.exception.CredenciaisInvalidasException;
import com.bths.platform.traslado.exception.*;
import com.bths.platform.usuario.exception.EmailUsuarioJaCadastradoException;
import com.bths.platform.usuario.exception.UsuarioNaoEncontradoException;
import com.bths.platform.veiculo.exception.VeiculoJaCadastradoException;
import com.bths.platform.veiculo.exception.VeiculoNaoEncontradoException;
import com.bths.platform.viagem.exception.ViagemNaoEncontradaException;
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
            ViagemNaoEncontradaException exception
    ) {

        Map<String, Object> erro = new HashMap<>();

        erro.put("erro", "Not Found");
        erro.put("mensagem", exception.getMessage());
        erro.put("status", HttpStatus.NOT_FOUND.value());

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
    public ResponseEntity<Map<String, Object>> tratarViagemIncompativel(
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

    @ExceptionHandler(AeroportoObrigatorioException.class)
    public ResponseEntity<Map<String, Object>> tratarAeroportoObrigatorio(
            AeroportoObrigatorioException exception
    ) {

        Map<String, Object> erro = new HashMap<>();

        erro.put("erro", "Bad Request");
        erro.put("mensagem", exception.getMessage());
        erro.put("status", HttpStatus.BAD_REQUEST.value());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(erro);
    }

    @ExceptionHandler(TrasladoNaoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> tratarTrasladoNaoEncontrado(
            TrasladoNaoEncontradoException exception
    ) {

        Map<String, Object> erro = new HashMap<>();

        erro.put("erro", "Not Found");
        erro.put("mensagem", exception.getMessage());
        erro.put("status", HttpStatus.NOT_FOUND.value());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(erro);
    }

    @ExceptionHandler(TransicaoStatusTrasladoInvalidaException.class)
    public ResponseEntity<Map<String, Object>> tratarTransicaoStatusTrasladoInvalida(
            TransicaoStatusTrasladoInvalidaException exception
    ) {

        Map<String, Object> erro = new HashMap<>();

        erro.put("erro", "Conflict");
        erro.put("mensagem", exception.getMessage());
        erro.put("status", HttpStatus.CONFLICT.value());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(erro);
    }

    @ExceptionHandler(MotivoCorrecaoObrigatorioException.class)
    public ResponseEntity<Map<String, Object>> tratarMotivoCorrecaoObrigatorio(
            MotivoCorrecaoObrigatorioException exception
    ) {

        Map<String, Object> erro = new HashMap<>();

        erro.put("erro", "Bad Request");
        erro.put("mensagem", exception.getMessage());
        erro.put("status", HttpStatus.BAD_REQUEST.value());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(erro);
    }

    @ExceptionHandler(EmailUsuarioJaCadastradoException.class)
    public ResponseEntity<Map<String, Object>> tratarEmailUsuarioJaCadastrado(
            EmailUsuarioJaCadastradoException exception
    ) {

        Map<String, Object> erro = new HashMap<>();

        erro.put("erro", "Conflict");
        erro.put("mensagem", exception.getMessage());
        erro.put("status", HttpStatus.CONFLICT.value());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(erro);
    }

    @ExceptionHandler(CredenciaisInvalidasException.class)
    public ResponseEntity<Map<String, Object>> tratarCredenciaisInvalidas(
            CredenciaisInvalidasException exception
    ) {

        Map<String, Object> erro = new HashMap<>();

        erro.put("erro", "Unauthorized");
        erro.put("mensagem", exception.getMessage());
        erro.put("status", HttpStatus.UNAUTHORIZED.value());

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(erro);
    }

    @ExceptionHandler(UsuarioNaoEncontradoException.class)
    public  ResponseEntity<Map<String, Object>> tratarUsuarioNaoEncontrado(
            UsuarioNaoEncontradoException exception
    ) {

        Map<String, Object> erro = new HashMap<>();

        erro.put("erro", "Not Found");
        erro.put("mensagem", exception.getMessage());
        erro.put("status", HttpStatus.NOT_FOUND.value());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(erro);
    }

    @ExceptionHandler(HospedeJaRealizouCheckInException.class)
    public ResponseEntity<Map<String, Object>>
    tratarHospedeJaRealizouCheckIn(
            HospedeJaRealizouCheckInException exception
    )  {
        Map<String, Object> erro = new HashMap<>();

        erro.put("erro", "Conflict");
        erro.put("mensagem", exception.getMessage());
        erro.put("status", HttpStatus.CONFLICT.value());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(erro);
    }

    @ExceptionHandler(NaoComparecimentoJaRegistradoException.class)
    public ResponseEntity<Map<String, Object>> tratarNaoComparecimentoJaRegistrado(
            NaoComparecimentoJaRegistradoException exception
    ) {

        Map<String, Object> erro = new HashMap<>();

        erro.put("erro", "Conflict");
        erro.put("mensagem", exception.getMessage());
        erro.put("status", HttpStatus.CONFLICT.value());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(erro);
    }

    @ExceptionHandler(MotoristaNaoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> tratarMotoristaNaoEncontrato(
            MotoristaNaoEncontradoException exception
    ) {

        Map<String, Object> erro = new HashMap<>();
        erro.put("erro", "Not Found");
        erro.put("mensagem", exception.getMessage());
        erro.put("status", HttpStatus.NOT_FOUND.value());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(erro);
    }

    @ExceptionHandler(VeiculoJaCadastradoException.class)
    public ResponseEntity<Map<String, Object>> tratarVeiculoJaCadastrado(
            VeiculoJaCadastradoException exception
    ) {

        Map<String, Object> erro = new HashMap<>();

        erro.put("erro", "Conflict");
        erro.put("mensagem", exception.getMessage());
        erro.put("status", HttpStatus.CONFLICT.value());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(erro);
    }

    @ExceptionHandler(VeiculoNaoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> tratarVeiculoNaoEncontrado(
            VeiculoNaoEncontradoException exception
    ) {

        Map<String, Object> erro = new HashMap<>();

        erro.put("erro", "Not Found");
        erro.put("mensagem", exception.getMessage());
        erro.put("status", HttpStatus.NOT_FOUND.value());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(erro);
    }

    @ExceptionHandler(MotoristaInativoException.class)
    public ResponseEntity<Map<String, Object>> tratarMotoristaInativo(
            MotoristaInativoException exception
    ) {

        Map<String, Object> erro = new HashMap<>();

        erro.put("erro", "Conflict");
        erro.put("mensagem", exception.getMessage());
        erro.put("status", HttpStatus.CONFLICT.value());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(erro);
    }

    @ExceptionHandler(VeiculoInativoException.class)
    public ResponseEntity<Map<String, Object>> tratarVeiculoInativo(
            VeiculoInativoException exception
    ) {

        Map<String, Object> erro = new HashMap<>();

        erro.put("erro", "Conflict");
        erro.put("mensagem", exception.getMessage());
        erro.put("status", HttpStatus.CONFLICT.value());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(erro);
    }

    @ExceptionHandler(OperacaoTrasladoNaoEncontradaException.class)
    public ResponseEntity<Map<String, Object>> tratarOperacaoTrasladoNaoEncontrada(
            OperacaoTrasladoNaoEncontradaException exception
    ) {

        Map<String, Object> erro = new HashMap<>();

        erro.put("erro", "Not Found");
        erro.put("mensagem", exception.getMessage());
        erro.put("status", HttpStatus.NOT_FOUND.value());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(erro);
    }

    @ExceptionHandler(CapacidadeVeiculoExcedidaException.class)
    public ResponseEntity<Map<String, Object>> tratarCapacidadeVeiculoExcedida(
            CapacidadeVeiculoExcedidaException exception
    ) {

        Map<String, Object> erro = new HashMap<>();

        erro.put("erro", "Conflict");
        erro.put("mensagem", exception.getMessage());
        erro.put("status", HttpStatus.CONFLICT.value());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(erro);
    }

    @ExceptionHandler(
            OperacaoTrasladoComPassageirosException.class
    )

    public ResponseEntity<Map<String, Object>>
    tratarOperacaoTrasladoComPassageiros(
            OperacaoTrasladoComPassageirosException exception
    ) {

        Map<String, Object> erro =
                new HashMap<>();

        erro.put(
                "erro",
                "Conflict"
        );

        erro.put(
                "mensagem",
                exception.getMessage()
        );

        erro.put(
                "status",
                HttpStatus.CONFLICT.value()
        );

        return ResponseEntity
                .status(
                        HttpStatus.CONFLICT
                )
                .body(erro);
    }

}
