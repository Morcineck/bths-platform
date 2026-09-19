package com.bths.platform.hospede;

import com.bths.platform.hospede.dto.HospedeRequest;
import com.bths.platform.hospede.dto.HospedeResponse;
import com.bths.platform.hospede.mapper.HospedeMapper;
import com.bths.platform.viagem.Viagem;
import com.bths.platform.viagem.ViagemRepository;
import org.springframework.stereotype.Service;
import com.bths.platform.viagem.exception.ViagemNaoEncontradaException;
import com.bths.platform.hospede.exception.HospedeJaCadastradoException;
import com.bths.platform.hospede.exception.HospedeNaoEncontradoException;

import java.util.List;
import java.util.UUID;

@Service
public class HospedeService {

    private final HospedeRepository hospedeRepository;
    private final ViagemRepository viagemRepository;
    private final HospedeMapper hospedeMapper;

    public HospedeService(
            HospedeRepository hospedeRepository,
            ViagemRepository viagemRepository,
            HospedeMapper hospedeMapper
    ) {
        this.hospedeRepository = hospedeRepository;
        this.viagemRepository = viagemRepository;
        this.hospedeMapper = hospedeMapper;



    }

    public HospedeResponse cadastrarHospede(HospedeRequest request) {

        Viagem viagem = viagemRepository.findById(request.getViagemId())
                .orElseThrow(() ->
                        new ViagemNaoEncontradaException("Viagem não encontrada!")
                );
        if (hospedeRepository.existsByCpfAndViagemId(
                request.getCpf(),
                request.getViagemId()
        )) {
            throw new HospedeJaCadastradoException(
                    "CPF já cadastrado nesta viagem!"
            );
        }

        Hospede hospede = new Hospede();
        hospedeMapper.atualizarEntidade(hospede, request);

        hospede.setViagem(viagem);

        hospede.setCodigoCheckIn(
                UUID.randomUUID().toString()
        );

        Hospede hospedeSalvo =
                hospedeRepository.save(hospede);

        return hospedeMapper.paraResponse(hospedeSalvo);
    }

    public HospedeResponse buscarHospedePorId(Long id) {

        Hospede hospede = hospedeRepository.findById(id)
                .orElseThrow(() -> new HospedeNaoEncontradoException(
                                "Hóspede não encontrado!"
                        )
                );
        return hospedeMapper.paraResponse(hospede);
    }

    public List<HospedeResponse> listarHospedes() {

        return hospedeRepository.findAll()
                .stream()
                .map(hospedeMapper::paraResponse)
                .toList();
    }

    public HospedeResponse atualizarHospede(Long id,
                                            HospedeRequest request
    ) {

        Hospede hospede = hospedeRepository.findById(id)
                .orElseThrow(() -> new HospedeNaoEncontradoException(
                                "Hóspede não encontrado!"
                        )
                );

        Viagem viagem = viagemRepository.findById(request.getViagemId())
                .orElseThrow(() ->
                        new ViagemNaoEncontradaException(
                                "Viagem não encontrada!"
                        )
                );

        boolean cpfJaCadastrado =
                hospedeRepository.existsByCpfAndViagemIdAndIdNot(
                        request.getCpf(),
                        request.getViagemId(),
                        id
                );

        if (cpfJaCadastrado) {
            throw new HospedeJaCadastradoException(
                    "CPF já cadastrado nesta viagem!"
            );
        }

        hospedeMapper.atualizarEntidade(hospede, request);

        hospede.setViagem(viagem);

        Hospede hospedeAtualizado = hospedeRepository.save(hospede);

        return hospedeMapper.paraResponse(hospedeAtualizado);
    }

    public void deletarHospede(Long id) {

        Hospede hospede = hospedeRepository.findById(id)
                .orElseThrow(() -> new HospedeNaoEncontradoException(
                                "Hóspede não encontrado!"
                        )
                );
        hospedeRepository.delete(hospede);
    }

}

