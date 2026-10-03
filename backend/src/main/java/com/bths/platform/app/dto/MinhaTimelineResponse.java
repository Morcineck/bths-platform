package com.bths.platform.app.dto;

import com.bths.platform.agenda.enums.TipoAgendaViagem;

import java.time.LocalDateTime;

public class MinhaTimelineResponse {

    private Long id;
    private String titulo;
    private String descricao;

    private LocalDateTime dataHoraInicio;
    private LocalDateTime dataHoraFim;

    private TipoAgendaViagem tipo;

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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDateTime getDataHoraInicio() {
        return dataHoraInicio;
    }

    public void setDataHoraInicio(LocalDateTime dataHoraInicio) {
        this.dataHoraInicio = dataHoraInicio;
    }

    public LocalDateTime getDataHoraFim() {
        return dataHoraFim;
    }

    public void setDataHoraFim(LocalDateTime dataHoraFim) {
        this.dataHoraFim = dataHoraFim;
    }

    public TipoAgendaViagem getTipo() {
        return tipo;
    }

    public void setTipo(TipoAgendaViagem tipo) {
        this.tipo = tipo;
    }
}
