package com.bths.platform.viagem;

import com.bths.platform.viagem.dto.ViagemRequest;
import com.bths.platform.viagem.dto.ViagemResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
