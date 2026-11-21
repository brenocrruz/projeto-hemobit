package com.hemobit.hemobit.domain;

public record QuantidadeDeBolsas(int valor) {
    public static final int MINIMA = 1;
    public static final int MAXIMA = 50;

    public QuantidadeDeBolsas {
        if (valor < MINIMA) {
            throw new IllegalArgumentException("A quantidade solicitada deve ser de no mínimo 1 bolsa.");
        }
        if (valor > MAXIMA) {
            throw new IllegalArgumentException("A quantidade máxima por solicitação é " + MAXIMA + ".");
        }
    }

    public static QuantidadeDeBolsas de(double quantidade) {
        if (quantidade < MINIMA) {
            throw new IllegalArgumentException("A quantidade solicitada deve ser de no mínimo 1 bolsa.");
        }
        if (quantidade > MAXIMA) {
            throw new IllegalArgumentException("A quantidade máxima por solicitação é " + MAXIMA + ".");
        }
        if (quantidade != Math.floor(quantidade)) {
            throw new IllegalArgumentException("A quantidade solicitada deve ser um número inteiro de bolsas.");
        }
        return new QuantidadeDeBolsas((int) quantidade);
    }
}
