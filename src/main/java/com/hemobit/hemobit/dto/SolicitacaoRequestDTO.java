package com.hemobit.hemobit.dto;

import com.hemobit.hemobit.domain.Prioridade;
import com.hemobit.hemobit.domain.TipoHemocomponente;
import com.hemobit.hemobit.domain.TipoSanguineo;

// Resumo: dados que o cliente envia para criar ou editar uma solicitação (HU06).
// Explicando fácil: o hospital vem só pelo número (unidadeSaudeId); quem transforma isso
// no objeto de verdade é o service.
public class SolicitacaoRequestDTO {

    private Long unidadeSaudeId;
    private TipoHemocomponente tipoHemocomponente;
    private TipoSanguineo tipoSanguineo;
    private double quantidade;
    private Prioridade prioridade;

    public Long getUnidadeSaudeId() {
        return unidadeSaudeId;
    }

    public void setUnidadeSaudeId(Long unidadeSaudeId) {
        this.unidadeSaudeId = unidadeSaudeId;
    }

    public TipoHemocomponente getTipoHemocomponente() {
        return tipoHemocomponente;
    }

    public void setTipoHemocomponente(TipoHemocomponente tipoHemocomponente) {
        this.tipoHemocomponente = tipoHemocomponente;
    }

    public TipoSanguineo getTipoSanguineo() {
        return tipoSanguineo;
    }

    public void setTipoSanguineo(TipoSanguineo tipoSanguineo) {
        this.tipoSanguineo = tipoSanguineo;
    }

    public double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(double quantidade) {
        this.quantidade = quantidade;
    }

    public Prioridade getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(Prioridade prioridade) {
        this.prioridade = prioridade;
    }
}
