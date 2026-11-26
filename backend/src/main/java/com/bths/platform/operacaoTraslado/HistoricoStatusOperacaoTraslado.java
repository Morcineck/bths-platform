package com.bths.platform.operacaoTraslado;

import com.bths.platform.traslado.enums.StatusTraslado;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "historico_status_operacao_traslado")
public class HistoricoStatusOperacaoTraslado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(
            name = "operacao_traslado_id",
            nullable = false
    )
    private OperacaoTraslado operacaoTraslado;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status_anterior",
            nullable = false
    )
    private StatusTraslado statusAnterior;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "novo_status",
            nullable = false
    )
    private StatusTraslado novoStatus;

    @Column(nullable = false)
    private String motivo;

    @Column(
            name = "data_hora",
            nullable = false
    )
    private LocalDateTime dataHora;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OperacaoTraslado getOperacaoTraslado() {
        return operacaoTraslado;
    }

    public void setOperacaoTraslado(
            OperacaoTraslado operacaoTraslado
    ) {
        this.operacaoTraslado =
                operacaoTraslado;
    }

    public StatusTraslado getStatusAnterior() {
        return statusAnterior;
    }

    public void setStatusAnterior(
            StatusTraslado statusAnterior
    ) {
        this.statusAnterior =
                statusAnterior;
    }

    public StatusTraslado getNovoStatus() {
        return novoStatus;
    }

    public void setNovoStatus(
            StatusTraslado novoStatus
    ) {
        this.novoStatus =
                novoStatus;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(
            String motivo
    ) {
        this.motivo = motivo;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(
            LocalDateTime dataHora
    ) {
        this.dataHora =
                dataHora;
    }
}