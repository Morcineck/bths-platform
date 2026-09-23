package com.bths.platform.motorista;

import com.bths.platform.motorista.dto.MotoristaRequest;
import com.bths.platform.motorista.dto.MotoristaResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/motoristas")
public class MotoristaController {

    private final MotoristaService motoristaService;

    public MotoristaController(
            MotoristaService motoristaService
    ) {
        this.motoristaService = motoristaService;
    }

    @PostMapping
    public ResponseEntity<MotoristaResponse> cadastrarMotorista(
            @Valid @RequestBody MotoristaRequest request
    ) {

        MotoristaResponse response =
                motoristaService.cadastrarMotorista(request);

        URI location = URI.create(
                "/api/motoristas/" + response.getId()
        );

        return ResponseEntity
                .created(location)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MotoristaResponse> buscarMotorista(
            @PathVariable Long id
    ) {

        MotoristaResponse response =
                motoristaService.buscarMotoristaPorId(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<MotoristaResponse>> listarMotoristas() {

        List<MotoristaResponse> response =
                motoristaService.listarMotoristas();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MotoristaResponse> atualizarMotorista(
            @PathVariable Long id,
            @Valid @RequestBody MotoristaRequest request
    ) {

        MotoristaResponse response =
                motoristaService.atualizarMotorista(
                        id,
                        request
                );

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/inativar")
    public ResponseEntity<MotoristaResponse> inativarMotorista(
            @PathVariable Long id
    ) {

        MotoristaResponse response =
                motoristaService.inativarMotorista(id);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/ativar")
    public ResponseEntity<MotoristaResponse> ativarMotorista(
            @PathVariable Long id
    ) {

        MotoristaResponse response =
                motoristaService.ativarMotorista(id);

        return ResponseEntity.ok(response);
    }
}