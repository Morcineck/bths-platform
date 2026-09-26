package com.bths.platform.quarto;

import com.bths.platform.alocacao.AlocacaoQuartoRepository;
import com.bths.platform.quarto.dto.QuartoOcupacaoResponse;
import com.bths.platform.quarto.exception.QuartoNaoEncontradoException;
import com.bths.platform.viagem.exception.ViagemNaoEncontradaException;
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
    private final AlocacaoQuartoRepository alocacaoQuartoRepository;

    public QuartoService(
            QuartoRepository quartoRepository,
            ViagemRepository viagemRepository,
            QuartoMapper quartoMapper,
            AlocacaoQuartoRepository alocacaoQuartoRepository
    ) {
        this.quartoRepository = quartoRepository;
        this.viagemRepository = viagemRepository;
        this.quartoMapper = quartoMapper;
        this.alocacaoQuartoRepository = alocacaoQuartoRepository;
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

    public List<QuartoOcupacaoResponse> listarOcupacaoQuartosPorViagem(
            Long viagemId
    ) {

        viagemRepository.findById(
                viagemId
        ).orElseThrow(() -> new ViagemNaoEncontradaException(
                        "Viagem não encontrada!"
                )
        );

        return quartoRepository
                .findByViagemId(
                        viagemId
                )
                .stream()
                .map(
                        quarto -> {
                            long ocupacao =
                                    alocacaoQuartoRepository
                                            .countByQuartoId(
                                                    quarto.getId()
                                            );

                            long vagasDisponiveis =
                                    quarto.getCapacidade()
                                            - ocupacao;

                            QuartoOcupacaoResponse response =
                                    new QuartoOcupacaoResponse();

                            response.setQuartoId(
                                    quarto.getId()
                            );

                            response.setNome(
                                    quarto.getNome()
                            );

                            response.setTipo(
                                    quarto.getTipo()
                            );

                            response.setStatus(
                                    quarto.getStatus()
                            );

                            response.setCapacidade(
                                    quarto.getCapacidade()
                            );

                            response.setOcupacao(
                                    ocupacao
                            );

                            response.setVagasDisponiveis(
                                    vagasDisponiveis
                            );

                            return response;
                        }
                )
                .toList();
    }

    public List<QuartoResponse> listarQuartosPorViagem(
            Long viagemId

    ) {

        viagemRepository.findById(viagemId)
                .orElseThrow(() ->
                        new ViagemNaoEncontradaException(
                                "Viagem não encontrada!"
                        )
                );


        return quartoRepository.findByViagemId(viagemId)
                .stream()
                .map(quartoMapper::paraResponse)
                .toList();

    }
}
