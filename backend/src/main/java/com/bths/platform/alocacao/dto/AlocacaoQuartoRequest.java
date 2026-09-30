package com.bths.platform.alocacao.dto;

import com.bths.platform.alocacao.enums.TipoCama;
import jakarta.validation.constraints.NotNull;

public class AlocacaoQuartoRequest {

    @NotNull
    private Long hospedeId;

    @NotNull
    private Long quartoId;

    @NotNull
    private TipoCama tipoCama;

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

    public TipoCama getTipoCama() {
        return tipoCama;
    }

    public void setTipoCama(TipoCama tipoCama) {
        this.tipoCama = tipoCama;
    }
}
