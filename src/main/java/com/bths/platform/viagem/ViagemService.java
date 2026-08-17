package com.bths.platform.viagem;

import com.bths.platform.exception.ViagemNaoEncontradaException;
import com.bths.platform.viagem.dto.ViagemRequest;
import com.bths.platform.viagem.dto.ViagemResponse;
import org.springframework.stereotype.Service;

import java.util.List;

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
        response.setDataInicio(viagemSalva.getDataInicio());
        response.setDataFim(viagemSalva.getDataFim());
        response.setEndereco(viagemSalva.getEndereco());
        response.setCidade(viagemSalva.getCidade());
        response.setEstado(viagemSalva.getEstado());
        response.setStatus(viagemSalva.getStatus());

        return response;
    }

    public List<ViagemResponse> listarViagens() {

        List<Viagem> viagens = viagemRepository.findAll();

        return viagens.stream()
                .map(viagem -> {
                    ViagemResponse response = new ViagemResponse();

                    response.setId(viagem.getId());
                    response.setNome(viagem.getNome());
                    response.setEvento(viagem.getEvento());
                    response.setDataInicio(viagem.getDataInicio());
                    response.setDataFim(viagem.getDataFim());
                    response.setEndereco(viagem.getEndereco());
                    response.setCidade(viagem.getCidade());
                    response.setEstado(viagem.getEstado());
                    response.setStatus(viagem.getStatus());

                    return response;
                })
                .toList();

    }

    public ViagemResponse buscarViagemPorId(Long id) {

        Viagem viagem = viagemRepository.findById(id)
                .orElseThrow(() -> new ViagemNaoEncontradaException("Viagem não encontrada!"));

        ViagemResponse response = new ViagemResponse();

        response.setId(viagem.getId());
        response.setNome(viagem.getNome());
        response.setEvento(viagem.getEvento());
        response.setDataInicio(viagem.getDataInicio());
        response.setDataFim(viagem.getDataFim());
        response.setEndereco(viagem.getEndereco());
        response.setCidade(viagem.getCidade());
        response.setEstado(viagem.getEstado());
        response.setStatus(viagem.getStatus());

        return response;
    }

    public ViagemResponse atualizarViagem(Long id, ViagemRequest request) {

        Viagem viagem = viagemRepository.findById(id)
                .orElseThrow(() -> new ViagemNaoEncontradaException("Viagem não encontrada!")
                );
        if (request.getDataFim().isBefore(request.getDataInicio())) {
            throw new IllegalArgumentException(
                    "A data final da viagem não pode ser anteior á data incial."
            );
        }

        viagem.setNome(request.getNome());
        viagem.setEvento(request.getEvento());
        viagem.setDataInicio(request.getDataInicio());
        viagem.setDataFim(request.getDataFim());
        viagem.setEndereco(request.getEndereco());
        viagem.setCidade(request.getCidade());
        viagem.setEstado(request.getEstado());
        viagem.setStatus(request.getStatus());

        Viagem viagemAtualizada = viagemRepository.save(viagem);

        ViagemResponse response = new ViagemResponse();

        response.setId(viagemAtualizada.getId());
        response.setNome(viagemAtualizada.getNome());
        response.setEvento(viagemAtualizada.getEvento());
        response.setDataInicio(viagemAtualizada.getDataInicio());
        response.setDataFim(viagemAtualizada.getDataFim());
        response.setEndereco(viagemAtualizada.getEndereco());
        response.setCidade(viagemAtualizada.getCidade());
        response.setEstado(viagemAtualizada.getEstado());
        response.setStatus(viagemAtualizada.getStatus());

        return response;

    }

    public void deletarViagem(Long id) {

        Viagem viagem = viagemRepository.findById(id)
                .orElseThrow(() ->
                        new ViagemNaoEncontradaException("Viagem não encontrada!")
                );

        viagemRepository.delete(viagem);
    }


}
