package com.bths.platform.qrcode;

import com.bths.platform.qrcode.dto.QrCodeCheckInResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api/check-in/qr")
public class QrCodeController {

    private final QrCodeService qrCodeService;

    public QrCodeController(QrCodeService qrCodeService) {
        this.qrCodeService = qrCodeService;
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<QrCodeCheckInResponse> identificarHospede(
            @PathVariable String codigo
    ) {

        QrCodeCheckInResponse response =
                qrCodeService.identificarHospede(codigo);

        return ResponseEntity.ok(response);
    }

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
