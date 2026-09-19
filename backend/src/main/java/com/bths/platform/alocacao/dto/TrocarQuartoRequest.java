package com.bths.platform.alocacao.dto;

import jakarta.validation.constraints.NotNull;

public class TrocarQuartoRequest {

    @NotNull
    private Long novoQuartoId;

    public TrocarQuartoRequest() {

    }

    public Long getNovoQuartoId() {
        return novoQuartoId;
    }

    public void setNovoQuartoId(Long novoQuartoId) {
        this.novoQuartoId = novoQuartoId;
    }
}
