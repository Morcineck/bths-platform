package com.bths.platform.traslado.dto;

import jakarta.validation.constraints.NotNull;

public class TrasladoOperacaoRequest {

    @NotNull(message = "O motorista é obrigatório!")
    private Long motoristaId;

    @NotNull(message = "O veículo é obrigatório!")
    private Long veiculoId;

    public Long getMotoristaId() {
        return motoristaId;
    }

    public void setMotoristaId(Long motoristaId) {
        this.motoristaId = motoristaId;
    }

    public Long getVeiculoId() {
        return veiculoId;
    }

    public void setVeiculoId(Long veiculoId) {
        this.veiculoId = veiculoId;
    }
}
