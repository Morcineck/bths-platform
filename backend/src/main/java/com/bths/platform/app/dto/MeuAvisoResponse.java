package com.bths.platform.app.dto;

import com.bths.platform.aviso.enums.TipoAviso;

import java.time.LocalDateTime;

public class MeuAvisoResponse {

    private Long id;
    private String titulo;
    private String mensagem;
    private TipoAviso tipo;
    private LocalDateTime dataPublicacao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public TipoAviso getTipo() {
        return tipo;
    }

    public void setTipo(TipoAviso tipo) {
        this.tipo = tipo;
    }

    public LocalDateTime getDataPublicacao() {
        return dataPublicacao;
    }

    public void setDataPublicacao(
            LocalDateTime dataPublicacao
    ) {
        this.dataPublicacao =
                dataPublicacao;
    }
}