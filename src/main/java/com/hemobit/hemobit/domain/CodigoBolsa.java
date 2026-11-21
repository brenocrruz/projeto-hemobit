package com.hemobit.hemobit.domain;

import java.util.Locale;

public record CodigoBolsa(String valor) {
    public CodigoBolsa {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("Informe o código da bolsa.");
        }
        valor = valor.trim().toUpperCase(Locale.ROOT);
    }

    public static CodigoBolsa de(String texto) {
        return new CodigoBolsa(texto);
    }

    @Override
    public String toString() {
        return valor;
    }
}
