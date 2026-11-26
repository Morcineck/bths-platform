
package com.bths.platform.checkin;

import com.bths.platform.alocacao.AlocacaoQuartoRepository;
import com.bths.platform.alocacao.AlocacaoService;
import com.bths.platform.alocacao.dto.AlocacaoQuartoRequest;
import com.bths.platform.alocacao.dto.AlocacaoQuartoResponse;
import com.bths.platform.alocacao.enums.TipoCama;
import com.bths.platform.alocacao.exception.HospedeJaAlocadoException;
import com.bths.platform.alocacao.exception.ViagemIncompativelException;
import com.bths.platform.checkin.dto.CheckInRequest;
import com.bths.platform.checkin.dto.CheckInResponse;
import com.bths.platform.checkin.exception.CheckInJaRealizadoException;
import com.bths.platform.checkin.exception.HospedeSemAlocacaoException;
import com.bths.platform.hospede.Hospede;
import com.bths.platform.hospede.HospedeRepository;
import com.bths.platform.hospede.enums.StatusCheckIn;
import com.bths.platform.quarto.Quarto;
import com.bths.platform.quarto.QuartoRepository;
import com.bths.platform.quarto.enums.StatusQuarto;
import com.bths.platform.quarto.enums.TipoQuarto;
import com.bths.platform.quarto.exception.QuartoIndisponivelException;
import com.bths.platform.quarto.exception.QuartoLotadoException;
import com.bths.platform.usuario.Usuario;
import com.bths.platform.usuario.UsuarioRepository;
import com.bths.platform.usuario.enums.PerfilUsuario;
import com.bths.platform.viagem.Viagem;
import com.bths.platform.viagem.ViagemRepository;
import com.bths.platform.viagem.enums.StatusViagem;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class HospedeQuartoCheckInIntegrationTest {

    @Autowired
    private ViagemRepository viagemRepository;

    @Autowired
    private HospedeRepository hospedeRepository;

    @Autowired
    private QuartoRepository quartoRepository;

    @Autowired
    private AlocacaoQuartoRepository alocacaoQuartoRepository;

    @Autowired
    private AlocacaoService alocacaoService;

    @Autowired
    private CheckInService checkInService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // ==================================================
    // 1. CHECK-IN COMPLETO E CHECK-IN DUPLICADO
    // ==================================================

    @Test
    void deveRealizarCheckInEImpedirCheckInDuplicado() {

        Viagem viagem = criarViagem("Viagem Integracao BTHS");

        Quarto quarto = criarQuarto(
                viagem,
                "Suite Integracao",
                2
        );

        Hospede hospede = criarHospede(
                viagem,
                "Hospede Integracao BTHS",
                "98765432100"
        );

        assertEquals(viagem.getId(), quarto.getViagem().getId());
        assertEquals(viagem.getId(), hospede.getViagem().getId());
        assertEquals(StatusCheckIn.PENDENTE, hospede.getStatusCheckIn());

        AlocacaoQuartoResponse alocacao =
                alocarHospede(hospede, quarto);

        assertNotNull(alocacao);

        assertTrue(
                alocacaoQuartoRepository.existsByHospedeIdAndViagemId(
                        hospede.getId(),
                        viagem.getId()
                )
        );

        assertEquals(
                1L,
                alocacaoQuartoRepository.countByQuartoId(quarto.getId())
        );

        Usuario operador = criarOperador();
        Authentication authentication = criarAuthentication(operador);

        CheckInRequest request = new CheckInRequest();
        request.setObservacao("Check-in realizado no teste de integracao");

        CheckInResponse response = checkInService.realizarCheckIn(
                hospede.getId(),
                request,
                authentication
        );

        assertNotNull(response);
        assertEquals(StatusCheckIn.REALIZADO, response.getStatusCheckIn());
        assertEquals(operador.getNome(), response.getResponsavel());

        Hospede atualizado = hospedeRepository
                .findById(hospede.getId())
                .orElseThrow();

        assertEquals(StatusCheckIn.REALIZADO, atualizado.getStatusCheckIn());
        assertNotNull(atualizado.getDataHoraCheckIn());
        assertEquals(operador.getNome(), atualizado.getResponsavelCheckIn());
        assertEquals(
                "Check-in realizado no teste de integracao",
                atualizado.getObservacaoCheckIn()
        );

        assertThrows(
                CheckInJaRealizadoException.class,
                () -> checkInService.realizarCheckIn(
                        atualizado.getId(),
                        request,
                        authentication
                )
        );

        Hospede aposTentativa = hospedeRepository
                .findById(atualizado.getId())
                .orElseThrow();

        assertEquals(
                StatusCheckIn.REALIZADO,
                aposTentativa.getStatusCheckIn()
        );
    }

    // ==================================================
    // 2. CHECK-IN SEM ALOCACAO
    // ==================================================

    @Test
    void deveImpedirCheckInDeHospedeSemAlocacao() {

        Viagem viagem = criarViagem("Viagem Sem Alocacao");

        Hospede hospede = criarHospede(
                viagem,
                "Hospede Sem Alocacao",
                "12345678909"
        );

        Long hospedeId = hospede.getId();

        CheckInRequest request = new CheckInRequest();
        request.setObservacao("Tentativa sem quarto");

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        "operador@beattrips.test",
                        null
                );

        assertThrows(
                HospedeSemAlocacaoException.class,
                () -> checkInService.realizarCheckIn(
                        hospedeId,
                        request,
                        authentication
                )
        );

        Hospede atualizado = hospedeRepository
                .findById(hospedeId)
                .orElseThrow();

        assertEquals(
                StatusCheckIn.PENDENTE,
                atualizado.getStatusCheckIn()
        );

        assertNull(atualizado.getDataHoraCheckIn());
    }

    // ==================================================
    // 3. ALOCACAO DUPLICADA
    // ==================================================

    @Test
    void deveImpedirAlocacaoDuplicadaNaMesmaViagem() {

        Viagem viagem = criarViagem("Viagem Alocacao Duplicada");

        Quarto quarto = criarQuarto(
                viagem,
                "Suite Alocacao Duplicada",
                2
        );

        Hospede hospede = criarHospede(
                viagem,
                "Hospede Alocacao Duplicada",
                "11122233344"
        );

        AlocacaoQuartoResponse primeiraAlocacao =
                alocarHospede(hospede, quarto);

        assertNotNull(primeiraAlocacao);

        AlocacaoQuartoRequest request = new AlocacaoQuartoRequest();
        request.setHospedeId(hospede.getId());
        request.setQuartoId(quarto.getId());
        request.setTipoCama(TipoCama.CASAL);

        assertThrows(
                HospedeJaAlocadoException.class,
                () -> alocacaoService.alocarHospede(request)
        );

        assertTrue(
                alocacaoQuartoRepository.existsByHospedeIdAndViagemId(
                        hospede.getId(),
                        viagem.getId()
                )
        );

        assertEquals(
                1L,
                alocacaoQuartoRepository.countByQuartoId(quarto.getId())
        );
    }

    // ==================================================
    // 4. QUARTO LOTADO
    // ==================================================

    @Test
    void deveImpedirAlocacaoEmQuartoLotado() {

        Viagem viagem = criarViagem("Viagem Quarto Lotado");

        Quarto quarto = criarQuarto(
                viagem,
                "Suite Capacidade Um",
                1
        );

        Hospede primeiroHospede = criarHospede(
                viagem,
                "Primeiro Hospede",
                "22233344455"
        );

        Hospede segundoHospede = criarHospede(
                viagem,
                "Segundo Hospede",
                "33344455566"
        );

        AlocacaoQuartoResponse primeiraAlocacao =
                alocarHospede(primeiroHospede, quarto);

        assertNotNull(primeiraAlocacao);

        assertEquals(
                1L,
                alocacaoQuartoRepository.countByQuartoId(quarto.getId())
        );

        assertThrows(
                QuartoLotadoException.class,
                () -> alocarHospede(segundoHospede, quarto)
        );

        assertEquals(
                1L,
                alocacaoQuartoRepository.countByQuartoId(quarto.getId())
        );

        assertFalse(
                alocacaoQuartoRepository.existsByHospedeIdAndViagemId(
                        segundoHospede.getId(),
                        viagem.getId()
                )
        );
    }

    // ==================================================
    // 5. QUARTO INDISPONIVEL
    // ==================================================

    @Test
    void deveImpedirAlocacaoEmQuartoIndisponivel() {

        Viagem viagem = criarViagem("Viagem Quarto Indisponivel");

        Quarto quarto = criarQuarto(
                viagem,
                "Suite Indisponivel",
                2
        );

        quarto.setStatus(StatusQuarto.INDISPONIVEL);
        quartoRepository.save(quarto);

        Hospede hospede = criarHospede(
                viagem,
                "Hospede Quarto Indisponivel",
                "44455566677"
        );

        assertThrows(
                QuartoIndisponivelException.class,
                () -> alocarHospede(hospede, quarto)
        );

        assertEquals(
                0L,
                alocacaoQuartoRepository.countByQuartoId(quarto.getId())
        );

        assertFalse(
                alocacaoQuartoRepository.existsByHospedeIdAndViagemId(
                        hospede.getId(),
                        viagem.getId()
                )
        );
    }

    // ==================================================
    // 6. VIAGENS INCOMPATIVEIS
    // ==================================================

    @Test
    void deveImpedirAlocacaoEntreViagensDiferentes() {

        Viagem viagemHospede = criarViagem(
                "Viagem do Hospede"
        );

        Viagem viagemQuarto = criarViagem(
                "Viagem do Quarto"
        );

        Hospede hospede = criarHospede(
                viagemHospede,
                "Hospede Viagem Incompativel",
                "55566677788"
        );

        Quarto quarto = criarQuarto(
                viagemQuarto,
                "Suite Outra Viagem",
                2
        );

        assertNotEquals(
                viagemHospede.getId(),
                viagemQuarto.getId()
        );

        assertThrows(
                ViagemIncompativelException.class,
                () -> alocarHospede(hospede, quarto)
        );

        assertFalse(
                alocacaoQuartoRepository.existsByHospedeIdAndViagemId(
                        hospede.getId(),
                        viagemHospede.getId()
                )
        );

        assertEquals(
                0L,
                alocacaoQuartoRepository.countByQuartoId(quarto.getId())
        );
    }

    // ==================================================
    // METODOS AUXILIARES
    // ==================================================

    private Viagem criarViagem(String nome) {

        Viagem viagem = new Viagem();

        viagem.setNome(nome);
        viagem.setEvento("Evento Teste");
        viagem.setDataInicio(LocalDate.of(2027, 4, 30));
        viagem.setDataFim(LocalDate.of(2027, 5, 2));
        viagem.setStatus(StatusViagem.PLANEJADA);

        return viagemRepository.save(viagem);
    }

    private Quarto criarQuarto(
            Viagem viagem,
            String nome,
            int capacidade
    ) {

        Quarto quarto = new Quarto();

        quarto.setNome(nome);
        quarto.setTipo(TipoQuarto.SUITE);
        quarto.setCapacidade(capacidade);
        quarto.setStatus(StatusQuarto.DISPONIVEL);
        quarto.setViagem(viagem);

        return quartoRepository.save(quarto);
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

    private AlocacaoQuartoResponse alocarHospede(
            Hospede hospede,
            Quarto quarto
    ) {

        AlocacaoQuartoRequest request = new AlocacaoQuartoRequest();

        request.setHospedeId(hospede.getId());
        request.setQuartoId(quarto.getId());
        request.setTipoCama(TipoCama.CASAL);

        return alocacaoService.alocarHospede(request);
    }

    private Usuario criarOperador() {

        Usuario operador = new Usuario();

        operador.setNome("Operador Integracao");
        operador.setEmail("operador.checkin@beattrips.test");
        operador.setSenha("senha-exclusiva-de-teste");
        operador.setPerfil(PerfilUsuario.STAFF);
        operador.setAtivo(true);

        return usuarioRepository.save(operador);
    }

    private Authentication criarAuthentication(Usuario usuario) {

        return new UsernamePasswordAuthenticationToken(
                usuario.getEmail(),
                null
        );
    }
}
