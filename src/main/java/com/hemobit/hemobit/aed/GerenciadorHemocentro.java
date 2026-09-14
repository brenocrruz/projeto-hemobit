package com.hemobit.hemobit.aed;

import com.hemobit.hemobit.aed.estruturas.ListaEncadeada;
import com.hemobit.hemobit.aed.estruturas.Fila;
import com.hemobit.hemobit.aed.estruturas.Pilha;

public class GerenciadorHemocentro {
    
    // Como a equipe de POO ainda está definindo as entidades de Domínio, 
    // usamos Object (genérico) para garantir que já funcione com qualquer classe que eles criarem.
    private ListaEncadeada<Object> estoque;
    private Fila<Object> filaRequisicoes;
    private Pilha<String> historicoOperacoes;

    public GerenciadorHemocentro() {
        this.estoque = new ListaEncadeada<>();
        this.filaRequisicoes = new Fila<>();
        this.historicoOperacoes = new Pilha<>();
    }

    public void adicionarBolsa(Object bolsa) {
        estoque.adicionar(bolsa);
        historicoOperacoes.empilhar("Nova bolsa adicionada ao estoque.");
    }

    public ListaEncadeada<Object> getEstoque() {
        return estoque;
    }

    public void enfileirarRequisicao(Object req) {
        filaRequisicoes.enfileirar(req);
        historicoOperacoes.empilhar("Nova requisição enfileirada.");
    }

    public Object processarProximaRequisicao() {
        Object req = filaRequisicoes.desenfileirar();
        if (req != null) {
            historicoOperacoes.empilhar("Requisição processada com sucesso.");
        }
        return req;
    }

    public String consultarUltimaOperacao() {
        if (historicoOperacoes.estaVazia()) return "Nenhuma operação registrada.";
        return historicoOperacoes.espiar();
    }
}