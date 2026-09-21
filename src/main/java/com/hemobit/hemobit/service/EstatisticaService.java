package com.hemobit.hemobit.dto;

import java.util.Map;

public class IndicadoresDTO {

    private String nomeIndicador;
    private double media;
    private double mediana;
    private double moda;
    private double desvioPadrao;
    private double variancia;
    private Map<String, Long> distribuicaoFrequencia;

    public IndicadoresDTO() {
    }

    public IndicadoresDTO(String nomeIndicador, double media, double mediana, double moda, double desvioPadrao, double variancia, Map<String, Long> distribuicaoFrequencia) {
        this.nomeIndicador = nomeIndicador;
        this.media = media;
        this.mediana = mediana;
        this.moda = moda;
        this.desvioPadrao = desvioPadrao;
        this.variancia = variancia;
        this.distribuicaoFrequencia = distribuicaoFrequencia;
    }

    public String getNomeIndicador() {
        return nomeIndicador;
    }

    public void setNomeIndicador(String nomeIndicador) {
        this.nomeIndicador = nomeIndicador;
    }

    public double getMedia() {
        return media;
    }

    public void setMedia(double media) {
        this.media = media;
    }

    public double getMediana() {
        return mediana;
    }

    public void setMediana(double mediana) {
        this.mediana = mediana;
    }

    public double getModa() {
        return moda;
    }

    public void setModa(double moda) {
        this.moda = moda;
    }

    public double getDesvioPadrao() {
        return desvioPadrao;
    }

    public void setDesvioPadrao(double desvioPadrao) {
        this.desvioPadrao = desvioPadrao;
    }

    public double getVariancia() {
        return variancia;
    }

    public void setVariancia(double variancia) {
        this.variancia = variancia;
    }

    public Map<String, Long> getDistribuicaoFrequencia() {
        return distribuicaoFrequencia;
    }

    public void setDistribuicaoFrequencia(Map<String, Long> distribuicaoFrequencia) {
        this.distribuicaoFrequencia = distribuicaoFrequencia;
    }
}
