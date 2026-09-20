package com.hemobit.hemobit.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "rota")
public class Rota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "origem_id", nullable = false)
    private Localidade origem;

    @ManyToOne
    @JoinColumn(name = "destino_id", nullable = false)
    private Localidade destino;

    @Column(nullable = false)
    private double distancia;

    @Column(nullable = false)
    private int tempoEstimado;

    public Rota() {
    }

    public Rota(Localidade origem, Localidade destino, double distancia, int tempoEstimado) {
        this.origem = origem;
        this.destino = destino;
        this.distancia = distancia;
        this.tempoEstimado = tempoEstimado;
    }

    public Long getId() {
        return id;
    }

    public Localidade getOrigem() {
        return origem;
    }

    public void setOrigem(Localidade origem) {
        this.origem = origem;
    }

    public Localidade getDestino() {
        return destino;
    }

    public void setDestino(Localidade destino) {
        this.destino = destino;
    }

    public double getDistancia() {
        return distancia;
    }

    public void setDistancia(double distancia) {
        this.distancia = distancia;
    }

    public int getTempoEstimado() {
        return tempoEstimado;
    }

    public void setTempoEstimado(int tempoEstimado) {
        this.tempoEstimado = tempoEstimado;
    }
}