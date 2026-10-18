package com.hemobit.hemobit.domain;

public enum StatusSolicitacao {
    PENDENTE,
    EM_SEPARACAO,
    ATENDIDA,
    CANCELADA,
    NEGADA;

    public boolean isFinalizada() {
        return this == ATENDIDA || this == CANCELADA || this == NEGADA;
    }
}
