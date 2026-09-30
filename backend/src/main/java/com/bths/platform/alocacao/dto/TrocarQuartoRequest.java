package com.bths.platform.alocacao.dto;

import com.bths.platform.alocacao.enums.TipoCama;
import jakarta.validation.constraints.NotNull;

public class TrocarQuartoRequest {

    @NotNull
    private Long novoQuartoId;

    @NotNull
    private TipoCama tipoCama;

    public TrocarQuartoRequest() {

    }

    public Long getNovoQuartoId() {
        return novoQuartoId;
    }

    public void setNovoQuartoId(Long novoQuartoId) {
        this.novoQuartoId = novoQuartoId;
    }

    public TipoCama getTipoCama() {
        return tipoCama;
    }

    public void setTipoCama(TipoCama tipoCama) {
        this.tipoCama = tipoCama;
    }
}
