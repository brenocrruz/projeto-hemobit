package com.hemobit.hemobit.aed.estruturas;

public class Fila<T> {
    private Nodo<T> frente;
    private Nodo<T> tras;

    public Fila() {
        this.frente = null;
        this.tras = null;
    }

    public void enfileirar(T elemento) {
        Nodo<T> novoNodo = new Nodo<>(elemento);
        if (tras != null) {
            tras.setProximo(novoNodo);
        }
        tras = novoNodo;
        if (frente == null) {
            frente = novoNodo;
        }
    }

    public T desenfileirar() {
        if (frente == null) return null;
        T dado = frente.getDado();
        frente = frente.getProximo();
        if (frente == null) tras = null;
        return dado;
    }

    public boolean estaVazia() { return frente == null; }
}