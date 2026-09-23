package com.hemobit.hemobit.aed.estruturas;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class Grafo {
    
    private Map<String, Vertice> vertices;

    public Grafo() {
        this.vertices = new HashMap<>();
    }

    public void adicionarVertice(String nomeLocal) {
        vertices.putIfAbsent(nomeLocal, new Vertice(nomeLocal));
    }

    public void adicionarAresta(String origem, String destino, double distancia) {
        Vertice vOrigem = vertices.get(origem);
        Vertice vDestino = vertices.get(destino);

        if (vOrigem != null && vDestino != null) {
            vOrigem.adicionarAresta(vDestino, distancia);
            vDestino.adicionarAresta(vOrigem, distancia); 
        }
    }

    private class NodoDistancia implements Comparable<NodoDistancia> {
        Vertice vertice;
        double distancia;

        NodoDistancia(Vertice v, double d) {
            this.vertice = v;
            this.distancia = d;
        }

        @Override
        public int compareTo(NodoDistancia outro) {
            return Double.compare(this.distancia, outro.distancia);
        }
    }

    public List<String> encontrarCaminhoMaisCurto(String nomeOrigem, String nomeDestino) {
        Vertice origem = vertices.get(nomeOrigem);
        Vertice destino = vertices.get(nomeDestino);
        
        if (origem == null || destino == null) return new ArrayList<>();

        Map<Vertice, Double> distancias = new HashMap<>();
        Map<Vertice, Vertice> anteriores = new HashMap<>();
        PriorityQueue<NodoDistancia> fila = new PriorityQueue<>();

        for (Vertice v : vertices.values()) {
            distancias.put(v, Double.MAX_VALUE);
        }
        
        distancias.put(origem, 0.0);
        fila.add(new NodoDistancia(origem, 0.0));

        while (!fila.isEmpty()) {
            Vertice atual = fila.poll().vertice;

            if (atual == destino) break;

            for (Aresta aresta : atual.getArestas()) {
                Vertice vizinho = aresta.getDestino();
                double novaDistancia = distancias.get(atual) + aresta.getDistancia();

                if (novaDistancia < distancias.get(vizinho)) {
                    distancias.put(vizinho, novaDistancia);
                    anteriores.put(vizinho, atual);
                    fila.add(new NodoDistancia(vizinho, novaDistancia));
                }
            }
        }

        List<String> caminho = new ArrayList<>();
        Vertice passo = destino;
        
        if (anteriores.get(passo) == null && passo != origem) {
            return caminho; 
        }
        
        while (passo != null) {
            caminho.add(passo.getNomeLocal());
            passo = anteriores.get(passo);
        }
        
        Collections.reverse(caminho);
        
        return caminho;
    }
}