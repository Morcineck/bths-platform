package com.bths.platform.traslado.dto;

import com.bths.platform.traslado.enums.Aeroporto;
import com.bths.platform.traslado.enums.TipoTraslado;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class TrasladoRequest {

    @NotNull(message = "O hóspede é obrigatório!")
    private Long hospedeId;

    @NotNull(message = "A viagem é obrigatória!")
    private Long viagemId;

    @NotNull(message = "O tipo de traslado é obrigatório!")
    private TipoTraslado tipo;

    private Aeroporto aeroporto;

    @NotNull(message = "A data e hora prevista são obrigatórias!")
    private LocalDateTime dataHoraPrevista;

    private String numeroVoo;
    private String companhiaAerea;

    @NotBlank(message = "O local de origem é obrigatório!")
    private String localOrigem;

    @NotBlank(message = "O local de destino é obrigatório!")
    private String localDestino;

    private String observacoes;

    public Long getHospedeId() {
        return hospedeId;
    }

    public void setHospedeId(Long hospedeId) {
        this.hospedeId = hospedeId;
    }

    public Long getViagemId() {
        return viagemId;
    }

    public void setViagemId(Long viagemId) {
        this.viagemId = viagemId;
    }

    public TipoTraslado getTipo() {
        return tipo;
    }

    public void setTipo(TipoTraslado tipo) {
        this.tipo = tipo;
    }

    public Aeroporto getAeroporto() {
        return aeroporto;
    }

    public void setAeroporto(Aeroporto aeroporto) {
        this.aeroporto = aeroporto;
    }

    public LocalDateTime getDataHoraPrevista() {
        return dataHoraPrevista;
    }

    public void setDataHoraPrevista(LocalDateTime dataHoraPrevista) {
        this.dataHoraPrevista = dataHoraPrevista;
    }

    public String getNumeroVoo() {
        return numeroVoo;
    }

    public void setNumeroVoo(String numeroVoo) {
        this.numeroVoo = numeroVoo;
    }

    public String getCompanhiaAerea() {
        return companhiaAerea;
    }

    public void setCompanhiaAerea(String companhiaAerea) {
        this.companhiaAerea = companhiaAerea;
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

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}