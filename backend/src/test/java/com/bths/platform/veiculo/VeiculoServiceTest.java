package com.bths.platform.veiculo;

import com.bths.platform.veiculo.dto.VeiculoRequest;
import com.bths.platform.veiculo.dto.VeiculoResponse;
import com.bths.platform.veiculo.exception.VeiculoJaCadastradoException;
import com.bths.platform.veiculo.exception.VeiculoNaoEncontradoException;
import com.bths.platform.veiculo.mapper.VeiculoMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VeiculoServiceTest {

    @Mock
    private VeiculoRepository veiculoRepository;

    @Mock
    private VeiculoMapper veiculoMapper;

    @InjectMocks
    private VeiculoService veiculoService;

    @Test
    void deveCadastrarVeiculoComSucesso() {

        VeiculoRequest request = new VeiculoRequest();
        request.setModelo("Renault Duster");
        request.setPlaca("ABC1D23");
        request.setCapacidadePassageiros(4);
        request.setObservacao("Veículo executivo");

        Veiculo veiculo = new Veiculo();
        veiculo.setModelo("Renault Duster");
        veiculo.setPlaca("ABC1D23");
        veiculo.setCapacidadePassageiros(4);
        veiculo.setObservacao("Veículo executivo");
        veiculo.setAtivo(true);

        Veiculo veiculoSalvo = new Veiculo();
        veiculoSalvo.setId(1L);
        veiculoSalvo.setModelo("Renault Duster");
        veiculoSalvo.setPlaca("ABC1D23");
        veiculoSalvo.setCapacidadePassageiros(4);
        veiculoSalvo.setObservacao("Veículo executivo");
        veiculoSalvo.setAtivo(true);

        VeiculoResponse responseEsperado = new VeiculoResponse();
        responseEsperado.setId(1L);
        responseEsperado.setModelo("Renault Duster");
        responseEsperado.setPlaca("ABC1D23");
        responseEsperado.setCapacidadePassageiros(4);
        responseEsperado.setObservacao("Veículo executivo");
        responseEsperado.setAtivo(true);

        when(veiculoRepository.existsByPlaca("ABC1D23"))
                .thenReturn(false);

        when(veiculoMapper.paraEntidade(request))
                .thenReturn(veiculo);

        when(veiculoRepository.save(veiculo))
                .thenReturn(veiculoSalvo);

        when(veiculoMapper.paraResponse(veiculoSalvo))
                .thenReturn(responseEsperado);

        VeiculoResponse resultado =
                veiculoService.cadastrarVeiculo(request);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Renault Duster", resultado.getModelo());
        assertEquals("ABC1D23", resultado.getPlaca());
        assertEquals(4, resultado.getCapacidadePassageiros());
        assertEquals("Veículo executivo", resultado.getObservacao());
        assertTrue(resultado.isAtivo());

        verify(veiculoRepository)
                .existsByPlaca("ABC1D23");

        verify(veiculoMapper)
                .paraEntidade(request);

        verify(veiculoRepository)
                .save(veiculo);

        verify(veiculoMapper)
                .paraResponse(veiculoSalvo);
    }

    @Test
    void deveLancarExcecaoQuandoPlacaJaEstiverCadastrada() {

        VeiculoRequest request = new VeiculoRequest();
        request.setModelo("Renault Duster");
        request.setPlaca("ABC1D23");
        request.setCapacidadePassageiros(4);

        when(veiculoRepository.existsByPlaca("ABC1D23"))
                .thenReturn(true);

        assertThrows(
                VeiculoJaCadastradoException.class,
                () -> veiculoService.cadastrarVeiculo(request)
        );

        verify(veiculoRepository)
                .existsByPlaca("ABC1D23");

        verify(veiculoMapper, never())
                .paraEntidade(any(VeiculoRequest.class));

        verify(veiculoRepository, never())
                .save(any(Veiculo.class));

        verify(veiculoMapper, never())
                .paraResponse(any(Veiculo.class));
    }

    @Test
    void deveBuscarVeiculoPorIdComSucesso() {

        Long id = 1L;

        Veiculo veiculo = new Veiculo();
        veiculo.setId(id);
        veiculo.setModelo("Renault Duster");
        veiculo.setPlaca("ABC1D23");
        veiculo.setCapacidadePassageiros(4);
        veiculo.setAtivo(true);

        VeiculoResponse responseEsperado = new VeiculoResponse();
        responseEsperado.setId(id);
        responseEsperado.setModelo("Renault Duster");
        responseEsperado.setPlaca("ABC1D23");
        responseEsperado.setCapacidadePassageiros(4);
        responseEsperado.setAtivo(true);

        when(veiculoRepository.findById(id))
                .thenReturn(Optional.of(veiculo));

        when(veiculoMapper.paraResponse(veiculo))
                .thenReturn(responseEsperado);

        VeiculoResponse resultado =
                veiculoService.buscarVeiculoPorId(id);

        assertNotNull(resultado);
        assertEquals(id, resultado.getId());
        assertEquals("Renault Duster", resultado.getModelo());
        assertEquals("ABC1D23", resultado.getPlaca());
        assertEquals(4, resultado.getCapacidadePassageiros());
        assertTrue(resultado.isAtivo());

        verify(veiculoRepository)
                .findById(id);

        verify(veiculoMapper)
                .paraResponse(veiculo);
    }

    @Test
    void deveLancarExcecaoQuandoVeiculoNaoForEncontrado() {

        Long id = 999L;

        when(veiculoRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                VeiculoNaoEncontradoException.class,
                () -> veiculoService.buscarVeiculoPorId(id)
        );

        verify(veiculoRepository)
                .findById(id);

        verify(veiculoMapper, never())
                .paraResponse(any(Veiculo.class));
    }

    @Test
    void deveListarVeiculosComSucesso() {

        Veiculo veiculo1 = new Veiculo();
        veiculo1.setId(1L);
        veiculo1.setModelo("Renault Duster");
        veiculo1.setPlaca("ABC1D23");

        Veiculo veiculo2 = new Veiculo();
        veiculo2.setId(2L);
        veiculo2.setModelo("Fiat Ducato");
        veiculo2.setPlaca("DEF4G56");

        VeiculoResponse response1 = new VeiculoResponse();
        response1.setId(1L);
        response1.setModelo("Renault Duster");
        response1.setPlaca("ABC1D23");

        VeiculoResponse response2 = new VeiculoResponse();
        response2.setId(2L);
        response2.setModelo("Fiat Ducato");
        response2.setPlaca("DEF4G56");

        when(veiculoRepository.findAll())
                .thenReturn(List.of(veiculo1, veiculo2));

        when(veiculoMapper.paraResponse(veiculo1))
                .thenReturn(response1);

        when(veiculoMapper.paraResponse(veiculo2))
                .thenReturn(response2);

        List<VeiculoResponse> resultado =
                veiculoService.listarVeiculos();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());

        assertEquals(
                "Renault Duster",
                resultado.get(0).getModelo()
        );

        assertEquals(
                "Fiat Ducato",
                resultado.get(1).getModelo()
        );

        verify(veiculoRepository)
                .findAll();

        verify(veiculoMapper)
                .paraResponse(veiculo1);

        verify(veiculoMapper)
                .paraResponse(veiculo2);
    }

    @Test
    void deveAtualizarVeiculoMantendoAPropriaPlaca() {

        Long id = 1L;

        VeiculoRequest request = new VeiculoRequest();
        request.setModelo("Renault Duster Iconic");
        request.setPlaca("ABC1D23");
        request.setCapacidadePassageiros(4);
        request.setObservacao("Veículo atualizado");

        Veiculo veiculo = new Veiculo();
        veiculo.setId(id);
        veiculo.setModelo("Renault Duster");
        veiculo.setPlaca("ABC1D23");
        veiculo.setCapacidadePassageiros(4);
        veiculo.setAtivo(true);

        VeiculoResponse responseEsperado = new VeiculoResponse();
        responseEsperado.setId(id);
        responseEsperado.setModelo("Renault Duster Iconic");
        responseEsperado.setPlaca("ABC1D23");
        responseEsperado.setCapacidadePassageiros(4);
        responseEsperado.setObservacao("Veículo atualizado");
        responseEsperado.setAtivo(true);

        when(veiculoRepository.findById(id))
                .thenReturn(Optional.of(veiculo));

        when(veiculoRepository.save(veiculo))
                .thenReturn(veiculo);

        when(veiculoMapper.paraResponse(veiculo))
                .thenReturn(responseEsperado);

        VeiculoResponse resultado =
                veiculoService.atualizarVeiculo(
                        id,
                        request
                );

        assertNotNull(resultado);
        assertEquals(id, resultado.getId());

        assertEquals(
                "Renault Duster Iconic",
                resultado.getModelo()
        );

        assertEquals(
                "ABC1D23",
                resultado.getPlaca()
        );

        /*
         * Como a placa enviada é a mesma que o veículo
         * já possui, o && do service sofre short-circuit.
         *
         * Portanto, existsByPlaca() nem precisa ser chamado.
         */
        verify(veiculoRepository, never())
                .existsByPlaca(anyString());

        verify(veiculoMapper)
                .atualizarVeiculo(
                        veiculo,
                        request
                );

        verify(veiculoRepository)
                .save(veiculo);

        verify(veiculoMapper)
                .paraResponse(veiculo);
    }

    @Test
    void deveLancarExcecaoAoAtualizarComPlacaDeOutroVeiculo() {

        Long id = 1L;

        VeiculoRequest request = new VeiculoRequest();
        request.setModelo("Renault Duster");
        request.setPlaca("DEF4G56");
        request.setCapacidadePassageiros(4);

        Veiculo veiculo = new Veiculo();
        veiculo.setId(id);
        veiculo.setModelo("Renault Duster");
        veiculo.setPlaca("ABC1D23");
        veiculo.setCapacidadePassageiros(4);
        veiculo.setAtivo(true);

        when(veiculoRepository.findById(id))
                .thenReturn(Optional.of(veiculo));

        when(veiculoRepository.existsByPlaca("DEF4G56"))
                .thenReturn(true);

        assertThrows(
                VeiculoJaCadastradoException.class,
                () -> veiculoService.atualizarVeiculo(
                        id,
                        request
                )
        );

        verify(veiculoRepository)
                .findById(id);

        verify(veiculoRepository)
                .existsByPlaca("DEF4G56");

        verify(veiculoMapper, never())
                .atualizarVeiculo(
                        any(Veiculo.class),
                        any(VeiculoRequest.class)
                );

        verify(veiculoRepository, never())
                .save(any(Veiculo.class));

        verify(veiculoMapper, never())
                .paraResponse(any(Veiculo.class));
    }

    @Test
    void deveInativarVeiculoComSucesso() {

        Long id = 1L;

        Veiculo veiculo = new Veiculo();
        veiculo.setId(id);
        veiculo.setModelo("Renault Duster");
        veiculo.setPlaca("ABC1D23");
        veiculo.setCapacidadePassageiros(4);
        veiculo.setAtivo(true);

        VeiculoResponse responseEsperado = new VeiculoResponse();
        responseEsperado.setId(id);
        responseEsperado.setModelo("Renault Duster");
        responseEsperado.setPlaca("ABC1D23");
        responseEsperado.setCapacidadePassageiros(4);
        responseEsperado.setAtivo(false);

        when(veiculoRepository.findById(id))
                .thenReturn(Optional.of(veiculo));

        when(veiculoRepository.save(veiculo))
                .thenReturn(veiculo);

        when(veiculoMapper.paraResponse(veiculo))
                .thenReturn(responseEsperado);

        VeiculoResponse resultado =
                veiculoService.inativarVeiculo(id);

        assertNotNull(resultado);
        assertFalse(resultado.isAtivo());
        assertFalse(veiculo.isAtivo());

        verify(veiculoRepository)
                .findById(id);

        verify(veiculoRepository)
                .save(veiculo);

        verify(veiculoMapper)
                .paraResponse(veiculo);
    }

    @Test
    void deveAtivarVeiculoComSucesso() {

        Long id = 1L;

        Veiculo veiculo = new Veiculo();
        veiculo.setId(id);
        veiculo.setModelo("Renault Duster");
        veiculo.setPlaca("ABC1D23");
        veiculo.setCapacidadePassageiros(4);
        veiculo.setAtivo(false);

        VeiculoResponse responseEsperado = new VeiculoResponse();
        responseEsperado.setId(id);
        responseEsperado.setModelo("Renault Duster");
        responseEsperado.setPlaca("ABC1D23");
        responseEsperado.setCapacidadePassageiros(4);
        responseEsperado.setAtivo(true);

        when(veiculoRepository.findById(id))
                .thenReturn(Optional.of(veiculo));

        when(veiculoRepository.save(veiculo))
                .thenReturn(veiculo);

        when(veiculoMapper.paraResponse(veiculo))
                .thenReturn(responseEsperado);

        VeiculoResponse resultado =
                veiculoService.ativarVeiculo(id);

        assertNotNull(resultado);
        assertTrue(resultado.isAtivo());
        assertTrue(veiculo.isAtivo());

        verify(veiculoRepository)
                .findById(id);

        verify(veiculoRepository)
                .save(veiculo);

        verify(veiculoMapper)
                .paraResponse(veiculo);
    }
}