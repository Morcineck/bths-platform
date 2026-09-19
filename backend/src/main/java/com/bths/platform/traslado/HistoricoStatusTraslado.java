package com.bths.platform.traslado;

import com.bths.platform.traslado.enums.StatusTraslado;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "historico_status_traslado")
public class HistoricoStatusTraslado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "traslado_id", nullable = false)
    private Traslado traslado;

    @Enumerated(EnumType.STRING)
    @Column(name = "traslado_anterior", nullable = false)
    private StatusTraslado statusAterior;

    @Enumerated(EnumType.STRING)
    @Column(name = "novo_status", nullable = false)
    private StatusTraslado novoStatus;

    @Column(nullable = false)
    private String motivo;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Traslado getTraslado() {
        return traslado;
    }

    public void setTraslado(Traslado traslado) {
        this.traslado = traslado;
    }

    public StatusTraslado getStatusAterior() {
        return statusAterior;
    }

    public void setStatusAterior(StatusTraslado statusAterior) {
        this.statusAterior = statusAterior;
    }

    public StatusTraslado getNovoStatus() {
        return novoStatus;
    }

    public void setNovoStatus(StatusTraslado novoStatus) {
        this.novoStatus = novoStatus;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }
}
