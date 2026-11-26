package com.bths.platform.app;

import com.bths.platform.app.dto.*;
import com.bths.platform.hospede.HospedeService;
import com.bths.platform.hospede.dto.HospedeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "App Hóspede", description = "Endpoints self-service destinados ao hóspede autenticado.")
@SecurityRequirement(name = "bthsCookieAuth")
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


    @Operation(summary = "Consultar hóspedes vinculados à conta",
            description = "Retorna os registros de hóspede associados ao usuário autenticado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Hóspedes vinculados retornados com sucesso.",
                    content = @Content(array = @ArraySchema(
                            schema = @Schema(implementation = HospedeResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Autenticação necessária."),
            @ApiResponse(responseCode = "403", description = "Recurso permitido apenas para o perfil HOSPEDE.")
    })
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


    @Operation(summary = "Consultar minha viagem",
            description = "Retorna a viagem operacional associada ao hóspede autenticado na V1.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Viagem encontrada.",
                    content = @Content(schema = @Schema(implementation = MinhaViagemResponse.class))),
            @ApiResponse(responseCode = "204", description = "Nenhuma viagem disponível para o hóspede."),
            @ApiResponse(responseCode = "401", description = "Autenticação necessária."),
            @ApiResponse(responseCode = "403", description = "Recurso permitido apenas para o perfil HOSPEDE.")
    })
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


    @Operation(summary = "Consultar meu quarto",
            description = "Retorna os dados do quarto atualmente atribuído ao hóspede autenticado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Quarto encontrado.",
                    content = @Content(schema = @Schema(implementation = MeuQuartoResponse.class))),
            @ApiResponse(responseCode = "204", description = "O hóspede ainda não possui quarto atribuído."),
            @ApiResponse(responseCode = "401", description = "Autenticação necessária."),
            @ApiResponse(responseCode = "403", description = "Recurso permitido apenas para o perfil HOSPEDE.")
    })
    @GetMapping("/quarto")
    public ResponseEntity<MeuQuartoResponse> buscarMeuQuarto(
            Authentication authentication
    ) {

        MeuQuartoResponse response =
                appHospedeService.buscarMeuQuarto(
                        authentication.getName()
                );

        if (response == null) {
            return ResponseEntity
                    .noContent()
                    .build();
        }

        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Consultar minha hospedagem",
            description = "Retorna os dados da hospedagem associada à viagem do hóspede autenticado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Hospedagem encontrada.",
                    content = @Content(schema = @Schema(implementation = MinhaHospedagemResponse.class))),
            @ApiResponse(responseCode = "204", description = "Nenhuma hospedagem disponível para o hóspede."),
            @ApiResponse(responseCode = "401", description = "Autenticação necessária."),
            @ApiResponse(responseCode = "403", description = "Recurso permitido apenas para o perfil HOSPEDE.")
    })
    @GetMapping("/hospedagem")
    public ResponseEntity<MinhaHospedagemResponse> buscarMinhaHospedagem(
            Authentication authentication
    ) {

        MinhaHospedagemResponse response =
                appHospedeService
                        .buscarMinhaHospedagem(
                                authentication.getName()
                        );

        if (response == null) {
            return ResponseEntity
                    .noContent()
                    .build();
        }

        return ResponseEntity.ok(
                response
        );
    }


    @Operation(summary = "Consultar minha timeline",
            description = "Retorna a agenda operacional da viagem do hóspede autenticado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Timeline retornada com sucesso.",
                    content = @Content(array = @ArraySchema(
                            schema = @Schema(implementation = MinhaTimelineResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Autenticação necessária."),
            @ApiResponse(responseCode = "403", description = "Recurso permitido apenas para o perfil HOSPEDE.")
    })
    @GetMapping("/timeline")
    public ResponseEntity<List<MinhaTimelineResponse>> buscarMinhaTimeline(
            Authentication authentication
    ) {

        List<MinhaTimelineResponse> responses =
                appHospedeService.buscarMinhaTimeline(
                        authentication.getName()
                );

        return ResponseEntity.ok(responses);
    }


    @Operation(summary = "Consultar meus traslados",
            description = "Retorna os transportes associados ao hóspede autenticado, incluindo dados operacionais públicos quando disponíveis.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Traslados retornados com sucesso.",
                    content = @Content(array = @ArraySchema(
                            schema = @Schema(implementation = MeuTrasladoResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Autenticação necessária."),
            @ApiResponse(responseCode = "403", description = "Recurso permitido apenas para o perfil HOSPEDE.")
    })
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


    @Operation(summary = "Consultar meu check-in",
            description = "Retorna o estado atual do check-in do hóspede autenticado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Informações de check-in retornadas com sucesso.",
                    content = @Content(schema = @Schema(implementation = MeuCheckInResponse.class))),
            @ApiResponse(responseCode = "204", description = "Nenhuma informação de check-in disponível."),
            @ApiResponse(responseCode = "401", description = "Autenticação necessária."),
            @ApiResponse(responseCode = "403", description = "Recurso permitido apenas para o perfil HOSPEDE.")
    })
    @GetMapping("/check-in")
    public ResponseEntity<MeuCheckInResponse> buscarMeuCheckIn(
            Authentication authentication
    ) {

        MeuCheckInResponse response =
                appHospedeService.buscarMeuCheckIn(
                        authentication.getName()
                );

        if (response == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Obter QR Code do check-in",
            description = "Retorna a imagem PNG do QR Code de identificação do hóspede autenticado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Imagem do QR Code retornada com sucesso.",
                    content = @Content(mediaType = MediaType.IMAGE_PNG_VALUE,
                            schema = @Schema(type = "string", format = "binary"))),
            @ApiResponse(responseCode = "204", description = "QR Code não disponível."),
            @ApiResponse(responseCode = "401", description = "Autenticação necessária."),
            @ApiResponse(responseCode = "403", description = "Recurso permitido apenas para o perfil HOSPEDE.")
    })
    @GetMapping(value = "/check-in/qr", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> buscarCheckInQR(
            Authentication authentication
    ) {

        byte[] imagem =
                appHospedeService.buscarMeuQrCode(
                        authentication.getName()
                );

        if (imagem == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity
                .ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(imagem);
    }


    @Operation(summary = "Consultar meus avisos",
            description = "Retorna os avisos publicados para a viagem do hóspede autenticado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Avisos retornados com sucesso.",
                    content = @Content(array = @ArraySchema(
                            schema = @Schema(implementation = MeuAvisoResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Autenticação necessária."),
            @ApiResponse(responseCode = "403", description = "Recurso permitido apenas para o perfil HOSPEDE.")
    })
    @GetMapping("/avisos")
    public ResponseEntity<List<MeuAvisoResponse>> buscarMeusAvisos(
            Authentication authentication
    ) {

        List<MeuAvisoResponse> responses =
                appHospedeService.buscarMeusAvisos(
                        authentication.getName()
                );

        return ResponseEntity.ok(
                responses
        );
    }

}