package com.bths.platform.hospede;

import com.bths.platform.viagem.Viagem;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "hospedes", uniqueConstraints = {
        @UniqueConstraint(name = "uk_hospede_cpf_viagem",
        columnNames = {"cpf", "viagem_id"})})
public class Hospede {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false)
    private String nomeCompleto;

    @Column(nullable = false, length = 11)
    private String cpf;

    private String telefone;

    private String email;

    private LocalDate dataNascimento;

    private LocalDateTime horarioPrevistoChegada;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusCheckIn statusCheckIn;

    @ManyToOne
    @JoinColumn(name = "viagem_id", nullable = false)
    private Viagem viagem;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public LocalDateTime getHorarioPrevistoChegada() {
        return horarioPrevistoChegada;
    }

    public void setHorarioPrevistoChegada(LocalDateTime horarioPrevistoChegada) {
        this.horarioPrevistoChegada = horarioPrevistoChegada;
    }

    public StatusCheckIn getStatusCheckIn() {
        return statusCheckIn;
    }

    public void setStatusCheckIn(StatusCheckIn statusCheckIn) {
        this.statusCheckIn = statusCheckIn;
    }

    public Viagem getViagem() {
        return viagem;
    }

    public void setViagem(Viagem viagem) {
        this.viagem = viagem;
    }

    public Hospede() {

    }
}
