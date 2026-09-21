package com.hemobit.hemobit.dto;

public class IndicadoresDTO {
    private String nomeIndicador;
    private Double media;
    private Double mediana;
    private Double moda;
    private Double variancia;
    private Double desvioPadrao;

    public IndicadoresDTO() {}

    public IndicadoresDTO(String nomeIndicador, Double media, Double mediana, Double moda, Double variancia, Double desvioPadrao) {
        this.nomeIndicador = nomeIndicador;
        this.media = media;
        this.mediana = mediana;
        this.moda = moda;
        this.variancia = variancia;
        this.desvioPadrao = desvioPadrao;
    }

    public String getNomeIndicador() { return nomeIndicador; }
    public void setNomeIndicador(String nomeIndicador) { this.nomeIndicador = nomeIndicador; }

    public Double getMedia() { return media; }
    public void setMedia(Double media) { this.media = media; }

    public Double getMediana() { return mediana; }
    public void setMediana(Double mediana) { this.mediana = mediana; }

    public Double getModa() { return moda; }
    public void setModa(Double moda) { this.moda = moda; }

    public Double getVariancia() { return variancia; }
    public void setVariancia(Double variancia) { this.variancia = variancia; }

    public Double getDesvioPadrao() { return desvioPadrao; }
    public void setDesvioPadrao(Double desvioPadrao) { this.desvioPadrao = desvioPadrao; }
}
