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
@Table(name = "historico_solicitacao")
public class HistoricoSolicitacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "solicitacao_id", nullable = false)
    private Solicitacao solicitacao;

    @Enumerated(EnumType.STRING)
    private StatusSolicitacao statusAnterior;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusSolicitacao statusNovo;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    @Column(nullable = false)
    private String operador;

    public HistoricoSolicitacao() {
    }

    public HistoricoSolicitacao(Solicitacao solicitacao, StatusSolicitacao statusAnterior,
                                StatusSolicitacao statusNovo, String operador) {
        this.solicitacao = solicitacao;
        this.statusAnterior = statusAnterior;
        this.statusNovo = statusNovo;
        this.operador = operador;
        this.dataHora = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Solicitacao getSolicitacao() {
        return solicitacao;
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
