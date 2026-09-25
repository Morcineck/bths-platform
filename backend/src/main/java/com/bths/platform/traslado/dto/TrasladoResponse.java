package com.bths.platform.traslado.dto;

import com.bths.platform.traslado.enums.Aeroporto;
import com.bths.platform.traslado.enums.StatusTraslado;
import com.bths.platform.traslado.enums.TipoTraslado;

import java.time.LocalDateTime;

public class TrasladoResponse {

    private Long id;

    private Long hospedeId;
    private String hospedeNome;

    private Long viagemId;
    private String viagemNome;

    private Long operacaoTrasladoId;

    private Long motoristaId;
    private String motoristaNome;

    private Long veiculoId;
    private String veiculoModelo;
    private String veiculoPlaca;
    private Integer veiculoCapacidadePassageiros;

    private TipoTraslado tipo;
    private Aeroporto aeroporto;
    private StatusTraslado status;

    private LocalDateTime dataHoraPrevista;

    private String numeroVoo;
    private String companhiaAerea;

    private String localOrigem;
    private String localDestino;

    private String observacoes;

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

    public Long getOperacaoTrasladoId() {
        return operacaoTrasladoId;
    }

    public void setOperacaoTrasladoId(
            Long operacaoTrasladoId
    ) {
        this.operacaoTrasladoId = operacaoTrasladoId;
    }

    public Long getMotoristaId() {
        return motoristaId;
    }

    public void setMotoristaId(Long motoristaId) {
        this.motoristaId = motoristaId;
    }

    public String getMotoristaNome() {
        return motoristaNome;
    }

    public void setMotoristaNome(String motoristaNome) {
        this.motoristaNome = motoristaNome;
    }

    public Long getVeiculoId() {
        return veiculoId;
    }

    public void setVeiculoId(Long veiculoId) {
        this.veiculoId = veiculoId;
    }

    public String getVeiculoModelo() {
        return veiculoModelo;
    }

    public void setVeiculoModelo(String veiculoModelo) {
        this.veiculoModelo = veiculoModelo;
    }

    public String getVeiculoPlaca() {
        return veiculoPlaca;
    }

    public void setVeiculoPlaca(String veiculoPlaca) {
        this.veiculoPlaca = veiculoPlaca;
    }

    public Integer getVeiculoCapacidadePassageiros() {
        return veiculoCapacidadePassageiros;
    }

    public void setVeiculoCapacidadePassageiros(
            Integer veiculoCapacidadePassageiros
    ) {
        this.veiculoCapacidadePassageiros =
                veiculoCapacidadePassageiros;
    }

    public TipoTraslado getTipo() {
        return tipo;
    }

    public void setTipo(TipoTraslado tipo) {
        this.tipo = tipo;
    }

    public Aeroporto getAeroporto() {
        return aeroporto;
    }

    public void setAeroporto(Aeroporto aeroporto) {
        this.aeroporto = aeroporto;
    }

    public StatusTraslado getStatus() {
        return status;
    }

    public void setStatus(StatusTraslado status) {
        this.status = status;
    }

    public LocalDateTime getDataHoraPrevista() {
        return dataHoraPrevista;
    }

    public void setDataHoraPrevista(
            LocalDateTime dataHoraPrevista
    ) {
        this.dataHoraPrevista = dataHoraPrevista;
    }

    public String getNumeroVoo() {
        return numeroVoo;
    }

    public void setNumeroVoo(String numeroVoo) {
        this.numeroVoo = numeroVoo;
    }

    public String getCompanhiaAerea() {
        return companhiaAerea;
    }

    public void setCompanhiaAerea(
            String companhiaAerea
    ) {
        this.companhiaAerea = companhiaAerea;
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

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(
            String observacoes
    ) {
        this.observacoes = observacoes;
    }
}