package com.bths.platform.dashboard;

import com.bths.platform.alocacao.AlocacaoQuartoRepository;
import com.bths.platform.dashboard.dto.*;
import com.bths.platform.viagem.exception.ViagemNaoEncontradaException;
import com.bths.platform.hospede.HospedeRepository;
import com.bths.platform.hospede.enums.StatusCheckIn;
import com.bths.platform.quarto.QuartoRepository;
import com.bths.platform.quarto.enums.StatusQuarto;
import com.bths.platform.traslado.Traslado;
import com.bths.platform.traslado.TrasladoRepository;
import com.bths.platform.traslado.enums.StatusTraslado;
import com.bths.platform.viagem.ViagemRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DashboardService {

    private final ViagemRepository viagemRepository;
    private final HospedeRepository hospedeRepository;
    private final QuartoRepository quartoRepository;
    private final AlocacaoQuartoRepository alocacaoQuartoRepository;
    private final TrasladoRepository trasladoRepository;

    public DashboardService(
            ViagemRepository viagemRepository,
            HospedeRepository hospedeRepository,
            QuartoRepository quartoRepository,
            AlocacaoQuartoRepository alocacaoQuartoRepository,
            TrasladoRepository trasladoRepository
    ) {

        this.viagemRepository = viagemRepository;
        this.hospedeRepository = hospedeRepository;
        this.quartoRepository = quartoRepository;
        this.alocacaoQuartoRepository = alocacaoQuartoRepository;
        this.trasladoRepository = trasladoRepository;

    }

    public DashboardResponse buscarDashboard(Long viagemId) {

        validarViagem(viagemId);

        DashboardResponse response =
                new DashboardResponse();

        response.setViagemId(viagemId);
        response.setHospedes(
                montarResumoHospedes(viagemId)
        );

        response.setHospedagem(
                montarResumoHospedagem(viagemId)
        );

        response.setTraslados(
                montarResumoTraslado(viagemId)
        );

        return response;
    }

    private void validarViagem(Long viagemId){

        if (!viagemRepository.existsById(viagemId)) {
            throw new ViagemNaoEncontradaException(
                    "Viagem não encontrada!"
            );
        }
    }

    private DashboardHospedesResponse montarResumoHospedes(Long viagemId) {

        long total = hospedeRepository.countByViagemId(viagemId);

        long presentes = hospedeRepository.contarPorViagemEStatusCheckIn(
                viagemId,
                StatusCheckIn.REALIZADO
        );

        long pendentes = hospedeRepository.contarPorViagemEStatusCheckIn(
                viagemId,
                StatusCheckIn.PENDENTE
        );

        double taxaCheckIn = total == 0
                ? 0.0
                : (presentes * 100.0) / total;

        DashboardHospedesResponse response =
                new DashboardHospedesResponse();

        response.setTotal(total);
        response.setPresentes(presentes);
        response.setPendentes(pendentes);
        response.setTaxaCheckIn(taxaCheckIn);

        return response;

    }

    private DashboardHospedagemResponse montarResumoHospedagem(Long viagemId) {

        long vagasTotais =
                quartoRepository.somarCapacidadePorViagemExcluindoStatus(
                        viagemId,
                        StatusQuarto.INDISPONIVEL
                );

        long ocupadas =
                alocacaoQuartoRepository.countByViagemId(viagemId);

        long disponiveis =
                Math.max(vagasTotais - ocupadas, 0);

        DashboardHospedagemResponse response =
                new DashboardHospedagemResponse();

        response.setVagasTotais(vagasTotais);
        response.setOcupadas(ocupadas);
        response.setDisponiveis(disponiveis);

        return response;
    }

    private DashboardTrasladosResponse montarResumoTraslado(Long viagemId) {

        long aguardando = trasladoRepository.countByViagemIdAndStatus(
                viagemId,
                StatusTraslado.AGUARDANDO
        );

        long emAndamento = trasladoRepository.countByViagemIdAndStatus(
                viagemId,
                StatusTraslado.EM_ANDAMENTO
        );

        long concluidos =  trasladoRepository.countByViagemIdAndStatus(
                viagemId,
                StatusTraslado.CONCLUIDO
        );

        List<Traslado> proximosTraslados =
                trasladoRepository.buscarProximosTraslados(
                        viagemId,
                        StatusTraslado.AGUARDANDO,
                        LocalDateTime.now(),
                        PageRequest.of(0, 5)
                );

        List<DashboardProximoTrasladoResponse> proximos =
                proximosTraslados.stream()
                        .map(traslado -> {

                            DashboardProximoTrasladoResponse response =
                                    new DashboardProximoTrasladoResponse();

                            response.setId(traslado.getId());
                            response.setHospedeNome(
                                    traslado.getHospede().getNomeCompleto()
                            );

                            response.setTipo(traslado.getTipo());
                            response.setDataHoraPrevista(
                                    traslado.getDataHoraPrevista()
                            );

                            response.setLocalOrigem(
                                    traslado.getLocalOrigem()
                            );

                            response.setLocalDestino(
                                    traslado.getLocalDestino()
                            );

                            return response;
                        })
                        .toList();

        DashboardTrasladosResponse response =
                new DashboardTrasladosResponse();

        response.setAguardando(aguardando);
        response.setEmAndamento(emAndamento);
        response.setConcluidos(concluidos);
        response.setProximos(proximos);

        return response;
    }



}

