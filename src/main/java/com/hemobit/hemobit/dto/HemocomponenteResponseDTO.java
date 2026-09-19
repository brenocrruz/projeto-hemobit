package com.hemobit.hemobit.dto;

import com.hemobit.hemobit.domain.Hemocomponente;
import com.hemobit.hemobit.domain.StatusHemocomponente;
import com.hemobit.hemobit.domain.TipoHemocomponente;

import java.time.LocalDate;

public class HemocomponenteResponseDTO {

    private Long id;
    private TipoHemocomponente tipo;
    private double quantidade;
    private LocalDate dataProducao;
    private LocalDate dataValidade;
    private StatusHemocomponente status;
    private Long doacaoId;
    private Long localizacaoAtualId;

    public HemocomponenteResponseDTO(Hemocomponente hemocomponente) {
        this.id = hemocomponente.getId();
        this.tipo = hemocomponente.getTipo();
        this.quantidade = hemocomponente.getQuantidade();
        this.dataProducao = hemocomponente.getDataProducao();
        this.dataValidade = hemocomponente.getDataValidade();
        this.status = hemocomponente.getStatus();
        this.doacaoId = hemocomponente.getDoacao() != null ? hemocomponente.getDoacao().getId() : null;
        this.localizacaoAtualId = hemocomponente.getLocalizacaoAtual() != null
                ? hemocomponente.getLocalizacaoAtual().getId() : null;
    }

    public Long getId() {
        return id;
    }

    public TipoHemocomponente getTipo() {
        return tipo;
    }

    public double getQuantidade() {
        return quantidade;
    }

    public LocalDate getDataProducao() {
        return dataProducao;
    }

    public LocalDate getDataValidade() {
        return dataValidade;
    }

    public StatusHemocomponente getStatus() {
        return status;
    }

    public Long getDoacaoId() {
        return doacaoId;
    }

    public Long getLocalizacaoAtualId() {
        return localizacaoAtualId;
    }
}