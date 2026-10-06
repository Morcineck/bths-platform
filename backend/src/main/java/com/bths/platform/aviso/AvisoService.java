package com.bths.platform.aviso;

import com.bths.platform.aviso.dto.AvisoRequest;
import com.bths.platform.aviso.dto.AvisoResponse;
import com.bths.platform.aviso.exception.AvisoNaoEncontradoException;
import com.bths.platform.aviso.mapper.AvisoMapper;
import com.bths.platform.viagem.Viagem;
import com.bths.platform.viagem.ViagemRepository;
import com.bths.platform.viagem.exception.ViagemNaoEncontradaException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AvisoService {

    private final AvisoRepository avisoRepository;
    private final ViagemRepository viagemRepository;
    private final AvisoMapper avisoMapper;

    public AvisoService(
            AvisoRepository avisoRepository,
            ViagemRepository viagemRepository,
            AvisoMapper avisoMapper
    ) {

        this.avisoRepository = avisoRepository;
        this.viagemRepository = viagemRepository;
        this.avisoMapper = avisoMapper;

    }

    public AvisoResponse cadastrarAviso(
            AvisoRequest request
    ) {

        Viagem viagem = viagemRepository
                .findById(request.getViagemId())
                .orElseThrow(() ->
                        new ViagemNaoEncontradaException(
                                "Viagem não encontrada!"
                        )
                );

        Aviso aviso =
                new Aviso();

        avisoMapper.atualizarEntidade(
                aviso,
                request
        );


        aviso.setViagem(
                viagem

        );

        Aviso salvo =
                avisoRepository.save(aviso);

        return avisoMapper.paraResponse(salvo);
    }

    public AvisoResponse buscarAviso(
            Long id
    ) {

        Aviso aviso =
                avisoRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new AvisoNaoEncontradoException(
                                        "Aviso não encontrado!"
                                )
                        );

        return avisoMapper.paraResponse(aviso);
    }

    public List<AvisoResponse> listarAvisoPorViagem(
            Long viagemId
    ) {

        viagemRepository
                .findById(viagemId
                )
                .orElseThrow(() ->
                        new ViagemNaoEncontradaException(
                                "Viagem não encontrada!"
                        )
                );

        return avisoRepository
                .findByViagemIdOrderByDataPublicacaoDesc(
                        viagemId
                )
                .stream()
                .map(avisoMapper::paraResponse)
                .toList();
    }

    public AvisoResponse atualizarAviso(
            Long id, AvisoRequest request
    ) {

        Aviso aviso =
                avisoRepository
                        .findById(id)
                        .orElseThrow(()->
                                new AvisoNaoEncontradoException(
                                        "Aviso não encontrado!"
                                )
                        );

        Viagem viagem =
                viagemRepository
                        .findById(request.getViagemId())
                        .orElseThrow(()->
                                new ViagemNaoEncontradaException(
                                        "Viagem não encontrada!"
                                )
                        );

        avisoMapper.atualizarEntidade(
                aviso, request
        );

        aviso.setViagem(
                viagem
        );

        Aviso atualizado =
                avisoRepository.save(aviso);

        return avisoMapper.paraResponse(atualizado);
    }

    public void deletarAviso(
            Long id

    ) {

        Aviso aviso =
                avisoRepository
                        .findById(id)
                        .orElseThrow(()->
                                new AvisoNaoEncontradoException(
                                        "Aviso não encontrado!"
                                )
                        );

        avisoRepository.delete(aviso);
    }
}
