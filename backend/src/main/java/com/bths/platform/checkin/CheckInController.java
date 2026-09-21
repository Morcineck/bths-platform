package com.bths.platform.checkin;

import com.bths.platform.checkin.dto.CheckInRequest;
import com.bths.platform.checkin.dto.CheckInResponse;
import com.bths.platform.checkin.dto.NaoComparecimentoRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hospedes")
public class CheckInController {

    private final CheckInService checkInService;

    public CheckInController(CheckInService checkInService) {
        this.checkInService = checkInService;
    }

    @PostMapping("/{hospedeId}/check-in")
    public ResponseEntity<CheckInResponse> realizarCheckIn(
            @PathVariable Long hospedeId,
            @Valid @RequestBody CheckInRequest request,
            Authentication authentication
    ) {

            CheckInResponse response =
                    checkInService.realizarCheckIn(
                            hospedeId,
                            request,
                            authentication
                    );

            return ResponseEntity.ok(response);
        }

    @GetMapping("/{hospedeId}/check-in")
    public ResponseEntity<CheckInResponse> consultarCheckIn(
            @PathVariable Long hospedeId
    ) {

        CheckInResponse response =
                checkInService.consultarCheckIn(hospedeId);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{hospedeId}/nao-comparecimento")
    public ResponseEntity<CheckInResponse> registrarNaoComparecimento(
            @PathVariable Long hospedeId,
            @Valid @RequestBody NaoComparecimentoRequest request,
            Authentication authentication
    ) {

        CheckInResponse response =
                checkInService.registrarNaoComparecimento(
                        hospedeId,
                        request,
                        authentication
                );

        return ResponseEntity.ok(response);
    }

}
