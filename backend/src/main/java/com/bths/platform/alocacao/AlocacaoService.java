package com.bths.platform.alocacao;

import com.bths.platform.alocacao.dto.AlocacaoQuartoRequest;
import com.bths.platform.alocacao.dto.AlocacaoQuartoResponse;
import com.bths.platform.alocacao.dto.OcupacaoQuartoResponse;
import com.bths.platform.alocacao.exception.AlocacaoNaoEncontradaException;
import com.bths.platform.alocacao.exception.HospedeJaAlocadoException;
import com.bths.platform.alocacao.exception.ViagemIncompativelException;
import com.bths.platform.alocacao.mapper.AlocacaoQuartoMapper;
import com.bths.platform.hospede.Hospede;
import com.bths.platform.hospede.HospedeRepository;
import com.bths.platform.hospede.exception.HospedeNaoEncontradoException;
import com.bths.platform.quarto.Quarto;
import com.bths.platform.quarto.QuartoRepository;
import com.bths.platform.quarto.exception.QuartoIndisponivelException;
import com.bths.platform.quarto.exception.QuartoLotadoException;
import com.bths.platform.quarto.exception.QuartoNaoEncontradoException;
import org.springframework.stereotype.Service;
import com.bths.platform.quarto.enums.StatusQuarto;

import java.util.List;

@Service
public class AlocacaoService {

    private final AlocacaoQuartoRepository alocacaoRepository;
    private final HospedeRepository hospedeRepository;
    private final QuartoRepository quartoRepository;
    private final AlocacaoQuartoMapper alocacaoMapper;

    public AlocacaoService(
            AlocacaoQuartoRepository alocacaoRepository,
            HospedeRepository hospedeRepository,
            QuartoRepository quartoRepository,
            AlocacaoQuartoMapper alocacaoMapper
    ) {
        this.alocacaoRepository = alocacaoRepository;
        this.hospedeRepository = hospedeRepository;
        this.quartoRepository = quartoRepository;
        this.alocacaoMapper = alocacaoMapper;
    }

    public AlocacaoQuartoResponse alocarHospede(
            AlocacaoQuartoRequest request
    ) {

        Hospede hospede = hospedeRepository
                .findById(request.getHospedeId())
                .orElseThrow(() ->
                        new HospedeNaoEncontradoException(
                                "Hóspede não encontrado!"
                        )
                );

        Quarto quarto = quartoRepository
                .findById(request.getQuartoId())
                .orElseThrow(() ->
                        new QuartoNaoEncontradoException(
                                "Quarto não encontrado!"
                        )
                );

        if (!hospede.getViagem().getId()
                .equals(quarto.getViagem().getId())) {

            throw new ViagemIncompativelException(
                    "Hóspede e quarto pertencem a viagens diferentes!"
            );
        }

        if (quarto.getStatus() == StatusQuarto.INDISPONIVEL) {
            throw new QuartoIndisponivelException(
                    "Quarto indisponível para alocação!"
            );
        }

        Long viagemId = hospede.getViagem().getId();

        boolean hospedeJaAlocado =
                alocacaoRepository.existsByHospedeIdAndViagemId(
                        hospede.getId(),
                        viagemId
                );

        if (hospedeJaAlocado) {
            throw new HospedeJaAlocadoException(
                    "Hóspede já está alocado nesta viagem!"
            );
        }

        long ocupacaoAtual =
                alocacaoRepository.countByQuartoId(
                        quarto.getId()
                );

        if (ocupacaoAtual >= quarto.getCapacidade()) {
            throw new QuartoLotadoException(
                    "Quarto lotado!"
            );
        }

        AlocacaoQuarto alocacao = new AlocacaoQuarto();

        alocacao.setHospede(hospede);
        alocacao.setQuarto(quarto);
        alocacao.setViagem(hospede.getViagem());

        AlocacaoQuarto alocacaoSalva =
                alocacaoRepository.save(alocacao);

        return alocacaoMapper.paraResponse(alocacaoSalva);
    }

    public AlocacaoQuartoResponse buscarAlocacaoPorId(Long id) {

        AlocacaoQuarto alocacao = alocacaoRepository.findById(id)
                .orElseThrow(() -> new AlocacaoNaoEncontradaException(
                                "Alocação não encontrada!"
                        )
                );

        return alocacaoMapper.paraResponse(alocacao);
    }

    public List<AlocacaoQuartoResponse> listarAlocacoes() {

        return alocacaoRepository.findAll()
                .stream()
                .map(alocacaoMapper::paraResponse)
                .toList();
    }

    public List<AlocacaoQuartoResponse> listarAlocacoesPorQuarto(
            Long quartoId
    ) {

        quartoRepository.findById(quartoId)
                .orElseThrow(() -> new QuartoNaoEncontradoException(
                        "Quarto não encontrado!"
                ));

        return alocacaoRepository.findByQuartoId(quartoId)
                .stream()
                .map(alocacaoMapper::paraResponse)
                .toList();
    }

    public AlocacaoQuartoResponse trocarQuarto(
            Long alocacaoId,
            Long novoQuartoId
    ) {

        AlocacaoQuarto alocacao = alocacaoRepository
                .findById(alocacaoId)
                .orElseThrow(() -> new AlocacaoNaoEncontradaException(
                        "Alocação não encontrada!"
                ));

        Quarto novoQuarto = quartoRepository
                .findById(novoQuartoId)
                .orElseThrow(() ->
                        new QuartoNaoEncontradoException(
                                "Quarto não encontrado!"
                        )
                );

        if (!alocacao.getViagem().getId().equals(novoQuarto.getViagem().getId())) {
            throw new ViagemIncompativelException(
                    "Hóspede e quarto pertencem a viagens diferentes!"
            );
        }

        if (novoQuarto.getStatus() == StatusQuarto.INDISPONIVEL) {
            throw new QuartoIndisponivelException(
                    "Quarto indisponível para alocação!"
            );
        }

        long ocupacaoAtual = alocacaoRepository.countByQuartoId(novoQuarto.getId());

        if (ocupacaoAtual >= novoQuarto.getCapacidade()) {
            throw new QuartoLotadoException(
                    "Quarto lotado!"
            );
        }

        alocacao.setQuarto(novoQuarto);

        AlocacaoQuarto alocacaoAtualizada = alocacaoRepository.save(alocacao);

        return alocacaoMapper.paraResponse(alocacaoAtualizada);
    }

    public void removerAlocacao(Long id) {

        AlocacaoQuarto alocacao = alocacaoRepository
                .findById(id)
                .orElseThrow(() -> new AlocacaoNaoEncontradaException(
                        "Alocação não encontrada!"
                ));

        alocacaoRepository.delete(alocacao);
    }

    public OcupacaoQuartoResponse buscarOcupacaoPorQuarto(
            Long quartoId
    ) {

        Quarto quarto = quartoRepository.findById(quartoId)
                .orElseThrow(() -> new QuartoNaoEncontradoException(
                        "Quarto não encontrado!"
                ));

        Long ocupacao = alocacaoRepository.countByQuartoId(quartoId);

        Long vagasDiponiveis = quarto.getCapacidade() - ocupacao;

        OcupacaoQuartoResponse response = new OcupacaoQuartoResponse();

        response.setQuartoId(quarto.getId());
        response.setQuartoNome(quarto.getNome());
        response.setCapacidade(quarto.getCapacidade());
        response.setOcupacao(ocupacao);
        response.setVagasDisponiveis(vagasDiponiveis);

        return response;
    }
}