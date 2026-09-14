package com.hemobit.hemobit.aed.estruturas;

public class Nodo<T> {
    private T dado;
    private Nodo<T> proximo;

    public Nodo(T dado) {
        this.dado = dado;
        this.proximo = null;
    }

    public T getDado() { return dado; }
    public Nodo<T> getProximo() { return proximo; }
    public void setProximo(Nodo<T> proximo) { this.proximo = proximo; }
}