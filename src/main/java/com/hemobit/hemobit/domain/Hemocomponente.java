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

    @Column(unique = true, length = 40)
    private String codigo;

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

    @ManyToOne
    @JoinColumn(name = "solicitacao_id")
    private Solicitacao solicitacao;

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

    public static Hemocomponente registrar(String codigo, TipoHemocomponente tipo, double quantidade,
                                           LocalDate dataProducao, LocalDate dataValidade, Doacao doacao,
                                           Localidade localizacaoAtual, LocalDate hoje) {
        String codigoNormalizado = normalizarCodigo(codigo);
        validarDados(tipo, quantidade, dataProducao, dataValidade);
        if (dataValidade.isBefore(hoje)) {
            throw new IllegalArgumentException("Não é permitido cadastrar bolsas com data de validade vencida!");
        }
        if (doacao == null) {
            throw new IllegalArgumentException("Informe a doação de origem da bolsa.");
        }
        Hemocomponente bolsa = new Hemocomponente(tipo, quantidade, dataProducao, dataValidade, doacao,
                localizacaoAtual);
        bolsa.codigo = codigoNormalizado;
        return bolsa;
    }

    public static String normalizarCodigo(String codigo) {
        return CodigoBolsa.de(codigo).valor();
    }

    public void atribuirCodigo(String novoCodigo) {
        this.codigo = normalizarCodigo(novoCodigo);
    }

    public void corrigirDados(TipoHemocomponente novoTipo, double novaQuantidade, LocalDate novaProducao,
                              LocalDate novaValidade, LocalDate hoje) {
        validarDados(novoTipo, novaQuantidade, novaProducao, novaValidade);
        if (!novaValidade.equals(this.dataValidade) && novaValidade.isBefore(hoje)) {
            throw new IllegalArgumentException("Não é permitido cadastrar bolsas com data de validade vencida!");
        }
        this.tipo = novoTipo;
        this.quantidade = novaQuantidade;
        this.dataProducao = novaProducao;
        this.dataValidade = novaValidade;
    }

    public boolean estaVencida(LocalDate hoje) {
        return dataValidade.isBefore(hoje);
    }

    public boolean estaDisponivel(LocalDate hoje) {
        return status == StatusHemocomponente.DISPONIVEL && !estaVencida(hoje);
    }

    public void reservarPara(Solicitacao solicitacao, LocalDate hoje) {
        if (solicitacao == null) {
            throw new IllegalArgumentException("Informe a solicitação que reserva a bolsa.");
        }
        if (!estaDisponivel(hoje)) {
            throw new IllegalStateException("A bolsa " + codigo + " não está disponível para reserva.");
        }
        this.status = StatusHemocomponente.RESERVADO;
        this.solicitacao = solicitacao;
    }

    public void marcarComoVencida(LocalDate hoje) {
        if (status != StatusHemocomponente.DISPONIVEL) {
            throw new IllegalStateException("Só bolsas disponíveis podem ser marcadas como vencidas.");
        }
        if (!estaVencida(hoje)) {
            throw new IllegalStateException("A bolsa " + codigo + " ainda está dentro da validade.");
        }
        this.status = StatusHemocomponente.VENCIDO;
    }

    private static void validarDados(TipoHemocomponente tipo, double quantidade, LocalDate dataProducao,
                                     LocalDate dataValidade) {
        if (tipo == null) {
            throw new IllegalArgumentException("Informe o tipo do hemocomponente.");
        }
        PeriodoDeValidade.de(dataProducao, dataValidade);
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero.");
        }
    }

    public Long getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public TipoSanguineo getTipoSanguineo() {
        if (doacao == null || doacao.getDoador() == null) {
            return null;
        }
        return doacao.getDoador().getTipoSanguineo();
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

    public Doacao getDoacao() {
        return doacao;
    }

    public Localidade getLocalizacaoAtual() {
        return localizacaoAtual;
    }

    public Solicitacao getSolicitacao() {
        return solicitacao;
    }

    @Override
    public String toString() {
        return "Hemocomponente{" +
                "id=" + id +
                ", codigo=" + codigo +
                ", tipo=" + tipo +
                ", quantidade=" + quantidade +
                ", dataProducao=" + dataProducao +
                ", dataValidade=" + dataValidade +
                ", status=" + status +
                '}';
    }
}
