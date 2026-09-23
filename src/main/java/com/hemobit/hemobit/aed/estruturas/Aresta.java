package com.hemobit.hemobit.aed.estruturas;

public class Aresta {
    private Vertice destino;
    private double distancia;

    public Aresta(Vertice destino, double distancia) {
        this.destino = destino;
        this.distancia = distancia;
    }

    public Vertice getDestino() {
        return destino;
    }

    public double getDistancia() {
        return distancia;
    }
}