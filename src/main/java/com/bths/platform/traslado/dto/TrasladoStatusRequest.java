package com.bths.platform.traslado.dto;

import com.bths.platform.traslado.enums.StatusTraslado;

public class TrasladoStatusRequest {

    private StatusTraslado status;

    public StatusTraslado getStatus() {
        return status;
    }

    public void setStatus(StatusTraslado status) {
        this.status = status;
    }
}
