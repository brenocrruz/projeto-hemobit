package com.hemobit.hemobit.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Map;

public class IndicadoresDTO {

    private String nomeIndicador;
    private Double media;
    private Double mediana;
    private Double moda;
    private Double variancia;
    private Double desvioPadrao;
    private final Map<Double, Long> distribuicaoFrequencia;

    public IndicadoresDTO(String nomeIndicador, Double media, Double mediana, Double moda, Double variancia, Double desvioPadrao) {
        this(nomeIndicador, media, mediana, moda, variancia, desvioPadrao, Map.of());
    }

    public IndicadoresDTO(String nomeIndicador, Double media, Double mediana, Double moda,
                          Double variancia, Double desvioPadrao, Map<Double, Long> distribuicaoFrequencia) {
        this.nomeIndicador = nomeIndicador;
        this.media = media;
        this.mediana = mediana;
        this.moda = moda;
        this.variancia = variancia;
        this.desvioPadrao = desvioPadrao;
        this.distribuicaoFrequencia = Map.copyOf(distribuicaoFrequencia);
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

    @JsonIgnore
    public Map<Double, Long> getDistribuicaoFrequencia() {
        return distribuicaoFrequencia;
    }
}
