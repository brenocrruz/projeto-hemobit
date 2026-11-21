package com.hemobit.hemobit.domain;

public enum StatusSolicitacao {
    PENDENTE,
    EM_SEPARACAO,
    ATENDIDA,
    CANCELADA,
    NEGADA;

    public boolean podeIrPara(StatusSolicitacao novo) {
        return switch (this) {
            case PENDENTE -> novo == EM_SEPARACAO || novo == CANCELADA || novo == NEGADA;
            case EM_SEPARACAO -> novo == CANCELADA || novo == NEGADA;
            default -> false;
        };
    }

    public boolean isFinalizada() {
        return this == ATENDIDA || this == CANCELADA || this == NEGADA;
    }
}
