package com.bths.platform.app;

import com.bths.platform.hospede.HospedeService;
import com.bths.platform.hospede.dto.HospedeResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/app")
public class AppHospedeController {

    private final HospedeService hospedeService;

    public AppHospedeController(
            HospedeService hospedeService
    ) {
        this.hospedeService =
                hospedeService;
    }

    @GetMapping("/hospedes")
    public ResponseEntity<List<HospedeResponse>>
    buscarHospedesDoUsuario(
            Authentication authentication
    ) {

        List<HospedeResponse> response =
                hospedeService
                        .buscarHospedesDoUsuario(
                                authentication.getName()
                        );

        return ResponseEntity.ok(
                response
        );
    }
}