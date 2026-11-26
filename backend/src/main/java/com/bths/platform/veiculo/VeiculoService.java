package com.bths.platform.veiculo;

import com.bths.platform.veiculo.dto.VeiculoRequest;
import com.bths.platform.veiculo.dto.VeiculoResponse;
import com.bths.platform.veiculo.exception.VeiculoJaCadastradoException;
import com.bths.platform.veiculo.exception.VeiculoNaoEncontradoException;
import com.bths.platform.veiculo.mapper.VeiculoMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VeiculoService {

    private final VeiculoRepository veiculoRepository;
    private final VeiculoMapper veiculoMapper;

    public VeiculoService(
            VeiculoRepository veiculoRepository,
            VeiculoMapper veiculoMapper
    ) {

        this.veiculoRepository = veiculoRepository;
        this.veiculoMapper = veiculoMapper;
    }

    public VeiculoResponse cadastrarVeiculo(
            VeiculoRequest request

    ) {

        if (veiculoRepository.existsByPlaca(
                request.getPlaca()
        )) {
            throw new VeiculoJaCadastradoException(
                    "Já existe um veículo cadastrado com essa placa!"
            );
        }

        Veiculo veiculo =
                veiculoMapper.paraEntidade(request);

        Veiculo salvo =
                veiculoRepository.save(veiculo);

        return veiculoMapper.paraResponse(salvo);
    }

    public VeiculoResponse buscarVeiculoPorId(
            Long id
    ) {

        Veiculo veiculo =
                veiculoRepository.findById(id)
                        .orElseThrow(() -> new VeiculoNaoEncontradoException(
                                        "Veículo não encontrado!"
                                )
                        );

        return veiculoMapper.paraResponse(veiculo);
    }

    public List<VeiculoResponse> listarVeiculos() {

        return veiculoRepository.findAll()
                .stream()
                .map(veiculoMapper::paraResponse)
                .toList();
    }

    public VeiculoResponse atualizarVeiculo(
            Long id,
            VeiculoRequest request
    ) {

        Veiculo veiculo =
                veiculoRepository.findById(id)
                        .orElseThrow(() -> new VeiculoNaoEncontradoException(
                                        "Veículo não encontrado!"
                                )
                        );

        boolean placaPertenceAOutroVeiculo =
                !veiculo.getPlaca().equals(
                        request.getPlaca()
                )

                        &&
                        veiculoRepository.existsByPlaca(
                                request.getPlaca()
                        );

        if (placaPertenceAOutroVeiculo) {
            throw new VeiculoJaCadastradoException(
                    "Já existe um veículo cadastrado com essa placa!"
            );

        }

        veiculoMapper.atualizarVeiculo(
                veiculo,
                request
        );

        Veiculo atualizado =
                veiculoRepository.save(veiculo);

        return veiculoMapper.paraResponse(atualizado);

    }

    public VeiculoResponse ativarVeiculo(
            Long id) {

        Veiculo veiculo =
                veiculoRepository.findById(id)
                        .orElseThrow(() ->
                                new VeiculoNaoEncontradoException(
                                        "Veículo não encontrado!"
                                )
                        );

        veiculo.setAtivo(true);

        Veiculo atualizado =
                veiculoRepository.save(veiculo);

        return veiculoMapper.paraResponse(atualizado);
    }

    public VeiculoResponse inativarVeiculo(
            Long id
    ) {

        Veiculo veiculo =
                veiculoRepository.findById(id)
                        .orElseThrow(() -> new VeiculoNaoEncontradoException(
                                "Veículo não encontrado!"
                        ));

        veiculo.setAtivo(false);

        Veiculo atualizado =
                veiculoRepository.save(veiculo);

        return veiculoMapper.paraResponse(atualizado);
    }
}

