package com.hemobit.hemobit.aed.estruturas;

public class Pilha<T> {
    private Nodo<T> topo;

    public Pilha() {
        this.topo = null;
    }

    public void empilhar(T elemento) {
        Nodo<T> novoNodo = new Nodo<>(elemento);
        novoNodo.setProximo(topo);
        topo = novoNodo;
    }

    public T desempilhar() {
        if (topo == null) return null;
        T dado = topo.getDado();
        topo = topo.getProximo();
        return dado;
    }

    public T espiar() {
        if (topo == null) return null;
        return topo.getDado();
    }

    public boolean estaVazia() { return topo == null; }
}