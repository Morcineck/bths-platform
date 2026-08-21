package com.bths.platform.alocacao.dto;

import java.time.LocalDateTime;

public class AlocacaoQuartoResponse {

    private Long id;

    private Long hospedeId;
    private String hospedeNome;

    private Long quartoId;
    private String quartoNome;

    private Long viagemId;
    private String viagemNome;

    private LocalDateTime dataAlocacao;

    public AlocacaoQuartoResponse() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getHospedeId() {
        return hospedeId;
    }

    public void setHospedeId(Long hospedeId) {
        this.hospedeId = hospedeId;
    }

    public String getHospedeNome() {
        return hospedeNome;
    }

    public void setHospedeNome(String hospedeNome) {
        this.hospedeNome = hospedeNome;
    }

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

    public LocalDateTime getDataAlocacao() {
        return dataAlocacao;
    }

    public void setDataAlocacao(LocalDateTime dataAlocacao) {
        this.dataAlocacao = dataAlocacao;
    }
}
