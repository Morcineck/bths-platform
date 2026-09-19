package com.bths.platform.qrcode;

import com.bths.platform.alocacao.AlocacaoQuarto;
import com.bths.platform.alocacao.AlocacaoQuartoRepository;
import com.bths.platform.hospede.exception.HospedeNaoEncontradoException;
import com.bths.platform.hospede.Hospede;
import com.bths.platform.hospede.HospedeRepository;
import com.bths.platform.qrcode.dto.QrCodeCheckInResponse;
import com.bths.platform.qrcode.mapper.QrCodeMapper;
import com.bths.platform.viagem.Viagem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QrCodeServiceTest {

    @Mock
    private HospedeRepository hospedeRepository;

    @Mock
    private AlocacaoQuartoRepository alocacaoRepository;

    @Mock
    private QrCodeMapper qrCodeMapper;

    @Mock
    private QrCodeGeradorService qrCodeGeradorService;

    private QrCodeService qrCodeService;

    @BeforeEach
    void setUp() {

        qrCodeService = new QrCodeService(
                hospedeRepository,
                alocacaoRepository,
                qrCodeMapper,
                qrCodeGeradorService
        );
    }

    @Test
    void deveIdentificarHospedeComQuartoQuandoQrCodeForValido() {

        // Arrange
        String codigoQr = "550e8400-e29b-41d4-a716-446655440000";

        Viagem viagem = new Viagem();
        viagem.setId(1L);

        Hospede hospede = new Hospede();
        hospede.setId(1L);
        hospede.setCodigoCheckIn(codigoQr);
        hospede.setViagem(viagem);

        AlocacaoQuarto alocacao = new AlocacaoQuarto();

        QrCodeCheckInResponse responseEsperado = new QrCodeCheckInResponse();

        when(hospedeRepository.findByCodigoCheckIn(codigoQr))
                .thenReturn(Optional.of(hospede));

        when(alocacaoRepository.findByHospedeIdAndViagemId(
                hospede.getId(),
                hospede.getViagem().getId()
        )).thenReturn(Optional.of(alocacao));

        when(qrCodeMapper.paraResponse(hospede, alocacao))
                .thenReturn(responseEsperado);

        // Act
        QrCodeCheckInResponse response =
                qrCodeService.identificarHospede(codigoQr);

        // Assert
        assertNotNull(response);
        assertSame(responseEsperado, response);

        verify(hospedeRepository)
                .findByCodigoCheckIn(codigoQr);

        verify(qrCodeMapper)
                .paraResponse(hospede, alocacao);
    }

    @Test
    void deveIdentificarHospedeSemQuartoQuandoQrCodeForValido() {

        // Arrange
        String codigoQr = "550e8400-e29b-41d4-a716-446655440001";

        Viagem viagem = new Viagem();
        viagem.setId(1L);

        Hospede hospede = new Hospede();
        hospede.setId(2L);
        hospede.setCodigoCheckIn(codigoQr);
        hospede.setViagem(viagem);

        QrCodeCheckInResponse responseEsperado = new QrCodeCheckInResponse();

        when(hospedeRepository.findByCodigoCheckIn(codigoQr))
                .thenReturn(Optional.of(hospede));

        when(alocacaoRepository.findByHospedeIdAndViagemId(
                hospede.getId(),
                hospede.getViagem().getId()
        )).thenReturn(Optional.empty());

        when(qrCodeMapper.paraResponse(hospede, null))
                .thenReturn(responseEsperado);

        // Act
        QrCodeCheckInResponse response =
                qrCodeService.identificarHospede(codigoQr);

        // Assert
        assertNotNull(response);
        assertSame(responseEsperado, response);

        verify(hospedeRepository)
                .findByCodigoCheckIn(codigoQr);

        verify(alocacaoRepository)
                .findByHospedeIdAndViagemId(
                        hospede.getId(),
                        hospede.getViagem().getId()
                );

        verify(qrCodeMapper)
                .paraResponse(hospede, null);
    }

    @Test
    void deveLancarExcecaoQuandoQrCodeForInvalido() {

        // Arrange
        String codigoQr = "qr-code-invalido";

        when(hospedeRepository.findByCodigoCheckIn(codigoQr))
                .thenReturn(Optional.empty());

        // Act
        HospedeNaoEncontradoException exception = assertThrows(
                HospedeNaoEncontradoException.class,
                () -> qrCodeService.identificarHospede(codigoQr)
        );

        // Assert
        assertEquals(
                "QR Code inválido ou hóspede não encontrado!",
                exception.getMessage()
        );

        verify(hospedeRepository)
                .findByCodigoCheckIn(codigoQr);
    }

    @Test
    void deveGerarImagemQrCodeQuandoCodigoForValido() {

        // Arrange
        String codigoQr = "550e8400-e29b-41d4-a716-446655440000";

        Hospede hospede = new Hospede();
        hospede.setId(1L);
        hospede.setCodigoCheckIn(codigoQr);

        byte[] imagemEsperada = new byte[]{1, 2, 3};

        when(hospedeRepository.findByCodigoCheckIn(codigoQr))
                .thenReturn(Optional.of(hospede));

        when(qrCodeGeradorService.gerarQRCode(codigoQr))
                .thenReturn(imagemEsperada);

        // Act
        byte[] imagem = qrCodeService.gerarImagemQrCode(codigoQr);

        // Assert
        assertSame(imagemEsperada, imagem);

        verify(hospedeRepository)
                .findByCodigoCheckIn(codigoQr);

        verify(qrCodeGeradorService)
                .gerarQRCode(codigoQr);
    }

    @Test
    void deveLancarExcecaoAoGerarImagemQuandoQrCodeForInvalido() {

        // Arrange
        String codigoQr = "qr-code-invalido";

        when(hospedeRepository.findByCodigoCheckIn(codigoQr))
                .thenReturn(Optional.empty());

        // Act
        HospedeNaoEncontradoException exception = assertThrows(
                HospedeNaoEncontradoException.class,
                () -> qrCodeService.gerarImagemQrCode(codigoQr)
        );

        // Assert
        assertEquals(
                "QR Code inválido ou hóspede não encontrado!",
                exception.getMessage()
        );

        verify(hospedeRepository)
                .findByCodigoCheckIn(codigoQr);

        verifyNoInteractions(qrCodeGeradorService);
    }
}
