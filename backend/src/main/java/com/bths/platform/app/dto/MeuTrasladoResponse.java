package com.bths.platform.app.dto;

import com.bths.platform.traslado.enums.Aeroporto;
import com.bths.platform.traslado.enums.StatusTraslado;
import com.bths.platform.traslado.enums.TipoTraslado;

import java.time.LocalDateTime;

public class MeuTrasladoResponse {

    private Long id;

    private TipoTraslado tipo;
    private LocalDateTime dataHoraPrevista;

    private String localOrigem;
    private String localDestino;

    private Aeroporto aeroporto;
    private String numeroVoo;
    private String companhiaAerea;

    private StatusTraslado status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public void setDataHoraPrevista(
            LocalDateTime dataHoraPrevista
    ) {
        this.dataHoraPrevista = dataHoraPrevista;
    }

    public String getLocalOrigem() {
        return localOrigem;
    }

    public void setLocalOrigem(
            String localOrigem
    ) {
        this.localOrigem = localOrigem;
    }

    public String getLocalDestino() {
        return localDestino;
    }

    public void setLocalDestino(
            String localDestino
    ) {
        this.localDestino = localDestino;
    }

    public Aeroporto getAeroporto() {
        return aeroporto;
    }

    public void setAeroporto(
            Aeroporto aeroporto
    ) {
        this.aeroporto = aeroporto;
    }

    public String getNumeroVoo() {
        return numeroVoo;
    }

    public void setNumeroVoo(
            String numeroVoo
    ) {
        this.numeroVoo = numeroVoo;
    }

    public String getCompanhiaAerea() {
        return companhiaAerea;
    }

    public void setCompanhiaAerea(
            String companhiaAerea
    ) {
        this.companhiaAerea = companhiaAerea;
    }

    public StatusTraslado getStatus() {
        return status;
    }

    public void setStatus(
            StatusTraslado status
    ) {
        this.status = status;
    }
}