package com.bths.platform.alocacao;

import com.bths.platform.hospede.Hospede;
import com.bths.platform.quarto.Quarto;
import com.bths.platform.viagem.Viagem;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "alocacao_quartos")
public class AlocacaoQuarto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "hospede_id", nullable = false)
    private Hospede hospede;

    @ManyToOne
    @JoinColumn(name = "quarto_id", nullable = false)
    private Quarto quarto;

    @ManyToOne
    @JoinColumn(name = "viagem_id", nullable = false)
    private Viagem viagem;

    @Column(nullable = false)
    private LocalDateTime dataAlocacao;

    public AlocacaoQuarto() {
    }

    @PrePersist
    public void prePersist() {
        if (dataAlocacao == null) {
            dataAlocacao = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Hospede getHospede() {
        return hospede;
    }

    public void setHospede(Hospede hospede) {
        this.hospede = hospede;
    }

    public Quarto getQuarto() {
        return quarto;
    }

    public void setQuarto(Quarto quarto) {
        this.quarto = quarto;
    }

    public Viagem getViagem() {
        return viagem;
    }

    public void setViagem(Viagem viagem) {
        this.viagem = viagem;
    }

    public LocalDateTime getDataAlocacao() {
        return dataAlocacao;
    }

    public void setDataAlocacao(LocalDateTime dataAlocacao) {
        this.dataAlocacao = dataAlocacao;
    }
}
