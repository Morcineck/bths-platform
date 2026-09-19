package com.bths.platform.viagem;

import com.bths.platform.viagem.exception.ViagemNaoEncontradaException;
import com.bths.platform.viagem.mapper.ViagemMapper;
import com.bths.platform.viagem.dto.ViagemRequest;
import com.bths.platform.viagem.dto.ViagemResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ViagemService {

    private final ViagemRepository viagemRepository;
    private final ViagemMapper viagemMapper;

    public ViagemService(ViagemRepository viagemRepository, ViagemMapper viagemMapper) {
        this.viagemRepository = viagemRepository;
        this.viagemMapper = viagemMapper;
    }


    public ViagemResponse cadastrarViagem(ViagemRequest request) {
        if (request.getDataFim().isBefore(request.getDataInicio())) {
            throw new IllegalArgumentException(
                    "A data final da viagem não pode ser anterior à data inicial."
            );
        }

        Viagem viagem = viagemMapper.paraEntidade(request);

        Viagem viagemSalva = viagemRepository.save(viagem);

        return viagemMapper.paraResponse(viagemSalva);
    }

    public List<ViagemResponse> listarViagens() {

        return viagemRepository.findAll()
                .stream()
                .map(viagemMapper::paraResponse)
                .toList();

    }

    public ViagemResponse buscarViagemPorId(Long id) {

        Viagem viagem = viagemRepository.findById(id)
                .orElseThrow(() ->
                        new ViagemNaoEncontradaException("Viagem não encontrada!")
                );


        return viagemMapper.paraResponse(viagem);
    }

    public ViagemResponse atualizarViagem(Long id, ViagemRequest request) {

        Viagem viagem = viagemRepository.findById(id)
                .orElseThrow(() -> new ViagemNaoEncontradaException("Viagem não encontrada!")
                );
        if (request.getDataFim().isBefore(request.getDataInicio())) {
            throw new IllegalArgumentException(
                    "A data final da viagem não pode ser anterior à data inicial."
            );
        }

        viagemMapper.atualizaEntidade(request, viagem);

        Viagem viagemAtualizada = viagemRepository.save(viagem);

        ViagemResponse response = new ViagemResponse();

       return viagemMapper.paraResponse(viagemAtualizada);

    }

    public void deletarViagem(Long id) {

        Viagem viagem = viagemRepository.findById(id)
                .orElseThrow(() ->
                        new ViagemNaoEncontradaException("Viagem não encontrada!")
                );

        viagemRepository.delete(viagem);
    }

}
