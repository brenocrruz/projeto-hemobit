package com.hemobit.hemobit.dto;

import com.hemobit.hemobit.domain.HistoricoSolicitacao;
import com.hemobit.hemobit.domain.StatusSolicitacao;

import java.time.LocalDateTime;

public class HistoricoSolicitacaoDTO {
    private StatusSolicitacao statusAnterior;
    private StatusSolicitacao statusNovo;
    private LocalDateTime dataHora;
    private String operador;

    public HistoricoSolicitacaoDTO(HistoricoSolicitacao h) {
        this.statusAnterior = h.getStatusAnterior();
        this.statusNovo = h.getStatusNovo();
        this.dataHora = h.getDataHora();
        this.operador = h.getOperador();
    }

    public StatusSolicitacao getStatusAnterior() {
        return statusAnterior;
    }

    public StatusSolicitacao getStatusNovo() {
        return statusNovo;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public String getOperador() {
        return operador;
    }
}
