package com.bths.platform.traslado.enums;

public enum Aeroporto {

    GRU("Aeroporto Internacional de Guarulhos"),
    CGH("Aeroporto de Congonhas"),
    VCP("Aeroporto Internacional de Viracopos");

    public final String descricao;

    Aeroporto(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
