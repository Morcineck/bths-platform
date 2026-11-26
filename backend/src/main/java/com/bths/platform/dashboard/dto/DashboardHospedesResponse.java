package com.bths.platform.dashboard.dto;

public class DashboardHospedesResponse {

    private long total;
    private long presentes;
    private long pendentes;
    private double taxaCheckIn;

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getPresentes() {
        return presentes;
    }

    public void setPresentes(long presentes) {
        this.presentes = presentes;
    }

    public long getPendentes() {
        return pendentes;
    }

    public void setPendentes(long pendentes) {
        this.pendentes = pendentes;
    }

    public double getTaxaCheckIn() {
        return taxaCheckIn;
    }

    public void setTaxaCheckIn(double taxaCheckIn) {
        this.taxaCheckIn = taxaCheckIn;
    }
}
