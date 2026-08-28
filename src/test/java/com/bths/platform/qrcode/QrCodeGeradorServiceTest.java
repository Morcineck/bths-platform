package com.bths.platform.qrcode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class QrCodeGeradorServiceTest {

    private QrCodeGeradorService qrCodeGeradorService;

    @BeforeEach
    void setUp() {
        qrCodeGeradorService = new QrCodeGeradorService();
    }

    @Test
    void deveGerarQrCodeEmPng() {

        // Arrange
        String conteudo = "550e8400-e29b-41d4-a716-446655440000";

        // Act
        byte[] imagem = qrCodeGeradorService.gerarQRCode(conteudo);

        // Assert
        assertNotNull(imagem);
        assertTrue(imagem.length > 0);
    }

    @Test
    void deveGerarImagemPngValida() throws IOException {

        // Arrange
        String conteudo = "550e8400-e29b-41d4-a716-446655440000";

        // Act
        byte[] imagem = qrCodeGeradorService.gerarQRCode(conteudo);

        var bufferedImage = ImageIO.read(
                new ByteArrayInputStream(imagem)
        );

        // Assert
        assertNotNull(bufferedImage);
        assertEquals(300, bufferedImage.getWidth());
        assertEquals(300, bufferedImage.getHeight());
    }
}