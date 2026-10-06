package com.bths.platform.hospede;

import com.bths.platform.hospede.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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

    @PutMapping("/{id}/acesso-bths/senha")
    public ResponseEntity<Void> redefinirSenhaAcessoBths(
            @PathVariable Long id,
            @Valid @RequestBody HospedeRedefinirSenhaRequest request
    ) {

        hospedeService.redefinirSenhaAcessoBths(
                id,
                request
        );

        return ResponseEntity
                .noContent()
                .build();
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

    @PatchMapping("/{hospedeId}/usuario/{usuarioId}")
    public ResponseEntity<HospedeResponse> vincularUsuarioAoHospede(
            @PathVariable Long hospedeId,
            @PathVariable UUID usuarioId
    ) {

        HospedeResponse response =
                hospedeService.vincularUsuarioAoHospede(
                        hospedeId,
                        usuarioId
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/acesso-bths")
    public ResponseEntity<HospedeAcessoBthsResponse> buscarAcessoBths(
            @PathVariable Long id
    ) {

        HospedeAcessoBthsResponse response =
                hospedeService.buscarAcessoBths(
                        id
                );

        return ResponseEntity.ok(
                response
        );
    }

    @PostMapping("/{id}/acesso-bths")
    public ResponseEntity<HospedeAcessoBthsResponse> criarOuVincularAcessoBths(
            @PathVariable Long id,
            @Valid @RequestBody HospedeCriarAcessoBthsRequest request
    ) {

        HospedeAcessoBthsResponse response =
                hospedeService.criarOuVincularAcessoBths(
                        id,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

}
