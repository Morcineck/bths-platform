package com.bths.platform.agenda;

import com.bths.platform.agenda.dto.AgendaViagemRequest;
import com.bths.platform.agenda.dto.AgendaViagemResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Agenda", description = "Gestão da agenda e da timeline operacional das viagens.")
@RestController
@RequestMapping("/api/agenda-viagem")
public class AgendaViagemController {

    private final AgendaViagemService agendaViagemService;

    public AgendaViagemController(
            AgendaViagemService agendaViagemService
    ) {
        this.agendaViagemService =
                agendaViagemService;
    }

    @PostMapping
    public ResponseEntity<AgendaViagemResponse> cadastrar(
            @Valid @RequestBody AgendaViagemRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        agendaViagemService.cadastrar(
                                request
                        )
                );
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgendaViagemResponse> buscarPorId(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                agendaViagemService.buscarPorId(
                        id
                )
        );
    }

    @GetMapping("/viagem/{viagemId}")
    public ResponseEntity<List<AgendaViagemResponse>> listarPorViagem(
            @PathVariable Long viagemId
    ) {

        return ResponseEntity.ok(
                agendaViagemService.listarPorViagem(
                        viagemId
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<AgendaViagemResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody AgendaViagemRequest request
    ) {

        return ResponseEntity.ok(
                agendaViagemService.atualizar(
                        id,
                        request
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @PathVariable Long id
    ) {

        agendaViagemService.deletar(
                id
        );

        return ResponseEntity
                .noContent()
                .build();
    }
}