package com.bths.platform.operacaoTraslado.dto;

import com.bths.platform.traslado.enums.Aeroporto;
import com.bths.platform.traslado.enums.TipoTraslado;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class OperacaoTrasladoUpdateRequest {

    @NotNull(message = "O tipo do traslado é obrigatório!")
    private TipoTraslado tipo;

    private Aeroporto aeroporto;

    @NotNull(message = "A data e hora prevista são obrigatórias!")
    private LocalDateTime dataHoraPrevista;

    @NotBlank(message = "A origem é obrigatória!")
    private String localOrigem;

    @NotBlank(message = "O destino é obrigatório!")
    private String localDestino;

    @NotNull(message = "O motorista é obrigatório!")
    private Long motoristaId;

    @NotNull(message = "O veículo é obrigatório!")
    private Long veiculoId;

    private String observacao;

    public TipoTraslado getTipo() {
        return tipo;
    }

    public void setTipo(
            TipoTraslado tipo
    ) {
        this.tipo = tipo;
    }

    public Aeroporto getAeroporto() {
        return aeroporto;
    }

    public void setAeroporto(
            Aeroporto aeroporto
    ) {
        this.aeroporto = aeroporto;
    }

    public LocalDateTime getDataHoraPrevista() {
        return dataHoraPrevista;
    }

    public void setDataHoraPrevista(
            LocalDateTime dataHoraPrevista
    ) {
        this.dataHoraPrevista = dataHoraPrevista;
    }

    public String getLocalOrigem() {
        return localOrigem;
    }

    public void setLocalOrigem(
            String localOrigem
    ) {
        this.localOrigem = localOrigem;
    }

    public String getLocalDestino() {
        return localDestino;
    }

    public void setLocalDestino(
            String localDestino
    ) {
        this.localDestino = localDestino;
    }

    public Long getMotoristaId() {
        return motoristaId;
    }

    public void setMotoristaId(
            Long motoristaId
    ) {
        this.motoristaId = motoristaId;
    }

    public Long getVeiculoId() {
        return veiculoId;
    }

    public void setVeiculoId(
            Long veiculoId
    ) {
        this.veiculoId = veiculoId;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(
            String observacao
    ) {
        this.observacao = observacao;
    }
}