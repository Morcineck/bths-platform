package com.bths.platform.alocacao;

import com.bths.platform.alocacao.dto.AlocacaoQuartoRequest;
import com.bths.platform.alocacao.dto.AlocacaoQuartoResponse;
import com.bths.platform.alocacao.dto.OcupacaoQuartoResponse;
import com.bths.platform.alocacao.dto.TrocarQuartoRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alocacoes-quartos")
public class AlocacaoQuartoController {

    private final AlocacaoService alocacaoService;

    public AlocacaoQuartoController(AlocacaoService alocacaoService) {

        this.alocacaoService = alocacaoService;
    }

    @PostMapping
    public ResponseEntity<AlocacaoQuartoResponse> alocarHospede(
            @Valid @RequestBody AlocacaoQuartoRequest request) {

        AlocacaoQuartoResponse response = alocacaoService.alocarHospede(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);

    }

    @GetMapping("/{id}")
    public ResponseEntity<AlocacaoQuartoResponse> buscarAlocacaoPorId(
            @PathVariable Long id) {

        AlocacaoQuartoResponse response = alocacaoService.buscarAlocacaoPorId(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<AlocacaoQuartoResponse>> listarAlocacoes() {

        return ResponseEntity.ok(alocacaoService.listarAlocacoes());
    }

    @GetMapping("/quarto/{quartoId}")
    public ResponseEntity<List<AlocacaoQuartoResponse>> listarAlocacoesPorQuarto(
            @PathVariable Long quartoId) {

        return ResponseEntity.ok(
                alocacaoService.listarAlocacoesPorQuarto(quartoId)
        );
    }

    @PutMapping("/{id}/quarto")
    public ResponseEntity<AlocacaoQuartoResponse> trocarQuarto(
            @PathVariable Long id, @Valid @RequestBody TrocarQuartoRequest request) {

        return ResponseEntity.ok(
                alocacaoService.trocarQuarto(
                        id,
                        request.getNovoQuartoId()
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removerAlocacao(
            @PathVariable Long id) {

        alocacaoService.removerAlocacao(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/quarto/{quartoId}/ocupacao")
    public ResponseEntity<OcupacaoQuartoResponse> buscarOcupacaoPorQuarto(
            @PathVariable Long quartoId
    ) {

        return ResponseEntity.ok(
                alocacaoService.buscarOcupacaoPorQuarto(quartoId)
        );
    }

}
