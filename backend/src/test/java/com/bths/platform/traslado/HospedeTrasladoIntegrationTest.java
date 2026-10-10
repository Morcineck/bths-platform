package com.bths.platform.traslado;

import com.bths.platform.alocacao.exception.ViagemIncompativelException;
import com.bths.platform.hospede.Hospede;
import com.bths.platform.hospede.HospedeRepository;
import com.bths.platform.hospede.enums.StatusCheckIn;
import com.bths.platform.motorista.Motorista;
import com.bths.platform.motorista.MotoristaRepository;
import com.bths.platform.traslado.dto.TrasladoOperacaoRequest;
import com.bths.platform.traslado.dto.TrasladoRequest;
import com.bths.platform.traslado.dto.TrasladoResponse;
import com.bths.platform.traslado.dto.TrasladoStatusRequest;
import com.bths.platform.traslado.enums.StatusTraslado;
import com.bths.platform.traslado.enums.TipoTraslado;
import com.bths.platform.traslado.exception.MotoristaInativoException;
import com.bths.platform.traslado.exception.TransicaoStatusTrasladoInvalidaException;
import com.bths.platform.traslado.exception.VeiculoInativoException;
import com.bths.platform.veiculo.Veiculo;
import com.bths.platform.veiculo.VeiculoRepository;
import com.bths.platform.viagem.Viagem;
import com.bths.platform.viagem.ViagemRepository;
import com.bths.platform.viagem.enums.StatusViagem;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class HospedeTrasladoIntegrationTest {

    @Autowired
    private ViagemRepository viagemRepository;

    @Autowired
    private HospedeRepository hospedeRepository;

    @Autowired
    private TrasladoRepository trasladoRepository;

    @Autowired
    private TrasladoService trasladoService;

    @Autowired
    private MotoristaRepository motoristaRepository;

    @Autowired
    private VeiculoRepository veiculoRepository;

    @Autowired
    private HistoricoStatusTrasladoRepository historicoRepository;

    @Test
    void deveCadastrarTrasladoParaHospedeDaViagem() {

        Viagem viagem = criarViagem("Viagem Traslado");

        Hospede hospede = criarHospede(
                viagem,
                "Hospede Traslado",
                "66677788899"
        );

        TrasladoRequest request = criarRequest(
                hospede.getId(),
                viagem.getId()
        );

        TrasladoResponse response =
                trasladoService.cadastrarTraslado(request);

        assertNotNull(response.getId());
        assertEquals(hospede.getId(), response.getHospedeId());
        assertEquals(viagem.getId(), response.getViagemId());
        assertEquals(StatusTraslado.AGUARDANDO, response.getStatus());

        Traslado salvo = trasladoRepository.findById(
                response.getId()
        ).orElseThrow();

        assertEquals(hospede.getId(), salvo.getHospede().getId());
        assertEquals(viagem.getId(), salvo.getViagem().getId());
        assertEquals(StatusTraslado.AGUARDANDO, salvo.getStatus());
    }

    @Test
    void deveImpedirTrasladoComViagemIncompativel() {

        Viagem viagemHospede = criarViagem("Viagem Hospede");
        Viagem outraViagem = criarViagem("Outra Viagem");

        Hospede hospede = criarHospede(
                viagemHospede,
                "Hospede Outra Viagem",
                "77788899900"
        );

        TrasladoRequest request = criarRequest(
                hospede.getId(),
                outraViagem.getId()
        );

        assertThrows(
                ViagemIncompativelException.class,
                () -> trasladoService.cadastrarTraslado(request)
        );

        assertTrue(
                trasladoRepository.findByHospedeId(hospede.getId())
                        .isEmpty()
        );
    }

    @Test
    void deveConsultarTrasladoPorHospedeEViagem() {

        Viagem viagem = criarViagem("Viagem Consulta Traslado");

        Hospede hospede = criarHospede(
                viagem,
                "Hospede Consulta Traslado",
                "88899900011"
        );

        TrasladoResponse criado =
                trasladoService.cadastrarTraslado(
                        criarRequest(hospede.getId(), viagem.getId())
                );

        assertEquals(
                1,
                trasladoService.listarTrasladosPorHospede(
                        hospede.getId()
                ).size()
        );

        assertEquals(
                1,
                trasladoService.listarTrasladosPorViagem(
                        viagem.getId()
                ).size()
        );

        assertEquals(
                criado.getId(),
                trasladoRepository.findByHospedeId(hospede.getId())
                        .get(0)
                        .getId()
        );
    }

    // Métodos auxiliares

    private Viagem criarViagem(String nome) {

        Viagem viagem = new Viagem();
        viagem.setNome(nome);
        viagem.setEvento("Evento Teste");
        viagem.setDataInicio(LocalDate.of(2027, 4, 30));
        viagem.setDataFim(LocalDate.of(2027, 5, 2));
        viagem.setStatus(StatusViagem.PLANEJADA);

        return viagemRepository.save(viagem);
    }

    private Hospede criarHospede(
            Viagem viagem,
            String nome,
            String cpf
    ) {

        Hospede hospede = new Hospede();
        hospede.setNomeCompleto(nome);
        hospede.setCpf(cpf);
        hospede.setViagem(viagem);
        hospede.setStatusCheckIn(StatusCheckIn.PENDENTE);

        return hospedeRepository.save(hospede);
    }

    private TrasladoRequest criarRequest(
            Long hospedeId,
            Long viagemId
    ) {

        TrasladoRequest request = new TrasladoRequest();

        request.setHospedeId(hospedeId);
        request.setViagemId(viagemId);
        request.setTipo(TipoTraslado.OUTRO);
        request.setDataHoraPrevista(
                LocalDateTime.of(2027, 4, 29, 14, 0)
        );
        request.setLocalOrigem("Ponto de encontro");
        request.setLocalDestino("Hospedagem Beat Trips");
        request.setObservacoes("Traslado de integracao");

        return request;
    }


    @Test
    void deveAssociarMotoristaEVeiculoAtivosAoTraslado() {

        Traslado traslado = prepararTraslado("90123456780");

        Motorista motorista = criarMotorista(true);
        Veiculo veiculo = criarVeiculo(true);

        TrasladoOperacaoRequest request =
                criarOperacaoRequest(motorista, veiculo);

        TrasladoResponse response = trasladoService.associarOperacao(
                traslado.getId(),
                request
        );

        assertEquals(motorista.getId(), response.getMotoristaId());
        assertEquals(veiculo.getId(), response.getVeiculoId());

        Traslado salvo = trasladoRepository
                .findById(traslado.getId())
                .orElseThrow();

        assertEquals(motorista.getId(), salvo.getMotorista().getId());
        assertEquals(veiculo.getId(), salvo.getVeiculo().getId());
    }

    @Test
    void deveImpedirMotoristaInativoNoTraslado() {

        Traslado traslado = prepararTraslado("90123456781");

        Motorista motorista = criarMotorista(false);
        Veiculo veiculo = criarVeiculo(true);

        TrasladoOperacaoRequest request =
                criarOperacaoRequest(motorista, veiculo);

        assertThrows(
                MotoristaInativoException.class,
                () -> trasladoService.associarOperacao(
                        traslado.getId(),
                        request
                )
        );

        Traslado salvo = trasladoRepository
                .findById(traslado.getId())
                .orElseThrow();

        assertNull(salvo.getMotorista());
        assertNull(salvo.getVeiculo());
    }

    @Test
    void deveImpedirVeiculoInativoNoTraslado() {

        Traslado traslado = prepararTraslado("90123456782");

        Motorista motorista = criarMotorista(true);
        Veiculo veiculo = criarVeiculo(false);

        TrasladoOperacaoRequest request =
                criarOperacaoRequest(motorista, veiculo);

        assertThrows(
                VeiculoInativoException.class,
                () -> trasladoService.associarOperacao(
                        traslado.getId(),
                        request
                )
        );

        Traslado salvo = trasladoRepository
                .findById(traslado.getId())
                .orElseThrow();

        assertNull(salvo.getMotorista());
        assertNull(salvo.getVeiculo());
    }

// Métodos auxiliares

    private Traslado prepararTraslado(String cpf) {

        Viagem viagem = criarViagem("Viagem Motorista Veiculo");

        Hospede hospede = criarHospede(
                viagem,
                "Hospede Transporte",
                cpf
        );

        TrasladoResponse response = trasladoService.cadastrarTraslado(
                criarRequest(hospede.getId(), viagem.getId())
        );

        return trasladoRepository.findById(response.getId())
                .orElseThrow();
    }

    private Motorista criarMotorista(boolean ativo) {

        Motorista motorista = new Motorista();

        motorista.setNomeCompleto("Motorista Integracao");
        motorista.setTelefone("11999999999");
        motorista.setAtivo(ativo);

        return motoristaRepository.save(motorista);
    }

    private Veiculo criarVeiculo(boolean ativo) {

        Veiculo veiculo = new Veiculo();

        veiculo.setModelo("Van Integracao");
        veiculo.setPlaca("TST1A23");
        veiculo.setCapacidadePassageiros(18);
        veiculo.setAtivo(ativo);

        return veiculoRepository.save(veiculo);
    }

    private TrasladoOperacaoRequest criarOperacaoRequest(
            Motorista motorista,
            Veiculo veiculo
    ) {

        TrasladoOperacaoRequest request =
                new TrasladoOperacaoRequest();

        request.setMotoristaId(motorista.getId());
        request.setVeiculoId(veiculo.getId());

        return request;
    }


    @Test
    void deveConcluirTrasladoERregistrarHistorico() {

        Traslado traslado = prepararTraslado("90123456783");
        Long trasladoId = traslado.getId();

        assertEquals(StatusTraslado.AGUARDANDO, traslado.getStatus());

        TrasladoStatusRequest iniciar = new TrasladoStatusRequest();
        iniciar.setStatus(StatusTraslado.EM_ANDAMENTO);

        TrasladoResponse emAndamento =
                trasladoService.atualizarStatusTraslado(
                        trasladoId,
                        iniciar
                );

        assertEquals(
                StatusTraslado.EM_ANDAMENTO,
                emAndamento.getStatus()
        );

        TrasladoStatusRequest concluir = new TrasladoStatusRequest();
        concluir.setStatus(StatusTraslado.CONCLUIDO);

        TrasladoResponse concluido =
                trasladoService.atualizarStatusTraslado(
                        trasladoId,
                        concluir
                );

        assertEquals(
                StatusTraslado.CONCLUIDO,
                concluido.getStatus()
        );

        Traslado persistido = trasladoRepository
                .findById(trasladoId)
                .orElseThrow();

        assertEquals(
                StatusTraslado.CONCLUIDO,
                persistido.getStatus()
        );

        var historico = historicoRepository.findByTrasladoId(trasladoId);

        assertEquals(2, historico.size());

        assertTrue(historico.stream().anyMatch(item ->
                item.getStatusAnterior() == StatusTraslado.AGUARDANDO
                        && item.getNovoStatus() == StatusTraslado.EM_ANDAMENTO
        ));

        assertTrue(historico.stream().anyMatch(item ->
                item.getStatusAnterior() == StatusTraslado.EM_ANDAMENTO
                        && item.getNovoStatus() == StatusTraslado.CONCLUIDO
        ));

        assertTrue(historico.stream().allMatch(item ->
                item.getDataHora() != null
                        && item.getMotivo() != null
                        && !item.getMotivo().isBlank()
        ));
    }

    @Test
    void deveImpedirConclusaoDiretaDeTrasladoAguardando() {

        Traslado traslado = prepararTraslado("90123456784");
        Long trasladoId = traslado.getId();

        TrasladoStatusRequest request = new TrasladoStatusRequest();
        request.setStatus(StatusTraslado.CONCLUIDO);

        assertThrows(
                TransicaoStatusTrasladoInvalidaException.class,
                () -> trasladoService.atualizarStatusTraslado(
                        trasladoId,
                        request
                )
        );

        assertEquals(
                StatusTraslado.AGUARDANDO,
                trasladoRepository.findById(trasladoId)
                        .orElseThrow()
                        .getStatus()
        );

        assertTrue(
                historicoRepository.findByTrasladoId(trasladoId).isEmpty()
        );
    }

    @Test
    void deveImpedirAlteracaoDeTrasladoCancelado() {

        Traslado traslado = prepararTraslado("90123456785");
        Long trasladoId = traslado.getId();

        TrasladoStatusRequest cancelar = new TrasladoStatusRequest();
        cancelar.setStatus(StatusTraslado.CANCELADO);

        trasladoService.atualizarStatusTraslado(trasladoId, cancelar);

        TrasladoStatusRequest reabrir = new TrasladoStatusRequest();
        reabrir.setStatus(StatusTraslado.EM_ANDAMENTO);

        assertThrows(
                TransicaoStatusTrasladoInvalidaException.class,
                () -> trasladoService.atualizarStatusTraslado(
                        trasladoId,
                        reabrir
                )
        );

        assertEquals(
                StatusTraslado.CANCELADO,
                trasladoRepository.findById(trasladoId)
                        .orElseThrow()
                        .getStatus()
        );

        assertEquals(
                1,
                historicoRepository.findByTrasladoId(trasladoId).size()
        );
    }

}
