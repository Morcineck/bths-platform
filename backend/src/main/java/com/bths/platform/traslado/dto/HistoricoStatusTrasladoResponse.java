package com.bths.platform.traslado.dto;

import com.bths.platform.traslado.enums.StatusTraslado;

import java.time.LocalDateTime;

public class HistoricoStatusTrasladoResponse {

    private Long id;
    private Long trasladoId;
    private StatusTraslado statusAnterior;
    private StatusTraslado novoStatus;
    private String motivo;
    private LocalDateTime dataHora;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTrasladoId() {
        return trasladoId;
    }

    public void setTrasladoId(Long trasladoId) {
        this.trasladoId = trasladoId;
    }

    public StatusTraslado getStatusAnterior() {
        return statusAnterior;
    }

    public void setStatusAnterior(StatusTraslado statusAnterior) {
        this.statusAnterior = statusAnterior;
    }

    public StatusTraslado getNovoStatus() {
        return novoStatus;
    }

    public void setNovoStatus(StatusTraslado novoStatus) {
        this.novoStatus = novoStatus;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }
}
