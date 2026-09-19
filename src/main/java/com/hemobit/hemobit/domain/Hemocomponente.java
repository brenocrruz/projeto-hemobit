package com.hemobit.hemobit.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "hemocomponente")
public class Hemocomponente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoHemocomponente tipo;

    @Column(nullable = false)
    private double quantidade;

    @Column(nullable = false)
    private LocalDate dataProducao;

    @Column(nullable = false)
    private LocalDate dataValidade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusHemocomponente status;

    @ManyToOne
    @JoinColumn(name = "doacao_id", nullable = false)
    private Doacao doacao;

    @ManyToOne
    @JoinColumn(name = "localizacao_atual_id")
    private Localidade localizacaoAtual;

    public Hemocomponente() {
    }

    public Hemocomponente(TipoHemocomponente tipo, double quantidade, LocalDate dataProducao,
                           LocalDate dataValidade, Doacao doacao, Localidade localizacaoAtual) {
        this.tipo = tipo;
        this.quantidade = quantidade;
        this.dataProducao = dataProducao;
        this.dataValidade = dataValidade;
        this.status = StatusHemocomponente.DISPONIVEL;
        this.doacao = doacao;
        this.localizacaoAtual = localizacaoAtual;
    }

    public Long getId() {
        return id;
    }

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

    public StatusHemocomponente getStatus() {
        return status;
    }

    public void setStatus(StatusHemocomponente status) {
        this.status = status;
    }

    public Doacao getDoacao() {
        return doacao;
    }

    public void setDoacao(Doacao doacao) {
        this.doacao = doacao;
    }

    public Localidade getLocalizacaoAtual() {
        return localizacaoAtual;
    }

    public void setLocalizacaoAtual(Localidade localizacaoAtual) {
        this.localizacaoAtual = localizacaoAtual;
    }
}