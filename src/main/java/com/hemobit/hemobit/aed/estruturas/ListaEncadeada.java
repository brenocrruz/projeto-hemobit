package com.hemobit.hemobit.aed.estruturas;

public class ListaEncadeada<T> {
    private Nodo<T> inicio;
    private Nodo<T> fim;
    private int tamanho;

    public ListaEncadeada() {
        this.inicio = null;
        this.fim = null;
        this.tamanho = 0;
    }

    public void adicionar(T elemento) {
        Nodo<T> novoNodo = new Nodo<>(elemento);
        if (inicio == null) {
            inicio = novoNodo;
        } else {
            fim.setProximo(novoNodo);
        }
        fim = novoNodo;
        tamanho++;
    }

    public T obter(int index) {
        if (index < 0 || index >= tamanho) {
            throw new IndexOutOfBoundsException("Índice inválido.");
        }
        Nodo<T> atual = inicio;
        for (int i = 0; i < index; i++) {
            atual = atual.getProximo();
        }
        return atual.getDado();
    }

    public int tamanho() { return tamanho; }
}