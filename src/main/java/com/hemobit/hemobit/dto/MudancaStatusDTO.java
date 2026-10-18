package com.hemobit.hemobit.dto;

import com.hemobit.hemobit.domain.StatusSolicitacao;

public class MudancaStatusDTO {
    private StatusSolicitacao status;
    private String operador;

    public StatusSolicitacao getStatus() {
        return status;
    }

    public void setStatus(StatusSolicitacao status) {
        this.status = status;
    }

    public String getOperador() {
        return operador;
    }

    public void setOperador(String operador) {
        this.operador = operador;
    }
}
