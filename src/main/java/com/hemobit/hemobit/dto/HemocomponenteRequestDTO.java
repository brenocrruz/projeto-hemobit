package com.hemobit.hemobit.dto;

import com.hemobit.hemobit.domain.TipoHemocomponente;

import java.time.LocalDate;

public class HemocomponenteRequestDTO {

    private TipoHemocomponente tipo;
    private double quantidade;
    private LocalDate dataProducao;
    private LocalDate dataValidade;
    private Long doacaoId;
    private Long localizacaoAtualId;

    public TipoHemocomponente getTipo() {
        return tipo;
    }

    public void setTipo(TipoHemocomponente tipo) {
        this.tipo = tipo;
    }

    public double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(double quantidade) {
        this.quantidade = quantidade;
    }

    public LocalDate getDataProducao() {
        return dataProducao;
    }

    public void setDataProducao(LocalDate dataProducao) {
        this.dataProducao = dataProducao;
    }

    public LocalDate getDataValidade() {
        return dataValidade;
    }

    public void setDataValidade(LocalDate dataValidade) {
        this.dataValidade = dataValidade;
    }

    public Long getDoacaoId() {
        return doacaoId;
    }

    public void setDoacaoId(Long doacaoId) {
        this.doacaoId = doacaoId;
    }

    public Long getLocalizacaoAtualId() {
        return localizacaoAtualId;
    }

    public void setLocalizacaoAtualId(Long localizacaoAtualId) {
        this.localizacaoAtualId = localizacaoAtualId;
    }
}