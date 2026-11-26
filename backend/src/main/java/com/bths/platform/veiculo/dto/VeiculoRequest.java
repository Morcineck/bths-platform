package com.bths.platform.veiculo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class VeiculoRequest {

    @NotBlank(message = "O modelo do veículo é obrigatório!")
    private String modelo;

    @NotBlank(message = "A placa do veículo é obrigatória!")
    @Pattern(
            regexp = "^[A-Z]{3}[0-9][A-Z0-9][0-9]{2}$",
            message = "A placa deve estar no formato válido!"
    )

    private String placa;

    @NotNull(message = "A capacidade de passageiros é obrigatória!")
    @Min(
            value = 1,
            message = "A capacidade deve ser de pelo menos 1 passageiro!"
    )
    private Integer capacidadePassageiros;

    private String observacao;

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public Integer getCapacidadePassageiros() {
        return capacidadePassageiros;
    }

    public void setCapacidadePassageiros(Integer capacidadePassageiros) {
        this.capacidadePassageiros = capacidadePassageiros;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
