package com.hemobit.hemobit.domain;

// Resumo: situações possíveis de uma solicitação (HU07).
// Explicando fácil: o pedido nasce PENDENTE, passa por EM_SEPARACAO e termina em
// ATENDIDA, CANCELADA ou NEGADA. Os três últimos são finais: depois deles o pedido não muda mais.
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
