package com.hemobit.hemobit.dto;

public class IndicadoresDTO {

    private String nomeIndicador;
    private Double media;
    private Double mediana;
    private Double moda;
    private Double variancia;
    private Double desvioPadrao;

    public IndicadoresDTO(String nomeIndicador, Double media, Double mediana, Double moda, Double variancia, Double desvioPadrao) {
        this.nomeIndicador = nomeIndicador;
        this.media = media;
        this.mediana = mediana;
        this.moda = moda;
        this.variancia = variancia;
        this.desvioPadrao = desvioPadrao;
    }

    public String getNomeIndicador() {
        return nomeIndicador;
    }

    public Double getMedia() {
        return media;
    }

    public Double getMediana() {
        return mediana;
    }

    public Double getModa() {
        return moda;
    }

    public Double getVariancia() {
        return variancia;
    }

    public Double getDesvioPadrao() {
        return desvioPadrao;
    }
}
