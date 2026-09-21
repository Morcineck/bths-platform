package com.bths.platform.checkin.dto;

import jakarta.validation.constraints.NotBlank;

public class CheckInRequest {

    private String observacao;

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
