package com.bths.platform.motorista.dto;


import jakarta.validation.constraints.NotBlank;

public class MotoristaRequest {

    @NotBlank(message = "O nome do motorista é obrigatório")
    private String nomeCompleto;

    private String telefone;

    private String observacao;

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
