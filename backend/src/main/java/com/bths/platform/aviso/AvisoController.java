package com.bths.platform.aviso;

import com.bths.platform.aviso.dto.AvisoRequest;
import com.bths.platform.aviso.dto.AvisoResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Avisos", description = "Gestão dos comunicados e avisos destinados aos hóspedes.")
@RestController
@RequestMapping("/api/avisos")
public class AvisoController {

    private final AvisoService avisoService;

    public AvisoController(AvisoService avisoService) {

        this.avisoService = avisoService;

    }

    @PostMapping
    public ResponseEntity<AvisoResponse> cadastrarAviso(
            @Valid @RequestBody AvisoRequest request
    ) {

        AvisoResponse avisoResponse =
                avisoService.cadastrarAviso(request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(avisoResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AvisoResponse> buscarAviso(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                avisoService.buscarAviso(id)
        );
    }

    @GetMapping("/viagem/{viagemId}")
    public ResponseEntity<List<AvisoResponse>> listarAviso(
            @PathVariable Long viagemId
    ) {

        return ResponseEntity.ok(
                avisoService.listarAvisoPorViagem(viagemId)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<AvisoResponse> atualizarAviso(
            @PathVariable Long id,
            @Valid @RequestBody AvisoRequest request
    ) {

        return ResponseEntity.ok(
                avisoService.atualizarAviso(
                        id,
                        request
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarAviso(
            @PathVariable Long id
    ) {

        avisoService.deletarAviso(
                id
        );

        return ResponseEntity
                .noContent()
                .build();
    }

}
