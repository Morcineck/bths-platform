package com.bths.platform.veiculo;

import com.bths.platform.veiculo.dto.VeiculoRequest;
import com.bths.platform.veiculo.dto.VeiculoResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/veiculos")
public class VeiculoController {

    private final VeiculoService veiculoService;

    public VeiculoController(VeiculoService veiculoService) {
        this.veiculoService = veiculoService;
    }

    @PostMapping
    public ResponseEntity<VeiculoResponse> cadastrarVeiculo(
            @Valid @RequestBody VeiculoRequest request
    ) {

        VeiculoResponse response =
                veiculoService.cadastrarVeiculo(request);

        URI location = URI.create(
                "/api/veiculos/" + response.getId()

        );

        return ResponseEntity
                .created(location)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<VeiculoResponse>> listarVeiculos() {

        List<VeiculoResponse> response =
                veiculoService.listarVeiculos();

        return ResponseEntity.ok(response);

    }

    @GetMapping("/{id}")
    public ResponseEntity<VeiculoResponse> buscarVeiculoPorId(
            @PathVariable Long id
    ) {

        VeiculoResponse response =
                veiculoService.buscarVeiculoPorId(id);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VeiculoResponse> atualizarVeiculo(
            @PathVariable Long id,
            @Valid @RequestBody VeiculoRequest request
    ) {

        VeiculoResponse response =
                veiculoService.atualizarVeiculo(id, request);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/inativar")
    public ResponseEntity<VeiculoResponse> inativarVeiculo(
            @PathVariable Long id
    ) {

        VeiculoResponse response =
                veiculoService.inativarVeiculo(id);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/ativar")
    public ResponseEntity<VeiculoResponse> ativarVeiculo(
            @PathVariable Long id
    ) {

        VeiculoResponse response =
                veiculoService.ativarVeiculo(id);

        return ResponseEntity.ok(response);
    }
}
