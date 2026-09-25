package com.bths.platform.traslado;

import com.bths.platform.hospede.Hospede;
import com.bths.platform.motorista.Motorista;
import com.bths.platform.operacaoTraslado.OperacaoTraslado;
import com.bths.platform.traslado.enums.Aeroporto;
import com.bths.platform.traslado.enums.StatusTraslado;
import com.bths.platform.traslado.enums.TipoTraslado;
import com.bths.platform.veiculo.Veiculo;
import com.bths.platform.viagem.Viagem;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "traslados")
public class Traslado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "hospede_id", nullable = false)
    private Hospede hospede;

    @ManyToOne(optional = false)
    @JoinColumn(name = "viagem_id", nullable = false)
    private Viagem viagem;

    @ManyToOne
    @JoinColumn(name = "motorista_id")
    private Motorista motorista;

    @ManyToOne
    @JoinColumn(name = "veiculo_id")
    private Veiculo veiculo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoTraslado tipo;

    @Enumerated(EnumType.STRING)
    private Aeroporto aeroporto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusTraslado status = StatusTraslado.AGUARDANDO;

    @Column(name = "data_hora_prevista", nullable = false)
    private LocalDateTime dataHoraPrevista;

    @Column(name = "numero_voo")
    private String numeroVoo;

    @Column(name = "companhia_aerea")
    private String companhiaAerea;

    @Column(name = "local_origem", nullable = false)
    private String localOrigem;

    @Column(name = "local_destino", nullable = false)
    private String localDestino;

    @Column(columnDefinition = "TEXT")
    private String observacao;

    @ManyToOne
    @JoinColumn(name = "operacao_traslado_id")
    private OperacaoTraslado operacaoTraslado;

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

    public Viagem getViagem() {
        return viagem;
    }

    public void setViagem(Viagem viagem) {
        this.viagem = viagem;
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

    public void setDataHoraPrevista(LocalDateTime dataHoraPrevista) {
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

    public void setCompanhiaAerea(String companhiaAerea) {
        this.companhiaAerea = companhiaAerea;
    }

    public String getLocalOrigem() {
        return localOrigem;
    }

    public void setLocalOrigem(String localOrigem) {
        this.localOrigem = localOrigem;
    }

    public String getLocalDestino() {
        return localDestino;
    }

    public void setLocalDestino(String localDestino) {
        this.localDestino = localDestino;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Motorista getMotorista() {
        return motorista;
    }

    public void setMotorista(Motorista motorista) {
        this.motorista = motorista;
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(Veiculo veiculo) {
        this.veiculo = veiculo;
    }

    public OperacaoTraslado getOperacaoTraslado() {
        return operacaoTraslado;
    }

    public void setOperacaoTraslado(OperacaoTraslado operacaoTraslado) {
        this.operacaoTraslado = operacaoTraslado;
    }
}
