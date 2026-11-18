package com.hemobit.hemobit.domain;

public enum TipoHemocomponente {
    CONCENTRADO_HEMACIAS("Concentrado de hemácias", 42),
    CONCENTRADO_PLAQUETAS("Concentrado de plaquetas", 5),
    PLASMA_FRESCO_CONGELADO("Plasma fresco congelado", 365);

    private final String rotulo;
    private final int validadeReferenciaDias;

    TipoHemocomponente(String rotulo, int validadeReferenciaDias) {
        this.rotulo = rotulo;
        this.validadeReferenciaDias = validadeReferenciaDias;
    }

    public String getRotulo() {
        return rotulo;
    }

    public int getValidadeReferenciaDias() {
        return validadeReferenciaDias;
    }
}
