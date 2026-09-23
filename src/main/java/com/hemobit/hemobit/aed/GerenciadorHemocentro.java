package com.hemobit.hemobit.aed;

import com.hemobit.hemobit.aed.estruturas.TabelaHashEstoque;
import com.hemobit.hemobit.aed.estruturas.Grafo;
import com.hemobit.hemobit.domain.Doacao;
import java.util.List;

public class GerenciadorHemocentro {
    
    private TabelaHashEstoque estoque;
    private Grafo mapaRotas;

    public GerenciadorHemocentro() {
        this.estoque = new TabelaHashEstoque();
        this.mapaRotas = new Grafo();
    }

    public void registrarEntrada(Doacao doacao) {
        estoque.adicionar(doacao);
    }

    public Doacao solicitarBolsa(String tipoSanguineo) {
        return estoque.retirar(tipoSanguineo);
    }

    public void adicionarLocalMapa(String nomeLocal) {
        mapaRotas.adicionarVertice(nomeLocal);
    }

    public void adicionarRota(String origem, String destino, double distancia) {
        mapaRotas.adicionarAresta(origem, destino, distancia);
    }

    public List<String> calcularMelhorRota(String origem, String destino) {
        return mapaRotas.encontrarCaminhoMaisCurto(origem, destino);
    }
}