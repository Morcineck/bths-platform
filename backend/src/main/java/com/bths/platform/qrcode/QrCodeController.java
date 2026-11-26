package com.bths.platform.qrcode;

import com.bths.platform.qrcode.dto.QrCodeCheckInResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Check-in e QR Code",
        description = "Operações de check-in, confirmação de presença e identificação por QR Code.")
@SecurityRequirement(name = "bthsCookieAuth")
@RestController
@RequestMapping("/api/check-in/qr")
public class QrCodeController {

    private final QrCodeService qrCodeService;

    public QrCodeController(QrCodeService qrCodeService) {
        this.qrCodeService = qrCodeService;
    }


    @Operation(summary = "Identificar hóspede pelo QR Code", description = "Consulta a identificação do hóspede usando o código do check-in. Acesso ADMIN/STAFF.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Hóspede identificado com sucesso."),
            @ApiResponse(responseCode = "401", description = "Autenticação necessária.", content = @Content),
            @ApiResponse(responseCode = "403", description = "Permissão insuficiente.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Código de identificação não encontrado.", content = @Content)
    })
    @GetMapping("/{codigo}")
    public ResponseEntity<QrCodeCheckInResponse> identificarHospede(
            @PathVariable String codigo
    ) {

        QrCodeCheckInResponse response =
                qrCodeService.identificarHospede(codigo);

        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Gerar imagem do QR Code", description = "Retorna a imagem PNG correspondente ao código de identificação informado. Acesso ADMIN/STAFF.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Imagem do QR Code gerada com sucesso.",
                    content = @Content(mediaType = MediaType.IMAGE_PNG_VALUE,
                            schema = @Schema(type = "string", format = "binary"))),
            @ApiResponse(responseCode = "401", description = "Autenticação necessária.", content = @Content),
            @ApiResponse(responseCode = "403", description = "Permissão insuficiente.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Código de identificação não encontrado.", content = @Content)
    })
    @GetMapping(
            value = "/{codigo}/imagem",
            produces = MediaType.IMAGE_PNG_VALUE
    )
    public ResponseEntity<byte[]> getImagemQrCode(
            @PathVariable String codigo
    ) {

        byte[] imagem =
                qrCodeService.gerarImagemQrCode(codigo);

        return ResponseEntity
                .ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(imagem);

    }

}
