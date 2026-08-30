package com.bths.platform.traslado.dto;

import com.bths.platform.traslado.enums.StatusTraslado;

public class TrasladoCorrecaoStatusRequest {

    private StatusTraslado status;
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

