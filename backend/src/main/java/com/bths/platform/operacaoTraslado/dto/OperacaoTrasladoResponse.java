package com.bths.platform.operacaoTraslado.dto;

import com.bths.platform.traslado.enums.Aeroporto;
import com.bths.platform.traslado.enums.StatusTraslado;
import com.bths.platform.traslado.enums.TipoTraslado;

import java.time.LocalDateTime;

public class OperacaoTrasladoResponse {

    private Long id;

    private Long viagemId;
    private String viagemNome;

    private TipoTraslado tipo;
    private Aeroporto aeroporto;
    private StatusTraslado status;

    private LocalDateTime dataHoraPrevista;

    private String localOrigem;
    private String localDestino;

    private Long motoristaId;
    private String motoristaNome;

    private Long veiculoId;
    private String veiculoModelo;
    private String veiculoPlaca;

    private Integer capacidadePassageiros;
    private Long quantidadePassageiros;
    private Long vagasDisponiveis;

    private String observacao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public StatusTraslado getStatus() {
        return status;
    }

    public void setStatus(
            StatusTraslado status
    ) {
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

    public Integer getCapacidadePassageiros() {
        return capacidadePassageiros;
    }

    public void setCapacidadePassageiros(
            Integer capacidadePassageiros
    ) {
        this.capacidadePassageiros = capacidadePassageiros;
    }

    public Long getQuantidadePassageiros() {
        return quantidadePassageiros;
    }

    public void setQuantidadePassageiros(
            Long quantidadePassageiros
    ) {
        this.quantidadePassageiros = quantidadePassageiros;
    }

    public Long getVagasDisponiveis() {
        return vagasDisponiveis;
    }

    public void setVagasDisponiveis(
            Long vagasDisponiveis
    ) {
        this.vagasDisponiveis = vagasDisponiveis;
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