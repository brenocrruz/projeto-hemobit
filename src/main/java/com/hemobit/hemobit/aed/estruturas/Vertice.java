package com.hemobit.hemobit.aed.estruturas;

import java.util.ArrayList;
import java.util.List;

public class Vertice {
    private String nomeLocal; 
    private List<Aresta> arestas;

    public Vertice(String nomeLocal) {
        this.nomeLocal = nomeLocal;
        this.arestas = new ArrayList<>();
    }

    // Método para ligar este local a outro
    public void adicionarAresta(Vertice destino, double distancia) {
        this.arestas.add(new Aresta(destino, distancia));
    }

    public String getNomeLocal() {
        return nomeLocal;
    }

    public List<Aresta> getArestas() {
        return arestas;
    }
}