package com.bths.platform.quarto.dto;

import com.bths.platform.quarto.enums.StatusQuarto;
import com.bths.platform.quarto.enums.TipoQuarto;

public class QuartoOcupacaoResponse {

    private Long quartoId;
    private String nome;
    private TipoQuarto tipo;
    private StatusQuarto status;
    private Integer capacidade;
    private Long ocupacao;
    private Long vagasDisponiveis;

    public Long getQuartoId() {
        return quartoId;
    }

    public void setQuartoId(Long quartoId) {
        this.quartoId = quartoId;
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

    public StatusQuarto getStatus() {
        return status;
    }

    public void setStatus(StatusQuarto status) {
        this.status = status;
    }

    public Integer getCapacidade() {
        return capacidade;
    }

    public void setCapacidade(Integer capacidade) {
        this.capacidade = capacidade;
    }

    public Long getOcupacao() {
        return ocupacao;
    }

    public void setOcupacao(Long ocupacao) {
        this.ocupacao = ocupacao;
    }

    public Long getVagasDisponiveis() {
        return vagasDisponiveis;
    }

    public void setVagasDisponiveis(Long vagasDisponiveis) {
        this.vagasDisponiveis = vagasDisponiveis;
    }
}
