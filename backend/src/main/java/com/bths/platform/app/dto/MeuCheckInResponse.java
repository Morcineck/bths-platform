package com.bths.platform.app.dto;

import com.bths.platform.hospede.enums.StatusCheckIn;

import java.time.LocalDateTime;

public class MeuCheckInResponse {

    private String hospedeNome;
    private StatusCheckIn statusCheckIn;
    private LocalDateTime dataHoraCheckIn;

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

    public LocalDateTime getDataHoraCheckIn() {
        return dataHoraCheckIn;
    }

    public void setDataHoraCheckIn(LocalDateTime dataHoraCheckIn) {
        this.dataHoraCheckIn = dataHoraCheckIn;
    }
}
