package com.bths.platform.operacaoTraslado.dto;

public class OperacaoTrasladoPassageiroResponse {

    private Long trasladoId;

    private Long hospedeId;

    private String hospedeNome;

    public Long getTrasladoId() {
        return trasladoId;
    }

    public void setTrasladoId(
            Long trasladoId
    ) {
        this.trasladoId = trasladoId;
    }

    public Long getHospedeId() {
        return hospedeId;
    }

    public void setHospedeId(
            Long hospedeId
    ) {
        this.hospedeId = hospedeId;
    }

    public String getHospedeNome() {
        return hospedeNome;
    }

    public void setHospedeNome(
            String hospedeNome
    ) {
        this.hospedeNome = hospedeNome;
    }
}