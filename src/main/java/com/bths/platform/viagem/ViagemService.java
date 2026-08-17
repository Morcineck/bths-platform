package com.bths.platform.viagem;

import com.bths.platform.viagem.dto.ViagemRequest;
import com.bths.platform.viagem.dto.ViagemResponse;
import org.springframework.stereotype.Service;

@Service
public class ViagemService {

    private final ViagemRepository viagemRepository;

    public ViagemService(ViagemRepository viagemRepository) {
        this.viagemRepository = viagemRepository;
    }


public ViagemResponse cadastrarViagem(ViagemRequest request) {
    if (request.getDataFim().isBefore(request.getDataInicio())) {
        throw new IllegalArgumentException(
                "A data final da viagem não pode ser anterior á data incial."
        );
    }

        Viagem viagem = new Viagem();

        viagem.setNome(request.getNome());
        viagem.setEvento(request.getEvento());
        viagem.setDataInicio(request.getDataInicio());
        viagem.setDataFim(request.getDataFim());
        viagem.setEndereco(request.getEndereco());
        viagem.setCidade(request.getCidade());
        viagem.setEstado(request.getEstado());
        viagem.setStatus(request.getStatus());

        Viagem viagemSalva = viagemRepository.save(viagem);

        ViagemResponse response = new ViagemResponse();

        response.setId(viagemSalva.getId());
        response.setNome(viagemSalva.getNome());
        response.setEvento(viagemSalva.getEvento());
        response.setDataIcinio(viagemSalva.getDataInicio());
        response.setDataFim(viagemSalva.getDataFim());
        response.setEndereco(viagemSalva.getEndereco());
        response.setCidade(viagemSalva.getCidade());
        response.setEstado(viagemSalva.getEstado());
        response.setStatus(viagemSalva.getStatus());

        return response;
    }

}
