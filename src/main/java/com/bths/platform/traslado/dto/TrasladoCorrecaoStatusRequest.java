package com.bths.platform.traslado.dto;

import com.bths.platform.traslado.enums.StatusTraslado;
import jakarta.validation.constraints.NotNull;

public class TrasladoCorrecaoStatusRequest {

    @NotNull(message = "O status é obrigatório!")
    private StatusTraslado status;

    @NotNull(message = "O motivo da correção é obrigatório!")
    private String motivo;

    public StatusTraslado getStatus() {
        return status;
    }

    public void setStatus(StatusTraslado status) {
        this.status = status;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}

