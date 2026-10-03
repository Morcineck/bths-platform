package com.bths.platform.agenda.dto;

import com.bths.platform.agenda.enums.TipoAgendaViagem;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class AgendaViagemRequest {

    @NotBlank
    private String titulo;

    private String descricao;

    @NotNull
    private LocalDateTime dataHoraInicio;

    private LocalDateTime dataHoraFim;

    @NotNull
    private TipoAgendaViagem tipo;

    @NotNull
    private Integer ordem;

    @NotNull
    private Boolean visivelHospede;

    @NotNull
    private Boolean ativo;

    @NotNull
    private Long viagemId;

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
}