package com.bths.platform.quarto.dto;

import com.bths.platform.quarto.enums.StatusQuarto;
import com.bths.platform.quarto.enums.TipoQuarto;

public class QuartoResponse {

    private Long id;
    private String nome;
    private TipoQuarto tipo;
    private Integer capacidade;
    private StatusQuarto status;

    private Long viagemId;
    private String viagemNome;

    public QuartoResponse() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public TipoQuarto getTipo() {
        return tipo;
    }

    public void setTipo(TipoQuarto tipo) {
        this.tipo = tipo;
    }

    public Integer getCapacidade() {
        return capacidade;
    }

    public void setCapacidade(Integer capacidade) {
        this.capacidade = capacidade;
    }

    public StatusQuarto getStatus() {
        return status;
    }

    public void setStatus(StatusQuarto status) {
        this.status = status;
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
}
