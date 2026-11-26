package com.bths.platform.hospedagem.dto;

import java.time.LocalTime;

public class HospedagemResponse {

    private Long id;
    private String nome;

    private String endereco;
    private String cidade;
    private String estado;

    private String localizacaoUrl;
    private String imagemUrl;

    private String wifiNome;
    private String wifiSenha;

    private LocalTime horarioCheckIn;
    private LocalTime horarioCheckOut;

    private String contatoNome;
    private String contatoTelefone;

    private String observacaoPublica;

    private Long viagemId;
    private String viagemNome;

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

    public String getLocalizacaoUrl() {
        return localizacaoUrl;
    }

    public void setLocalizacaoUrl(String localizacaoUrl) {
        this.localizacaoUrl = localizacaoUrl;
    }

    public String getImagemUrl() {
        return imagemUrl;
    }

    public void setImagemUrl(String imagemUrl) {
        this.imagemUrl = imagemUrl;
    }

    public String getWifiNome() {
        return wifiNome;
    }

    public void setWifiNome(String wifiNome) {
        this.wifiNome = wifiNome;
    }

    public String getWifiSenha() {
        return wifiSenha;
    }

    public void setWifiSenha(String wifiSenha) {
        this.wifiSenha = wifiSenha;
    }

    public LocalTime getHorarioCheckIn() {
        return horarioCheckIn;
    }

    public void setHorarioCheckIn(LocalTime horarioCheckIn) {
        this.horarioCheckIn = horarioCheckIn;
    }

    public LocalTime getHorarioCheckOut() {
        return horarioCheckOut;
    }

    public void setHorarioCheckOut(LocalTime horarioCheckOut) {
        this.horarioCheckOut = horarioCheckOut;
    }

    public String getContatoNome() {
        return contatoNome;
    }

    public void setContatoNome(String contatoNome) {
        this.contatoNome = contatoNome;
    }

    public String getContatoTelefone() {
        return contatoTelefone;
    }

    public void setContatoTelefone(String contatoTelefone) {
        this.contatoTelefone = contatoTelefone;
    }

    public String getObservacaoPublica() {
        return observacaoPublica;
    }

    public void setObservacaoPublica(String observacaoPublica) {
        this.observacaoPublica = observacaoPublica;
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
