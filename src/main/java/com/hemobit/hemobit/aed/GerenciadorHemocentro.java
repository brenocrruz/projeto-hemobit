package com.hemobit.hemobit.aed;

import com.hemobit.hemobit.aed.estruturas.ListaEncadeada;
import com.hemobit.hemobit.aed.estruturas.Fila;
import com.hemobit.hemobit.aed.estruturas.Pilha;
import com.hemobit.hemobit.domain.Hemocomponente;
import com.hemobit.hemobit.domain.Solicitacao;

public class GerenciadorHemocentro {

    private ListaEncadeada<Hemocomponente> estoque;
    private Fila<Solicitacao> filaRequisicoes;
    private Pilha<String> historicoOperacoes;

    public GerenciadorHemocentro() {
        this.estoque = new ListaEncadeada<>();
        this.filaRequisicoes = new Fila<>();
        this.historicoOperacoes = new Pilha<>();
    }

    public void adicionarBolsa(Hemocomponente bolsa) {
        estoque.adicionar(bolsa);
        historicoOperacoes.empilhar("Nova bolsa adicionada ao estoque.");
    }

    public ListaEncadeada<Hemocomponente> getEstoque() {
        return estoque;
    }

    public void enfileirarRequisicao(Solicitacao req) {
        filaRequisicoes.enfileirar(req);
        historicoOperacoes.empilhar("Nova requisição enfileirada.");
    }

    public Solicitacao processarProximaRequisicao() {
        Solicitacao req = filaRequisicoes.desenfileirar();
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