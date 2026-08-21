package com.bths.platform.quarto;

import com.bths.platform.exception.QuartoNaoEncontradoException;
import com.bths.platform.exception.ViagemNaoEncontradaException;
import com.bths.platform.quarto.dto.QuartoRequest;
import com.bths.platform.quarto.dto.QuartoResponse;
import com.bths.platform.quarto.mapper.QuartoMapper;
import com.bths.platform.viagem.Viagem;
import com.bths.platform.viagem.ViagemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuartoService {

    private final QuartoRepository quartoRepository;
    private final ViagemRepository viagemRepository;
    private final QuartoMapper quartoMapper;

    public QuartoService(
            QuartoRepository quartoRepository,
            ViagemRepository viagemRepository,
            QuartoMapper quartoMapper
    ) {
        this.quartoRepository = quartoRepository;
        this.viagemRepository = viagemRepository;
        this.quartoMapper = quartoMapper;
    }

    public QuartoResponse cadastrarQuarto(QuartoRequest request) {

        Viagem viagem = viagemRepository.findById(request.getViagemId())
                .orElseThrow(() -> new ViagemNaoEncontradaException(
                                "Viagem não encontrada!"
                        )
                );

        Quarto quarto = new Quarto();

        quartoMapper.atualizarEntidade(quarto, request);

        quarto.setViagem(viagem);

        Quarto quartoSalvo = quartoRepository.save(quarto);

        return quartoMapper.paraResponse(quartoSalvo);
    }

    public QuartoResponse buscarQuartoPorId(Long id) {

        Quarto quarto = quartoRepository.findById(id)
                .orElseThrow(() -> new QuartoNaoEncontradoException(
                                "Quarto não encontrado!"
                        )
                );

        return quartoMapper.paraResponse(quarto);
    }

    public List<QuartoResponse> listarQuartos() {

        return quartoRepository.findAll()
                .stream()
                .map(quartoMapper::paraResponse)
                .toList();

    }

    public QuartoResponse atualizarQuarto(Long id, QuartoRequest request) {

        Quarto quarto = quartoRepository.findById(id)
                .orElseThrow(() -> new QuartoNaoEncontradoException(
                                "Quarto não encontrado!"
                        )
                );

        Viagem viagem = viagemRepository.findById(request.getViagemId())
                .orElseThrow(() -> new ViagemNaoEncontradaException(
                                "Viagem não encontrada!"
                        )
                );
        quartoMapper.atualizarEntidade(quarto, request);

        quarto.setViagem(viagem);

        Quarto quartoAtualizado = quartoRepository.save(quarto);

        return quartoMapper.paraResponse(quartoAtualizado);
    }

    public void deletarQuarto(Long id) {

        Quarto quarto = quartoRepository.findById(id)
                .orElseThrow(() -> new QuartoNaoEncontradoException(
                                "Quarto não encontrado!"
                        )
                );

        quartoRepository.delete(quarto);
    }
}
