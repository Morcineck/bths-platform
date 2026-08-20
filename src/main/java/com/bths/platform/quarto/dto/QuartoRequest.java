package com.bths.platform.quarto.dto;


import com.bths.platform.quarto.StatusQuarto;
import com.bths.platform.quarto.TipoQuarto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class QuartoRequest {

    @NotBlank
    private String nome;

    @NotNull
    private TipoQuarto tipo;

    @NotNull
    @Min(value = 1, message = "A capacidade deve ser pelo menos 1 hóspede")
    private Integer capacidade;

    @NotNull
    private StatusQuarto status;

    @NotNull
    private Long viagemId;

    public QuartoRequest() {

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
}
