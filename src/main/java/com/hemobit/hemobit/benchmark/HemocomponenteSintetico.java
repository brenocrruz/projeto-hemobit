package com.hemobit.hemobit.benchmark;

import java.time.LocalDate;


public class HemocomponenteSintetico {

    private final long id;
    private final String tipo;
    private final LocalDate dataValidade;

    public HemocomponenteSintetico(long id, String tipo, LocalDate dataValidade) {
        this.id = id;
        this.tipo = tipo;
        this.dataValidade = dataValidade;
    }

    public long getId() {
        return id;
    }

    public String getTipo() {
        return tipo;
    }

    public LocalDate getDataValidade() {
        return dataValidade;
    }
}