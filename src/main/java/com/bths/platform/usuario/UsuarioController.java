package com.bths.platform.usuario;

import com.bths.platform.usuario.dto.UsuarioRequest;
import com.bths.platform.usuario.dto.UsuarioResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> cadastrarUsuario(
           @Valid @RequestBody UsuarioRequest request
    ) {

        UsuarioResponse response =
                usuarioService.cadastrarUsuario(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> buscarUsuarioPorId(
            @PathVariable UUID id
    ) {

        UsuarioResponse response =
                usuarioService.buscarUsuarioPorId(id);

        return ResponseEntity.ok(response);
    }
}
