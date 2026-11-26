package com.bths.platform.traslado.dto;

import com.bths.platform.traslado.enums.StatusTraslado;
import jakarta.validation.constraints.NotNull;

public class TrasladoStatusRequest {

    @NotNull(message = "O status é obrigatório!")
    private StatusTraslado status;

    public StatusTraslado getStatus() {
        return status;
    }

    public void setStatus(StatusTraslado status) {
        this.status = status;
    }
}
