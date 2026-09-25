package com.bths.platform.operacaoTraslado;



import com.bths.platform.operacaoTraslado.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/traslados/operacoes")
public class OperacaoTrasladoController {

    private final OperacaoTrasladoService operacaoTrasladoService;

    public OperacaoTrasladoController(
            OperacaoTrasladoService operacaoTrasladoService
    ) {
        this.operacaoTrasladoService =
                operacaoTrasladoService;
    }

    @PostMapping
    public ResponseEntity<OperacaoTrasladoResponse> criarOperacao(
            @Valid @RequestBody OperacaoTrasladoRequest request
    ) {

        OperacaoTrasladoResponse response =
                operacaoTrasladoService.criarOperacao(
                        request
                );

        URI location = URI.create(
                "/api/traslados/operacoes/"
                        + response.getId()
        );

        return ResponseEntity
                .created(location)
                .body(response);
    }

    @PatchMapping(
            "/{operacaoId}/traslados/{trasladoId}"
    )
    public ResponseEntity<OperacaoTrasladoResponse> vincularTraslado(
            @PathVariable Long operacaoId,
            @PathVariable Long trasladoId
    ) {

        OperacaoTrasladoResponse response =
                operacaoTrasladoService
                        .vincularTraslado(
                                operacaoId,
                                trasladoId
                        );

        return ResponseEntity.ok(response);
    }

    @PatchMapping(
            "/{operacaoId}/veiculo/{veiculoId}"
    )
    public ResponseEntity<OperacaoTrasladoResponse> alterarVeiculo(
            @PathVariable Long operacaoId,
            @PathVariable Long veiculoId
    ) {

        OperacaoTrasladoResponse response =
                operacaoTrasladoService.alterarVeiculo(
                        operacaoId,
                        veiculoId
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/viagem/{viagemId}")
    public ResponseEntity<List<OperacaoTrasladoResponse>> listarPorViagem(
            @PathVariable Long viagemId
    ) {

        List<OperacaoTrasladoResponse> response =
                operacaoTrasladoService
                        .listarPorViagem(
                                viagemId
                        );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OperacaoTrasladoResponse> atualizarOperacao(
            @PathVariable Long id,
            @Valid @RequestBody OperacaoTrasladoUpdateRequest request
    ) {

        OperacaoTrasladoResponse response =
                operacaoTrasladoService.atualizarOperacao(
                        id,
                        request
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OperacaoTrasladoResponse> buscarPorId(
            @PathVariable Long id
    ) {

        OperacaoTrasladoResponse response =
                operacaoTrasladoService
                        .buscarPorId(id);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirOperacao(
            @PathVariable Long id
    ) {

        operacaoTrasladoService
                .excluirOperacao(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/passageiros")
    public ResponseEntity<List<OperacaoTrasladoPassageiroResponse>>
    listarPassageiros(
            @PathVariable Long id
    ) {

        List<OperacaoTrasladoPassageiroResponse> passageiros =
                operacaoTrasladoService
                        .listarPassageiros(id);

        return ResponseEntity.ok(
                passageiros
        );
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OperacaoTrasladoResponse> atualizarStatus(
            @PathVariable Long id,
            @Valid @RequestBody OperacaoTrasladoStatusRequest request
    ) {

        OperacaoTrasladoResponse response =
                operacaoTrasladoService.atualizarStatus(
                        id,
                        request
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/historico-status")
    public ResponseEntity<List<HistoricoStatusOperacaoTrasladoResponse>>
    listarHistoricoStatus(
            @PathVariable Long id
    ) {

        List<HistoricoStatusOperacaoTrasladoResponse> response =
                operacaoTrasladoService
                        .listarHistoricoStatus(id);

        return ResponseEntity.ok(response);
    }
}
