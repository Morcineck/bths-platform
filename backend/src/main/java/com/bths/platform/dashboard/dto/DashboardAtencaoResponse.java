package com.bths.platform.dashboard.dto;

import java.util.List;

public class DashboardAtencaoResponse {

    private Long hospedesSemQuarto;
    private List<DashboardHospedeSemQuartoResponse> hospedesSemQuartoDetalhes;


    public Long getHospedesSemQuarto() {
        return hospedesSemQuarto;
    }

    public void setHospedesSemQuarto(Long hospedesSemQuarto) {
        this.hospedesSemQuarto = hospedesSemQuarto;
    }

    public List<DashboardHospedeSemQuartoResponse> getHospedesSemQuartoDetalhes() {
        return hospedesSemQuartoDetalhes;
    }

    public void setHospedesSemQuartoDetalhes(List<DashboardHospedeSemQuartoResponse> hospedesSemQuartoDetalhes) {
        this.hospedesSemQuartoDetalhes = hospedesSemQuartoDetalhes;
    }
}
