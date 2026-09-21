package com.hemobit.hemobit.dto;

public class IndicadoresDTO {

    private String nomeMetrica;
    private Double media;
    private Double mediana;
    private Double moda;
    private Double variancia;
    private Double desvioPadrao;
    private Integer totalAmostras;

    public IndicadoresDTO() {
    }

    public IndicadoresDTO(String nomeMetrica, Double media, Double mediana, Double moda, Double variancia, Double desvioPadrao, Integer totalAmostras) {
        this.nomeMetrica = nomeMetrica;
        this.media = media;
        this.mediana = mediana;
        this.moda = moda;
        this.variancia = variancia;
        this.desvioPadrao = desvioPadrao;
        this.totalAmostras = totalAmostras;
    }

    public String getNomeMetrica() {
        return nomeMetrica;
    }

    public void setNomeMetrica(String nomeMetrica) {
        this.nomeMetrica = nomeMetrica;
    }

    public Double getMedia() {
        return media;
    }

    public void setMedia(Double media) {
        this.media = media;
    }

    public Double getMediana() {
        return mediana;
    }

    public void setMediana(Double mediana) {
        this.mediana = mediana;
    }

    public Double getModa() {
        return moda;
    }

    public void setModa(Double moda) {
        this.moda = moda;
    }

    public Double getVariancia() {
        return variancia;
    }

    public void setVariancia(Double variancia) {
        this.variancia = variancia;
    }

    public Double getDesvioPadrao() {
        return desvioPadrao;
    }

    public void setDesvioPadrao(Double desvioPadrao) {
        this.desvioPadrao = desvioPadrao;
    }

    public Integer getTotalAmostras() {
        return totalAmostras;
    }

    public void setTotalAmostras(Integer totalAmostras) {
        this.totalAmostras = totalAmostras;
    }
}
