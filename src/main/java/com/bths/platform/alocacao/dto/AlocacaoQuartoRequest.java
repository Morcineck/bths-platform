package com.bths.platform.alocacao.dto;

import jakarta.validation.constraints.NotNull;

public class AlocacaoQuartoRequest {

    @NotNull
    private Long hospedeId;

    @NotNull
    private Long quartoId;

    public AlocacaoQuartoRequest() {
    }

    public Long getHospedeId() {
        return hospedeId;
    }

    public void setHospedeId(Long hospedeId) {
        this.hospedeId = hospedeId;
    }

    public Long getQuartoId() {
        return quartoId;
    }

    public void setQuartoId(Long quartoId) {
        this.quartoId = quartoId;
    }
}
