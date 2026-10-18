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

    @Enumerated(EnumType.STRING)
    private TipoSanguineo tipoSanguineo;

    @Enumerated(EnumType.STRING)
    private Prioridade prioridade;

    private LocalDateTime dataAtendimento;

    public Solicitacao() {
    }

    public Solicitacao(UnidadeSaude unidadeSaude, TipoHemocomponente tipoHemocomponente, double quantidade) {
        this.unidadeSaude = unidadeSaude;
        this.tipoHemocomponente = tipoHemocomponente;
        this.quantidade = quantidade;
        this.status = StatusSolicitacao.PENDENTE;
        this.dataHora = LocalDateTime.now();
    }

    public Solicitacao(UnidadeSaude unidadeSaude, TipoHemocomponente tipoHemocomponente,
                       TipoSanguineo tipoSanguineo, double quantidade, Prioridade prioridade) {
        this(unidadeSaude, tipoHemocomponente, quantidade);
        this.tipoSanguineo = tipoSanguineo;
        this.prioridade = prioridade;
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

    public TipoSanguineo getTipoSanguineo() {
        return tipoSanguineo;
    }

    public void setTipoSanguineo(TipoSanguineo tipoSanguineo) {
        this.tipoSanguineo = tipoSanguineo;
    }

    public Prioridade getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(Prioridade prioridade) {
        this.prioridade = prioridade;
    }

    public LocalDateTime getDataAtendimento() {
        return dataAtendimento;
    }

    public void setDataAtendimento(LocalDateTime dataAtendimento) {
        this.dataAtendimento = dataAtendimento;
    }

    @Override
    public String toString() {
        return "Solicitacao{tipo=" + tipoHemocomponente + ", tipoSanguineo=" + tipoSanguineo
                + ", quantidade=" + quantidade + ", prioridade=" + prioridade + ", status=" + status + "}";
    }
}
