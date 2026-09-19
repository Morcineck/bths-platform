package com.bths.platform.qrcode.dto;

import com.bths.platform.hospede.enums.StatusCheckIn;

public class QrCodeCheckInResponse {

    private Long hospedeId;
    private String hospedeNome;
    private StatusCheckIn statusCheckIn;

    private Long viagemId;
    private String viagemNome;

    private Long quartoId;
    private String quartoNome;

    public Long getHospedeId() {
        return hospedeId;
    }

    public void setHospedeId(Long hospedeId) {
        this.hospedeId = hospedeId;
    }

    public String getHospedeNome() {
        return hospedeNome;
    }

    public void setHospedeNome(String hospedeNome) {
        this.hospedeNome = hospedeNome;
    }

    public StatusCheckIn getStatusCheckIn() {
        return statusCheckIn;
    }

    public void setStatusCheckIn(StatusCheckIn statusCheckIn) {
        this.statusCheckIn = statusCheckIn;
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

    public Long getQuartoId() {
        return quartoId;
    }

    public void setQuartoId(Long quartoId) {
        this.quartoId = quartoId;
    }

    public String getQuartoNome() {
        return quartoNome;
    }

    public void setQuartoNome(String quartoNome) {
        this.quartoNome = quartoNome;
    }
}
