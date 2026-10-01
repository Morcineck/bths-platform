package com.bths.platform.hospedagem;

import com.bths.platform.hospedagem.dto.HospedagemRequest;
import com.bths.platform.hospedagem.dto.HospedagemResponse;
import com.bths.platform.hospedagem.exception.HospedagemNaoEncontradaException;
import com.bths.platform.hospedagem.mapper.HospedagemMapper;
import com.bths.platform.viagem.Viagem;
import com.bths.platform.viagem.ViagemRepository;
import com.bths.platform.viagem.exception.ViagemNaoEncontradaException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HospedagemService {

    private final HospedagemRepository hospedagemRepository;
    private final ViagemRepository viagemRepository;
    private final HospedagemMapper hospedagemMapper;

    public HospedagemService(
            HospedagemRepository hospedagemRepository,
            ViagemRepository viagemRepository,
            HospedagemMapper hospedagemMapper
    ) {
        this.hospedagemRepository = hospedagemRepository;
        this.viagemRepository = viagemRepository;
        this.hospedagemMapper = hospedagemMapper;

    }

    public HospedagemResponse cadastrarHospedagem(
            HospedagemRequest request
    ) {

        Viagem viagem =
                viagemRepository
                        .findById(
                                request.getViagemId()
                        )
                        .orElseThrow(() ->
                                new ViagemNaoEncontradaException(
                                        "Viagem não encontrada"
                                )
                        );

        Hospedagem hospedagem =
                new Hospedagem();

        hospedagemMapper.atualizarEntidade(
                hospedagem,
                request
        );

        hospedagem.setViagem(
                viagem
        );

        Hospedagem hospedagemSalva =
                hospedagemRepository.save(
                        hospedagem
                );

        return hospedagemMapper.paraResponse(
                hospedagemSalva
        );
    }

    public HospedagemResponse buscarHospedagemPorId(
            Long id
    ) {

        Hospedagem hospedagem =
                hospedagemRepository
                        .findById(id)
                        .orElseThrow(()->
                                new HospedagemNaoEncontradaException(
                                        "Hospedagem não encontrada!"
                                )
                        );

        return hospedagemMapper.paraResponse(
                hospedagem
        );
    }

    public List<HospedagemResponse> listarHospedagens() {

        return hospedagemRepository
                .findAll()
                .stream()
                .map(hospedagemMapper::paraResponse)
                .toList();
    }

    public List<HospedagemResponse> listarHospedagensPorViagem(
            Long viagemId
    ) {

        viagemRepository
                .findById(viagemId)
                .orElseThrow(()->
                        new ViagemNaoEncontradaException(
                                "Viagem não encontrada!"
                        )
                );

        return hospedagemRepository
                .findByViagemId(
                        viagemId
                )
                .stream()
                .map(hospedagemMapper::paraResponse)
                .toList();
    }

    public HospedagemResponse atualizarHospedagem(
            Long id,
            HospedagemRequest request
    ) {

        Hospedagem hospedagem =
                hospedagemRepository
                        .findById(id)
                        .orElseThrow(()->
                                new HospedagemNaoEncontradaException(
                                        "Hospedagem não encontrada!"
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

        hospedagemMapper.atualizarEntidade(
                hospedagem,
                request
        );

        hospedagem.setViagem(
                viagem
        );

        Hospedagem hospedagemAtualizada =
                hospedagemRepository.save(
                        hospedagem
                );

        return hospedagemMapper.paraResponse(
                hospedagemAtualizada
        );
    }

    public void deletarHospedagem(
            Long id
    ) {

        Hospedagem hospedagem =
                hospedagemRepository
                        .findById(id)
                        .orElseThrow(()->
                                new HospedagemNaoEncontradaException(
                                        "Hospedagem não encontrada!"
                                )
                        );

        hospedagemRepository.delete(
                hospedagem
        );
    }
}
