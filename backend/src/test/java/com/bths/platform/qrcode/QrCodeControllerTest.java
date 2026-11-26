package com.bths.platform.qrcode;

import com.bths.platform.qrcode.dto.QrCodeCheckInResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QrCodeControllerTest {

    @Mock
    private QrCodeService qrCodeService;

    private QrCodeController qrCodeController;

    @BeforeEach
    void setUp() {
        qrCodeController = new QrCodeController(qrCodeService);
    }

    @Test
    void deveIdentificarHospedeQuandoQrCodeForValido() {

        // Arrange
        String codigoQr = "550e8400-e29b-41d4-a716-446655440000";

        QrCodeCheckInResponse responseEsperado =
                new QrCodeCheckInResponse();

        when(qrCodeService.identificarHospede(codigoQr))
                .thenReturn(responseEsperado);

        // Act
        ResponseEntity<QrCodeCheckInResponse> response =
                qrCodeController.identificarHospede(codigoQr);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertSame(responseEsperado, response.getBody());

        verify(qrCodeService)
                .identificarHospede(codigoQr);
    }

    @Test
    void deveRetornarImagemPngQuandoQrCodeForValido() {

        // Arrange
        String codigoQr = "550e8400-e29b-41d4-a716-446655440000";

        byte[] imagemEsperada = new byte[]{1, 2, 3};

        when(qrCodeService.gerarImagemQrCode(codigoQr))
                .thenReturn(imagemEsperada);

        // Act
        ResponseEntity<byte[]> response =
                qrCodeController.getImagemQrCode(codigoQr);

        // Assert
        assertEquals(200, response.getStatusCode().value());

        assertEquals(
                MediaType.IMAGE_PNG,
                response.getHeaders().getContentType()
        );

        assertArrayEquals(
                imagemEsperada,
                response.getBody()
        );

        verify(qrCodeService)
                .gerarImagemQrCode(codigoQr);
    }
}