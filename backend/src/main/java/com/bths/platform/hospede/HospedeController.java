package com.bths.platform.hospede;

import com.bths.platform.hospede.dto.HospedeRequest;
import com.bths.platform.hospede.dto.HospedeResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hospedes")
public class HospedeController {

    private final HospedeService hospedeService;

    public HospedeController(HospedeService hospedeService) {
        this.hospedeService = hospedeService;
    }

    @PostMapping
    public ResponseEntity<HospedeResponse> cadastrarHospede(
            @Valid @RequestBody HospedeRequest request
    ) {

        HospedeResponse response =
                hospedeService.cadastrarHospede(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<HospedeResponse>> listarHospedes() {

        return ResponseEntity.ok
                (hospedeService.listarHospedes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HospedeResponse> buscarHospedePorId(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                hospedeService.buscarHospedePorId(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<HospedeResponse> atualizarHospede(
            @PathVariable Long id,
            @Valid @RequestBody HospedeRequest request
    ) {

        return ResponseEntity.ok(
                hospedeService.atualizarHospede(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarHospede(
            @PathVariable Long id
    ) {
        hospedeService.deletarHospede(id);
        return ResponseEntity.noContent().build();

    }

    @GetMapping("/viagem/{viagemId}")
    public ResponseEntity<List<HospedeResponse>> listarHospedesPorViagem(
            @PathVariable Long viagemId
    ) {

        return ResponseEntity.ok(
                hospedeService.listarHospedesPorViagem(viagemId)
        );
    }

}
