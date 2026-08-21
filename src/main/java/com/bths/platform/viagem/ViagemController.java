package com.bths.platform.viagem;

import com.bths.platform.viagem.dto.ViagemRequest;
import com.bths.platform.viagem.dto.ViagemResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/viagens")
public class ViagemController {

    private final ViagemService viagemService;

    public ViagemController(ViagemService viagemService) {
        this.viagemService = viagemService;
    }

    @PostMapping
    public ResponseEntity<ViagemResponse> cadastrarViagem(
           @Valid @RequestBody ViagemRequest request){

        ViagemResponse response = viagemService.cadastrarViagem(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<ViagemResponse>> listarViagens(){

        List<ViagemResponse> viagens = viagemService.listarViagens();

        return ResponseEntity.ok(viagens);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ViagemResponse> buscarViagem(
            @PathVariable Long id){

        ViagemResponse viagem = viagemService.buscarViagemPorId(id);

        return ResponseEntity.ok(viagem);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ViagemResponse> atualizarViagem(
            @PathVariable Long id, @Valid @RequestBody ViagemRequest request) {

        ViagemResponse response = viagemService.atualizarViagem(id, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ViagemResponse> deletarViagem(
            @PathVariable Long id) {

        viagemService.deletarViagem(id);

        return ResponseEntity.noContent().build();
    }

}
