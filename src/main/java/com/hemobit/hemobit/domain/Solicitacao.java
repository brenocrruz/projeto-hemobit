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
import java.time.LocalDateTime;

@Entity
@Table(name = "solicitacao")
public class Solicitacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "unidade_saude_id", nullable = false)
    private UnidadeSaude unidadeSaude;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoHemocomponente tipoHemocomponente;

    @Column(nullable = false)
    private double quantidade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusSolicitacao status;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    public Solicitacao() {
    }

    public Solicitacao(UnidadeSaude unidadeSaude, TipoHemocomponente tipoHemocomponente, double quantidade) {
        this.unidadeSaude = unidadeSaude;
        this.tipoHemocomponente = tipoHemocomponente;
        this.quantidade = quantidade;
        this.status = StatusSolicitacao.PENDENTE;
        this.dataHora = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public UnidadeSaude getUnidadeSaude() {
        return unidadeSaude;
    }

    public void setUnidadeSaude(UnidadeSaude unidadeSaude) {
        this.unidadeSaude = unidadeSaude;
    }

    public TipoHemocomponente getTipoHemocomponente() {
        return tipoHemocomponente;
    }

    public void setTipoHemocomponente(TipoHemocomponente tipoHemocomponente) {
        this.tipoHemocomponente = tipoHemocomponente;
    }

    public double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(double quantidade) {
        this.quantidade = quantidade;
    }

    public StatusSolicitacao getStatus() {
        return status;
    }

    public void setStatus(StatusSolicitacao status) {
        this.status = status;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }
    @Override
    public String toString() {
        return "Solicitacao{tipo=" + tipoHemocomponente + ", quantidade=" + quantidade + ", status=" + status + "}";
    }
}