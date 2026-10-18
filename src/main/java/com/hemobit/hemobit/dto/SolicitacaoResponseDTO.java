package com.hemobit.hemobit.dto;

import com.hemobit.hemobit.domain.Prioridade;
import com.hemobit.hemobit.domain.Solicitacao;
import com.hemobit.hemobit.domain.StatusSolicitacao;
import com.hemobit.hemobit.domain.TipoHemocomponente;
import com.hemobit.hemobit.domain.TipoSanguineo;

import java.time.LocalDateTime;

// Resumo: o que a API devolve sobre uma solicitação.
// Explicando fácil: o "id" é o código de identificação que a HU06 pede (ex.: 12).
public class SolicitacaoResponseDTO {

    private Long id;
    private Long unidadeSaudeId;
    private String unidadeSaudeNome;
    private TipoHemocomponente tipoHemocomponente;
    private TipoSanguineo tipoSanguineo;
    private double quantidade;
    private Prioridade prioridade;
    private StatusSolicitacao status;
    private LocalDateTime dataHora;
    private LocalDateTime dataAtendimento;

    public SolicitacaoResponseDTO(Solicitacao s) {
        this.id = s.getId();
        this.unidadeSaudeId = s.getUnidadeSaude() != null ? s.getUnidadeSaude().getId() : null;
        this.unidadeSaudeNome = s.getUnidadeSaude() != null ? s.getUnidadeSaude().getNome() : null;
        this.tipoHemocomponente = s.getTipoHemocomponente();
        this.tipoSanguineo = s.getTipoSanguineo();
        this.quantidade = s.getQuantidade();
        this.prioridade = s.getPrioridade();
        this.status = s.getStatus();
        this.dataHora = s.getDataHora();
        this.dataAtendimento = s.getDataAtendimento();
    }

    public Long getId() {
        return id;
    }

    public Long getUnidadeSaudeId() {
        return unidadeSaudeId;
    }

    public String getUnidadeSaudeNome() {
        return unidadeSaudeNome;
    }

    public TipoHemocomponente getTipoHemocomponente() {
        return tipoHemocomponente;
    }

    public TipoSanguineo getTipoSanguineo() {
        return tipoSanguineo;
    }

    public double getQuantidade() {
        return quantidade;
    }

    public Prioridade getPrioridade() {
        return prioridade;
    }

    public StatusSolicitacao getStatus() {
        return status;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public LocalDateTime getDataAtendimento() {
        return dataAtendimento;
    }
}
