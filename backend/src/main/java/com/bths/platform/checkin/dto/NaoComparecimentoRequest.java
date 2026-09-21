package com.bths.platform.checkin.dto;

import jakarta.validation.constraints.NotBlank;

public class NaoComparecimentoRequest {

    @NotBlank(message = "O motivo do não comparecimento é obrigatório")
    private String motivo;

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
