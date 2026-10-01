package com.bths.platform.hospedagem;

import com.bths.platform.hospedagem.dto.HospedagemRequest;
import com.bths.platform.hospedagem.dto.HospedagemResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hospedagens")
public class HospedagemController {

    private final HospedagemService hospedagemService;

    public HospedagemController(
            HospedagemService hospedagemService
    ) {

        this.hospedagemService = hospedagemService;
    }

    @PostMapping
    public ResponseEntity<HospedagemResponse> cadastrarHospedagem(
            @Valid @RequestBody HospedagemRequest request
    ) {

        HospedagemResponse response =
                hospedagemService
                        .cadastrarHospedagem(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("{id}")
    public ResponseEntity<HospedagemResponse> buscarHospedagemPorId(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                hospedagemService
                        .buscarHospedagemPorId(
                                id
                        )
        );
    }

    @GetMapping
    public ResponseEntity<List<HospedagemResponse>> listarHospedagens() {

        return ResponseEntity.ok(
                hospedagemService
                        .listarHospedagens()
        );
    }

    @GetMapping("/viagem/{viagemId}")
    public ResponseEntity<List<HospedagemResponse>> listarHospedagensPorViagem(
            @PathVariable Long viagemId
    ) {

        return ResponseEntity.ok(
                hospedagemService
                        .listarHospedagensPorViagem(
                                viagemId
                        )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<HospedagemResponse> atualizarHospedagem(
            @PathVariable Long id,
            @Valid @RequestBody HospedagemRequest request
    ) {

        return ResponseEntity.ok(
                hospedagemService.atualizarHospedagem(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HospedagemResponse> deletarHospedagem(
            @PathVariable Long id
    ) {

        hospedagemService.deletarHospedagem(id);

        return ResponseEntity
                .noContent()
                .build();

    }
}
