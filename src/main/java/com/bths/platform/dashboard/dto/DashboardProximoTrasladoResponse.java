package com.bths.platform.dashboard.dto;

import com.bths.platform.traslado.enums.TipoTraslado;

import java.time.LocalDateTime;

public class DashboardProximoTrasladoResponse {

    private long id;
    private String hospedeNome;
    private TipoTraslado tipo;
    private LocalDateTime dataHoraPrevista;
    private String localOrigem;
    private String localDestino;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getHospedeNome() {
        return hospedeNome;
    }

    public void setHospedeNome(String hospedeNome) {
        this.hospedeNome = hospedeNome;
    }

    public TipoTraslado getTipo() {
        return tipo;
    }

    public void setTipo(TipoTraslado tipo) {
        this.tipo = tipo;
    }

    public LocalDateTime getDataHoraPrevista() {
        return dataHoraPrevista;
    }

    public void setDataHoraPrevista(LocalDateTime dataHoraPrevista) {
        this.dataHoraPrevista = dataHoraPrevista;
    }

    public String getLocalOrigem() {
        return localOrigem;
    }

    public void setLocalOrigem(String localOrigem) {
        this.localOrigem = localOrigem;
    }

    public String getLocalDestino() {
        return localDestino;
    }

    public void setLocalDestino(String localDestino) {
        this.localDestino = localDestino;
    }
}
