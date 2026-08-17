package com.bths.platform.viagem.dto;

import com.bths.platform.viagem.StatusViagem;

import java.time.LocalDate;

public class ViagemResponse {

    private Long id;
    private String nome;
    private String evento;
    private LocalDate dataIcinio;
    private LocalDate dataFim;
    private String endereco;
    private String cidade;
    private String estado;
    private StatusViagem status;

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

    public String getEvento() {
        return evento;
    }

    public void setEvento(String evento) {
        this.evento = evento;
    }

    public LocalDate getDataIcinio() {
        return dataIcinio;
    }

    public void setDataIcinio(LocalDate dataIcinio) {
        this.dataIcinio = dataIcinio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public StatusViagem getStatus() {
        return status;
    }

    public void setStatus(StatusViagem status) {
        this.status = status;
    }
}
