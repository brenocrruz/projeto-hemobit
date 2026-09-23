package com.hemobit.hemobit.aed.estruturas;

import com.hemobit.hemobit.domain.Doacao;

public class FilaFEFO {
    
    private class Nodo {
        Doacao doacao;
        Nodo proximo;
        Nodo(Doacao doacao) { this.doacao = doacao; }
    }

    private Nodo head;

    public void inserir(Doacao doacao) {
        Nodo novo = new Nodo(doacao);

        if (head == null || doacao.getDataHora().isBefore(head.doacao.getDataHora())) {
            novo.proximo = head;
            head = novo;
            return;
        }

        Nodo atual = head;
        while (atual.proximo != null && !doacao.getDataHora().isBefore(atual.proximo.doacao.getDataHora())) {
            atual = atual.proximo;
        }

        novo.proximo = atual.proximo;
        atual.proximo = novo;
    }

    public Doacao remover() {
        if (head == null) return null;
        Doacao removida = head.doacao;
        head = head.proximo;
        return removida;     
    }
}