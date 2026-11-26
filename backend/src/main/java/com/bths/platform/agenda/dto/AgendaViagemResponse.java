package com.bths.platform.agenda.dto;

import com.bths.platform.agenda.enums.TipoAgendaViagem;

import java.time.LocalDateTime;

public class AgendaViagemResponse {

    private Long id;
    private String titulo;
    private String descricao;

    private LocalDateTime dataHoraInicio;
    private LocalDateTime dataHoraFim;

    private TipoAgendaViagem tipo;

    private Integer ordem;
    private Boolean visivelHospede;
    private Boolean ativo;

    private Long viagemId;
    private String viagemNome;

    public Long getId() {
        return id;
    }

    public void setId(
            Long id
    ) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(
            String titulo
    ) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(
            String descricao
    ) {
        this.descricao = descricao;
    }

    public LocalDateTime getDataHoraInicio() {
        return dataHoraInicio;
    }

    public void setDataHoraInicio(
            LocalDateTime dataHoraInicio
    ) {
        this.dataHoraInicio =
                dataHoraInicio;
    }

    public LocalDateTime getDataHoraFim() {
        return dataHoraFim;
    }

    public void setDataHoraFim(
            LocalDateTime dataHoraFim
    ) {
        this.dataHoraFim =
                dataHoraFim;
    }

    public TipoAgendaViagem getTipo() {
        return tipo;
    }

    public void setTipo(
            TipoAgendaViagem tipo
    ) {
        this.tipo = tipo;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(
            Integer ordem
    ) {
        this.ordem = ordem;
    }

    public Boolean getVisivelHospede() {
        return visivelHospede;
    }

    public void setVisivelHospede(
            Boolean visivelHospede
    ) {
        this.visivelHospede =
                visivelHospede;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(
            Boolean ativo
    ) {
        this.ativo = ativo;
    }

    public Long getViagemId() {
        return viagemId;
    }

    public void setViagemId(
            Long viagemId
    ) {
        this.viagemId =
                viagemId;
    }

    public String getViagemNome() {
        return viagemNome;
    }

    public void setViagemNome(
            String viagemNome
    ) {
        this.viagemNome =
                viagemNome;
    }
}