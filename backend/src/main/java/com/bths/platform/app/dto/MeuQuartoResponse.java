package com.bths.platform.app.dto;

import com.bths.platform.alocacao.enums.TipoCama;
import com.bths.platform.quarto.enums.TipoQuarto;

public class MeuQuartoResponse {

    private Long quartoId;
    private String quartoNome;
    private TipoQuarto quartoTipo;
    private TipoCama tipoCama;
    private Integer capacidade;

    private Long viagemId;
    private String viagemNome;



    public Long getQuartoId() {
        return quartoId;
    }

    public void setQuartoId(Long quartoId) {
        this.quartoId = quartoId;
    }

    public String getQuartoNome() {
        return quartoNome;
    }

    public void setQuartoNome(String quartoNome) {
        this.quartoNome = quartoNome;
    }

    public TipoQuarto getQuartoTipo() {
        return quartoTipo;
    }

    public void setQuartoTipo(TipoQuarto quartoTipo) {
        this.quartoTipo = quartoTipo;
    }

    public Integer getCapacidade() {
        return capacidade;
    }

    public void setCapacidade(Integer capacidade) {
        this.capacidade = capacidade;
    }

    public Long getViagemId() {
        return viagemId;
    }

    public void setViagemId(Long viagemId) {
        this.viagemId = viagemId;
    }

    public String getViagemNome() {
        return viagemNome;
    }

    public void setViagemNome(String viagemNome) {
        this.viagemNome = viagemNome;
    }

    public TipoCama getTipoCama() {
        return tipoCama;
    }

    public void setTipoCama(TipoCama tipoCama) {
        this.tipoCama = tipoCama;
    }
}
