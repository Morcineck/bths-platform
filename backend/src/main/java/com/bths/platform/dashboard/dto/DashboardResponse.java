package com.bths.platform.dashboard.dto;

public class DashboardResponse {

    private long viagemId;
    private DashboardHospedesResponse hospedes;
    private DashboardHospedagemResponse hospedagem;
    private DashboardTrasladosResponse traslados;

    public long getViagemId() {
        return viagemId;
    }

    public void setViagemId(long viagemId) {
        this.viagemId = viagemId;
    }

    public DashboardHospedesResponse getHospedes() {
        return hospedes;
    }

    public void setHospedes(DashboardHospedesResponse hospedes) {
        this.hospedes = hospedes;
    }

    public DashboardHospedagemResponse getHospedagem() {
        return hospedagem;
    }

    public void setHospedagem(DashboardHospedagemResponse hospedagem) {
        this.hospedagem = hospedagem;
    }

    public DashboardTrasladosResponse getTraslados() {
        return traslados;
    }

    public void setTraslados(DashboardTrasladosResponse traslados) {
        this.traslados = traslados;
    }
}
