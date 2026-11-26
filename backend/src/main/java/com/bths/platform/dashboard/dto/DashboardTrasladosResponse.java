package com.bths.platform.dashboard.dto;

import java.util.List;

public class DashboardTrasladosResponse {

    private long aguardando;
    private long emAndamento;
    private long concluidos;
    private List<DashboardProximoTrasladoResponse> proximos;

    public long getAguardando() {
        return aguardando;
    }

    public void setAguardando(long aguardando) {
        this.aguardando = aguardando;
    }

    public long getEmAndamento() {
        return emAndamento;
    }

    public void setEmAndamento(long emAndamento) {
        this.emAndamento = emAndamento;
    }

    public long getConcluidos() {
        return concluidos;
    }

    public void setConcluidos(long concluidos) {
        this.concluidos = concluidos;
    }

    public List<DashboardProximoTrasladoResponse> getProximos() {
        return proximos;
    }

    public void setProximos(List<DashboardProximoTrasladoResponse> proximos) {
        this.proximos = proximos;
    }
}
