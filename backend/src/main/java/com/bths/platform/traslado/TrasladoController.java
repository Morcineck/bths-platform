package com.bths.platform.traslado;

import com.bths.platform.traslado.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/traslados")
public class TrasladoController {

    private final TrasladoService trasladoService;

    public TrasladoController(TrasladoService trasladoService) {
        this.trasladoService = trasladoService;
    }

    @PostMapping
    public ResponseEntity<TrasladoResponse> cadastrarTraslado(
          @Valid @RequestBody TrasladoRequest request
    ) {

        TrasladoResponse response =
                trasladoService.cadastrarTraslado(request);

        URI location = URI.create(
                "/api/traslados/" + response.getId());

        return ResponseEntity
                .created(location)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrasladoResponse> buscarTraslado(
            @PathVariable Long id
    ) {

        TrasladoResponse response =
                trasladoService.buscarTrasladoPorId(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/viagem/{viagemId}")
    public ResponseEntity<List<TrasladoResponse>> listarTrasladoPorViagem(
            @PathVariable Long viagemId
    ) {

        List<TrasladoResponse> response =
                trasladoService.listarTrasladosPorViagem(viagemId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/hospede/{hospedeId}")
    public ResponseEntity<List<TrasladoResponse>> listarTrasladoPorHospede(
            @PathVariable Long hospedeId
    ) {

        List<TrasladoResponse> response =
                trasladoService.listarTrasladosPorHospede(hospedeId);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TrasladoResponse> atualizarTraslado(
            @PathVariable Long id,
           @Valid @RequestBody TrasladoUpdateRequest request
    ) {

        TrasladoResponse response = trasladoService.atualizarTraslado(id, request);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TrasladoResponse> atualizarStatusTraslado(
            @PathVariable Long id,
           @Valid @RequestBody TrasladoStatusRequest request
    ) {

        TrasladoResponse response =
                trasladoService.atualizarStatusTraslado(id, request);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/corrigir-status")
    public  ResponseEntity<TrasladoResponse> corrigirStatusTraslado(
            @PathVariable Long id,
           @Valid @RequestBody TrasladoCorrecaoStatusRequest request

    ) {

        TrasladoResponse response =
                trasladoService.corrigirStatusTraslado(id, request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/historico-status")
    public ResponseEntity<List<HistoricoStatusTrasladoResponse>> listarHistoricoStatusTraslado(
            @PathVariable Long id
    ) {

        List<HistoricoStatusTrasladoResponse> response =
                trasladoService.listarHistoricoStatusTraslado(id);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/operacao")
    public ResponseEntity<TrasladoResponse> associarOperacao(
            @PathVariable Long id,
            @Valid @RequestBody TrasladoOperacaoRequest request
    ) {

        TrasladoResponse response =
                trasladoService.associarOperacao(id, request);

        return ResponseEntity.ok(response);
    }

}
