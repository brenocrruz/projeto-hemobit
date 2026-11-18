package com.hemobit.hemobit.dto;

import com.hemobit.hemobit.domain.Rota;

public class RotaResponseDTO {
    private final Long id;
    private final Long origemId;
    private final String origemNome;
    private final Long destinoId;
    private final String destinoNome;
    private final double distancia;
    private final int tempoEstimado;

    public RotaResponseDTO(Rota rota) {
        this.id = rota.getId();
        this.origemId = rota.getOrigem().getId();
        this.origemNome = rota.getOrigem().getNome();
        this.destinoId = rota.getDestino().getId();
        this.destinoNome = rota.getDestino().getNome();
        this.distancia = rota.getDistancia();
        this.tempoEstimado = rota.getTempoEstimado();
    }

    public Long getId() {
        return id;
    }

    public Long getOrigemId() {
        return origemId;
    }

    public String getOrigemNome() {
        return origemNome;
    }

    public Long getDestinoId() {
        return destinoId;
    }

    public String getDestinoNome() {
        return destinoNome;
    }

    public double getDistancia() {
        return distancia;
    }

    public int getTempoEstimado() {
        return tempoEstimado;
    }
}
