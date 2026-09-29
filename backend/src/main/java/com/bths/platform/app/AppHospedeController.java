package com.bths.platform.app;

import com.bths.platform.app.dto.MeuTrasladoResponse;
import com.bths.platform.app.dto.MinhaViagemResponse;
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
    private final AppHospedeService appHospedeService;

    public AppHospedeController(
            HospedeService hospedeService,
            AppHospedeService appHospedeService
    ) {
        this.hospedeService =
                hospedeService;
        this.appHospedeService =
                appHospedeService;
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

    @GetMapping("/viagem")
    public ResponseEntity<MinhaViagemResponse> buscarMinhaViagem(
            Authentication authentication
    ) {

        MinhaViagemResponse response =
                appHospedeService.buscarMinhaViagem(
                        authentication.getName()
                );

        if (response == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/traslados")
    public ResponseEntity<List<MeuTrasladoResponse>> buscarMeusTraslados(
            Authentication authentication
    ) {

        List<MeuTrasladoResponse> response =
                appHospedeService.buscarMeusTraslados(
                        authentication.getName()
                );

        return ResponseEntity.ok(
                response);
    }

}