package com.bths.platform.qrcode;

import com.bths.platform.alocacao.AlocacaoQuarto;
import com.bths.platform.alocacao.AlocacaoQuartoRepository;
import com.bths.platform.exception.HospedeNaoEncontradoException;
import com.bths.platform.hospede.Hospede;
import com.bths.platform.hospede.HospedeRepository;
import com.bths.platform.qrcode.dto.QrCodeCheckInResponse;
import com.bths.platform.qrcode.mapper.QrCodeMapper;
import org.springframework.stereotype.Service;

@Service
public class QrCodeService {

    private final HospedeRepository hospedeRepository;
    private final AlocacaoQuartoRepository alocacaoQuartoRepository;
    private final QrCodeMapper qrCodeMapper;
    private final QrCodeGeradorService qrCodeGeradorService;

    public QrCodeService(
            HospedeRepository hospedeRepository,
            AlocacaoQuartoRepository alocacaoQuartoRepository,
            QrCodeMapper qrCodeMapper, QrCodeGeradorService qrCodeGeradorService

    ) {

        this.hospedeRepository = hospedeRepository;
        this.alocacaoQuartoRepository = alocacaoQuartoRepository;
        this.qrCodeMapper = qrCodeMapper;
        this.qrCodeGeradorService = qrCodeGeradorService;
    }

    public QrCodeCheckInResponse identificarHospede(String codigo) {

        Hospede hospede = hospedeRepository
                .findByCodigoCheckIn(codigo)
                .orElseThrow(() -> new HospedeNaoEncontradoException(
                                "QR Code inválido ou hóspede não encontrado!"
                        )
                );

        Long viagemId = hospede.getViagem().getId();

       AlocacaoQuarto alocacao =
               alocacaoQuartoRepository
                       .findByHospedeIdAndViagemId(
                               hospede.getId(),
                               viagemId
                       )
                       .orElse(null);

       return qrCodeMapper.paraResponse(
               hospede,
               alocacao
       );
    }

    public byte[] gerarImagemQrCode(String codigo) {

        Hospede hospede = hospedeRepository
                .findByCodigoCheckIn(codigo)
                .orElseThrow(() -> new HospedeNaoEncontradoException(
                        "QR Code inválido ou hóspede não encontrado!"
                ));

        return qrCodeGeradorService.gerarQRCode(
                hospede.getCodigoCheckIn()
        );
    }

}
