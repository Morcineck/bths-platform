package com.bths.platform.operacaoTraslado.dto;

import com.bths.platform.traslado.enums.StatusTraslado;
import jakarta.validation.constraints.NotNull;

public class OperacaoTrasladoStatusRequest {

    @NotNull(message = "O status é obrigatório!")
    private StatusTraslado status;

    public StatusTraslado getStatus() {
        return status;
    }

    public void setStatus(StatusTraslado status) {
        this.status = status;
    }
}
