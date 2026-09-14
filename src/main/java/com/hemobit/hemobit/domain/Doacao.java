package com.hemobit.hemobit.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "doacao")
public class Doacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    @Column(nullable = false)
    private double quantidade;

    @ManyToOne
    @JoinColumn(name = "doador_id", nullable = false)
    private Doador doador;

    @ManyToOne
    @JoinColumn(name = "unidade_coleta_id", nullable = false)
    private UnidadeColeta unidadeColeta;

    public Doacao() {
    }

    public Doacao(LocalDateTime dataHora, double quantidade, Doador doador, UnidadeColeta unidadeColeta) {
        this.dataHora = dataHora;
        this.quantidade = quantidade;
        this.doador = doador;
        this.unidadeColeta = unidadeColeta;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(double quantidade) {
        this.quantidade = quantidade;
    }

    public Doador getDoador() {
        return doador;
    }

    public void setDoador(Doador doador) {
        this.doador = doador;
    }

    public UnidadeColeta getUnidadeColeta() {
        return unidadeColeta;
    }

    public void setUnidadeColeta(UnidadeColeta unidadeColeta) {
        this.unidadeColeta = unidadeColeta;
    }
}