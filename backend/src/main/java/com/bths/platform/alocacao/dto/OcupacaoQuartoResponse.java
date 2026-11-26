package com.bths.platform.alocacao.dto;

public class OcupacaoQuartoResponse {

    private Long quartoId;
    private String quartoNome;
    private Integer capacidade;
    private Long ocupacao;
    private Long vagasDisponiveis;

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
