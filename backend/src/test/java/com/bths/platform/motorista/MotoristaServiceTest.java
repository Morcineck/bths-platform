package com.bths.platform.motorista;

import com.bths.platform.motorista.dto.MotoristaRequest;
import com.bths.platform.motorista.dto.MotoristaResponse;
import com.bths.platform.motorista.exception.MotoristaNaoEncontradoException;
import com.bths.platform.motorista.mapper.MotoristaMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MotoristaServiceTest {

    @Mock
    private MotoristaRepository motoristaRepository;

    @Mock
    private MotoristaMapper motoristaMapper;

    @InjectMocks
    private MotoristaService motoristaService;

    @Test
    void deveCadastrarMotorista() {

        MotoristaRequest request =
                new MotoristaRequest();

        request.setNomeCompleto(
                "Carlos da Silva"
        );

        request.setTelefone(
                "21999999999"
        );

        request.setObservacao(
                "Motorista terceirizado"
        );

        Motorista motorista =
                new Motorista();

        motorista.setNomeCompleto(
                "Carlos da Silva"
        );

        motorista.setTelefone(
                "21999999999"
        );

        motorista.setObservacao(
                "Motorista terceirizado"
        );

        motorista.setAtivo(true);

        Motorista motoristaSalvo =
                new Motorista();

        motoristaSalvo.setId(1L);

        motoristaSalvo.setNomeCompleto(
                "Carlos da Silva"
        );

        motoristaSalvo.setTelefone(
                "21999999999"
        );

        motoristaSalvo.setObservacao(
                "Motorista terceirizado"
        );

        motoristaSalvo.setAtivo(true);

        MotoristaResponse response =
                new MotoristaResponse();

        response.setId(1L);
        response.setNomeCompleto(
                "Carlos da Silva"
        );
        response.setTelefone(
                "21999999999"
        );
        response.setObservacao(
                "Motorista terceirizado"
        );
        response.setAtivo(true);

        when(
                motoristaMapper.paraEntidade(
                        request
                )
        ).thenReturn(motorista);

        when(
                motoristaRepository.save(
                        motorista
                )
        ).thenReturn(motoristaSalvo);

        when(
                motoristaMapper.paraResponse(
                        motoristaSalvo
                )
        ).thenReturn(response);

        MotoristaResponse resultado =
                motoristaService
                        .cadastrarMotorista(
                                request
                        );

        assertNotNull(resultado);
        assertEquals(
                1L,
                resultado.getId()
        );

        assertEquals(
                "Carlos da Silva",
                resultado.getNomeCompleto()
        );

        assertTrue(
                resultado.isAtivo()
        );

        verify(motoristaRepository)
                .save(motorista);
    }

    @Test
    void deveBuscarMotoristaPorId() {

        Motorista motorista =
                new Motorista();

        motorista.setId(1L);

        motorista.setNomeCompleto(
                "Carlos da Silva"
        );

        motorista.setAtivo(true);

        MotoristaResponse response =
                new MotoristaResponse();

        response.setId(1L);

        response.setNomeCompleto(
                "Carlos da Silva"
        );

        response.setAtivo(true);

        when(
                motoristaRepository.findById(
                        1L
                )
        ).thenReturn(
                Optional.of(motorista)
        );

        when(
                motoristaMapper.paraResponse(
                        motorista
                )
        ).thenReturn(response);

        MotoristaResponse resultado =
                motoristaService
                        .buscarMotoristaPorId(
                                1L
                        );

        assertEquals(
                1L,
                resultado.getId()
        );

        assertEquals(
                "Carlos da Silva",
                resultado.getNomeCompleto()
        );
    }

    @Test
    void deveLancarExceptionQuandoMotoristaNaoExistir() {

        when(
                motoristaRepository.findById(
                        99L
                )
        ).thenReturn(
                Optional.empty()
        );

        MotoristaNaoEncontradoException exception =
                assertThrows(
                        MotoristaNaoEncontradoException.class,
                        () ->
                                motoristaService
                                        .buscarMotoristaPorId(
                                                99L
                                        )
                );

        assertEquals(
                "Motorista não encontrado!",
                exception.getMessage()
        );
    }

    @Test
    void deveListarMotoristas() {

        Motorista motorista1 =
                new Motorista();

        motorista1.setId(1L);
        motorista1.setNomeCompleto(
                "Carlos"
        );

        Motorista motorista2 =
                new Motorista();

        motorista2.setId(2L);
        motorista2.setNomeCompleto(
                "João"
        );

        MotoristaResponse response1 =
                new MotoristaResponse();

        response1.setId(1L);
        response1.setNomeCompleto(
                "Carlos"
        );

        MotoristaResponse response2 =
                new MotoristaResponse();

        response2.setId(2L);
        response2.setNomeCompleto(
                "João"
        );

        when(
                motoristaRepository.findAll()
        ).thenReturn(
                List.of(
                        motorista1,
                        motorista2
                )
        );

        when(
                motoristaMapper.paraResponse(
                        motorista1
                )
        ).thenReturn(response1);

        when(
                motoristaMapper.paraResponse(
                        motorista2
                )
        ).thenReturn(response2);

        List<MotoristaResponse> resultado =
                motoristaService
                        .listarMotoristas();

        assertEquals(
                2,
                resultado.size()
        );
    }

    @Test
    void deveAtualizarMotorista() {

        MotoristaRequest request =
                new MotoristaRequest();

        request.setNomeCompleto(
                "Carlos Atualizado"
        );

        Motorista motorista =
                new Motorista();

        motorista.setId(1L);
        motorista.setNomeCompleto(
                "Carlos"
        );

        MotoristaResponse response =
                new MotoristaResponse();

        response.setId(1L);
        response.setNomeCompleto(
                "Carlos Atualizado"
        );

        when(
                motoristaRepository.findById(
                        1L
                )
        ).thenReturn(
                Optional.of(motorista)
        );

        when(
                motoristaRepository.save(
                        motorista
                )
        ).thenReturn(motorista);

        when(
                motoristaMapper.paraResponse(
                        motorista
                )
        ).thenReturn(response);

        MotoristaResponse resultado =
                motoristaService
                        .atualizarMotorista(
                                1L,
                                request
                        );

        verify(motoristaMapper)
                .atualizarEntidade(
                        motorista,
                        request
                );

        assertEquals(
                "Carlos Atualizado",
                resultado.getNomeCompleto()
        );
    }

    @Test
    void deveInativarMotorista() {

        Motorista motorista =
                new Motorista();

        motorista.setId(1L);
        motorista.setAtivo(true);

        MotoristaResponse response =
                new MotoristaResponse();

        response.setId(1L);
        response.setAtivo(false);

        when(
                motoristaRepository.findById(
                        1L
                )
        ).thenReturn(
                Optional.of(motorista)
        );

        when(
                motoristaRepository.save(
                        motorista
                )
        ).thenReturn(motorista);

        when(
                motoristaMapper.paraResponse(
                        motorista
                )
        ).thenReturn(response);

        MotoristaResponse resultado =
                motoristaService
                        .inativarMotorista(
                                1L
                        );

        assertFalse(
                motorista.isAtivo()
        );

        assertFalse(
                resultado.isAtivo()
        );
    }

    @Test
    void deveAtivarMotorista() {

        Motorista motorista =
                new Motorista();

        motorista.setId(1L);
        motorista.setAtivo(false);

        MotoristaResponse response =
                new MotoristaResponse();

        response.setId(1L);
        response.setAtivo(true);

        when(
                motoristaRepository.findById(
                        1L
                )
        ).thenReturn(
                Optional.of(motorista)
        );

        when(
                motoristaRepository.save(
                        motorista
                )
        ).thenReturn(motorista);

        when(
                motoristaMapper.paraResponse(
                        motorista
                )
        ).thenReturn(response);

        MotoristaResponse resultado =
                motoristaService
                        .ativarMotorista(
                                1L
                        );

        assertTrue(
                motorista.isAtivo()
        );

        assertTrue(
                resultado.isAtivo()
        );
    }
}