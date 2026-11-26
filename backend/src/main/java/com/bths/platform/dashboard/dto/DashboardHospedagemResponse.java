package com.bths.platform.dashboard.dto;

public class DashboardHospedagemResponse {

    private long vagasTotais;
    private long ocupadas;
    private long disponiveis;

    public long getVagasTotais() {
        return vagasTotais;
    }

    public void setVagasTotais(long vagasTotais) {
        this.vagasTotais = vagasTotais;
    }

    public long getOcupadas() {
        return ocupadas;
    }

    public void setOcupadas(long ocupadas) {
        this.ocupadas = ocupadas;
    }

    public long getDisponiveis() {
        return disponiveis;
    }

    public void setDisponiveis(long disponiveis) {
        this.disponiveis = disponiveis;
    }
}
