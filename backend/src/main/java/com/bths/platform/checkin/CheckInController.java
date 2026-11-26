package com.bths.platform.checkin;

import com.bths.platform.checkin.dto.CheckInRequest;
import com.bths.platform.checkin.dto.CheckInResponse;
import com.bths.platform.checkin.dto.NaoComparecimentoRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Check-in e QR Code",
        description = "Operações de check-in, confirmação de presença e identificação por QR Code.")
@SecurityRequirement(name = "bthsCookieAuth")
@RestController
@RequestMapping("/api/hospedes")
public class CheckInController {

    private final CheckInService checkInService;

    public CheckInController(CheckInService checkInService) {
        this.checkInService = checkInService;
    }

    @Operation(summary = "Realizar check-in",
            description = "Registra o check-in de um hóspede, identificando o operador autenticado. Acesso ADMIN/STAFF.")
    @Parameter(name = "X-XSRF-TOKEN", in = ParameterIn.HEADER,
            description = "Token CSRF obtido após inicializar o fluxo em /api/auth/csrf.", required = true)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Check-in realizado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados de check-in inválidos.", content = @Content),
            @ApiResponse(responseCode = "401", description = "Autenticação necessária.", content = @Content),
            @ApiResponse(responseCode = "403", description = "Permissão insuficiente.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Hóspede não encontrado.", content = @Content),
            @ApiResponse(responseCode = "409", description = "Check-in já realizado ou regra operacional impeditiva.", content = @Content)
    })
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

    @Operation(summary = "Consultar check-in",
            description = "Consulta a situação de check-in de um hóspede. Acesso ADMIN/STAFF.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Informações de check-in retornadas com sucesso."),
            @ApiResponse(responseCode = "401", description = "Autenticação necessária.", content = @Content),
            @ApiResponse(responseCode = "403", description = "Permissão insuficiente.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Hóspede não encontrado.", content = @Content)
    })
    @GetMapping("/{hospedeId}/check-in")
    public ResponseEntity<CheckInResponse> consultarCheckIn(
            @PathVariable Long hospedeId
    ) {

        CheckInResponse response =
                checkInService.consultarCheckIn(hospedeId);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Registrar não comparecimento",
            description = "Registra a ausência do hóspede na viagem, identificando o operador autenticado. Acesso ADMIN/STAFF.")
    @Parameter(name = "X-XSRF-TOKEN", in = ParameterIn.HEADER,
            description = "Token CSRF obtido após inicializar o fluxo em /api/auth/csrf.", required = true)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Não comparecimento registrado com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados inválidos.", content = @Content),
            @ApiResponse(responseCode = "401", description = "Autenticação necessária.", content = @Content),
            @ApiResponse(responseCode = "403", description = "Permissão insuficiente.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Hóspede não encontrado.", content = @Content),
            @ApiResponse(responseCode = "409", description = "Não comparecimento já registrado ou operação incompatível.", content = @Content)
    })
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