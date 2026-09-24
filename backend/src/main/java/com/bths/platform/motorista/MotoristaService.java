package com.bths.platform.motorista;

import com.bths.platform.motorista.dto.MotoristaRequest;
import com.bths.platform.motorista.dto.MotoristaResponse;
import com.bths.platform.motorista.exception.MotoristaNaoEncontradoException;
import com.bths.platform.motorista.mapper.MotoristaMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MotoristaService {

    private final MotoristaRepository motoristaRepository;
    private final MotoristaMapper motoristaMapper;

    public MotoristaService(
            MotoristaRepository motoristaRepository,
            MotoristaMapper motoristaMapper
    ) {
        this.motoristaRepository = motoristaRepository;
        this.motoristaMapper = motoristaMapper;
    }

    public MotoristaResponse cadastrarMotorista(
            MotoristaRequest request
    ) {

        Motorista motorista =
                motoristaMapper.paraEntidade(request);

        Motorista salvo =
                motoristaRepository.save(motorista);

        return motoristaMapper.paraResponse(salvo);
    }

    public MotoristaResponse buscarMotoristaPorId(
            Long id
    ) {

        Motorista motorista =
                motoristaRepository.findById(id)
                        .orElseThrow(() ->
                                new MotoristaNaoEncontradoException(
                                        "Motorista não encontrado!"
                                )
                        );

        return motoristaMapper.paraResponse(motorista);
    }

    public List<MotoristaResponse> listarMotoristas() {

        return motoristaRepository.findAll()
                .stream()
                .map(motoristaMapper::paraResponse)
                .toList();
    }

    public MotoristaResponse atualizarMotorista(
            Long id,
            MotoristaRequest request
    ) {

        Motorista motorista =
                motoristaRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Motorista não encontrado!"
                                )
                        );

        motoristaMapper.atualizarEntidade(
                motorista,
                request
        );

        Motorista atualizado =
                motoristaRepository.save(motorista);

        return motoristaMapper.paraResponse(atualizado);
    }

    public MotoristaResponse inativarMotorista(
            Long id
    ) {

        Motorista motorista =
                motoristaRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Motorista não encontrado!"
                                )
                        );

        motorista.setAtivo(false);

        Motorista atualizado =
                motoristaRepository.save(motorista);

        return motoristaMapper.paraResponse(atualizado);
    }

    public MotoristaResponse ativarMotorista(
            Long id
    ) {

        Motorista motorista =
                motoristaRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Motorista não encontrado!"
                                )
                        );

        motorista.setAtivo(true);

        Motorista atualizado =
                motoristaRepository.save(motorista);

        return motoristaMapper.paraResponse(atualizado);
    }
}