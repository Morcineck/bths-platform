package com.bths.platform.quarto;

import com.bths.platform.quarto.dto.QuartoOcupacaoResponse;
import com.bths.platform.quarto.dto.QuartoRequest;
import com.bths.platform.quarto.dto.QuartoResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quartos")
public class QuartoController {

    private final QuartoService quartoService;


    public QuartoController(QuartoService quartoService) {
        this.quartoService = quartoService;
    }

    @PostMapping
    public ResponseEntity<QuartoResponse> cadastrarQuarto(
            @Valid @RequestBody QuartoRequest request
    ) {

        QuartoResponse response = quartoService.cadastrarQuarto(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuartoResponse> buscarQuartoPorId(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                quartoService.buscarQuartoPorId(id)
        );
    }

    @GetMapping
    public ResponseEntity<List<QuartoResponse>> listarQuartos() {

        return ResponseEntity.ok(
                quartoService.listarQuartos());
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuartoResponse> atualizarQuarto(
            @PathVariable Long id,
            @Valid @RequestBody QuartoRequest request) {

        return ResponseEntity.ok(
                quartoService.atualizarQuarto(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarQuarto(
            @PathVariable Long id
    ) {

        quartoService.deletarQuarto(id);

        return ResponseEntity.noContent().build();

    }

    @GetMapping("/viagem/{viagemId}")
    public ResponseEntity<List<QuartoResponse>> listarQuartosPorViagem(
            @PathVariable Long viagemId
    ) {

        return ResponseEntity.ok(
                quartoService.listarQuartosPorViagem(viagemId)
        );
    }

    @GetMapping("/viagem/{viagemId}/ocupacao")
    public ResponseEntity<List<QuartoOcupacaoResponse>> listarOcupacaoQuartoPorViagem(
            @PathVariable Long viagemId
    ) {

        return ResponseEntity.ok(
                quartoService
                        .listarOcupacaoQuartosPorViagem(
                                viagemId
                        )
        );
    }

}
