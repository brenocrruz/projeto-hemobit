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
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.BiPredicate;

@Entity
@Table(name = "solicitacao")
public class Solicitacao {
    public static final int QUANTIDADE_MAXIMA_POR_SOLICITACAO = QuantidadeDeBolsas.MAXIMA;

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

    public static Solicitacao abrir(UnidadeSaude unidadeSaude, TipoHemocomponente tipoHemocomponente,
                                    TipoSanguineo tipoSanguineo, double quantidade, Prioridade prioridade) {
        validarPedido(tipoHemocomponente, tipoSanguineo, quantidade, prioridade);
        if (unidadeSaude == null) {
            throw new IllegalArgumentException("Solicitação precisa estar vinculada a uma unidade de saúde.");
        }
        return new Solicitacao(unidadeSaude, tipoHemocomponente, tipoSanguineo, quantidade, prioridade);
    }

    public void alterarPedido(TipoHemocomponente novoTipo, TipoSanguineo novoTipoSanguineo, double novaQuantidade,
                              Prioridade novaPrioridade) {
        if (status != StatusSolicitacao.PENDENTE) {
            throw new IllegalStateException("Só é possível editar solicitações com status PENDENTE.");
        }
        validarPedido(novoTipo, novoTipoSanguineo, novaQuantidade, novaPrioridade);
        this.tipoHemocomponente = novoTipo;
        this.tipoSanguineo = novoTipoSanguineo;
        this.quantidade = novaQuantidade;
        this.prioridade = novaPrioridade;
    }

    public StatusSolicitacao mudarStatus(StatusSolicitacao novoStatus) {
        if (novoStatus == null) {
            throw new IllegalArgumentException("Informe o novo status da solicitação.");
        }
        if (status.isFinalizada()) {
            throw new IllegalStateException("Não é permitido alterar o status de solicitações já finalizadas.");
        }
        if (novoStatus == status) {
            throw new IllegalStateException("A solicitação já está com o status " + status + ".");
        }
        if (!status.podeIrPara(novoStatus)) {
            throw new IllegalStateException("Transição de status inválida: " + status + " -> " + novoStatus + ".");
        }
        StatusSolicitacao anterior = status;
        this.status = novoStatus;
        return anterior;
    }

    public List<Hemocomponente> atender(List<Hemocomponente> candidatas,
                                        BiPredicate<TipoSanguineo, TipoSanguineo> compativel,
                                        LocalDate hoje, LocalDateTime agora) {
        if (status.isFinalizada()) {
            throw new IllegalStateException("Não é permitido alterar o status de solicitações já finalizadas.");
        }
        if (tipoSanguineo == null) {
            throw new IllegalStateException(
                    "Solicitação sem tipo sanguíneo do paciente: edite o pedido antes de atender.");
        }

        int necessarias = (int) quantidade;
        List<Hemocomponente> escolhidas = candidatas.stream()
                .filter(bolsa -> compativel.test(tipoSanguineo, bolsa.getTipoSanguineo()))
                .limit(necessarias)
                .toList();

        if (escolhidas.size() < necessarias) {
            throw new IllegalStateException("Estoque compatível insuficiente: solicitadas " + necessarias
                    + " bolsa(s) de " + tipoHemocomponente
                    + " para paciente " + tipoSanguineo
                    + ", mas há apenas " + escolhidas.size() + " compatível(is) e dentro da validade.");
        }

        escolhidas.forEach(bolsa -> bolsa.reservarPara(this, hoje));
        this.dataAtendimento = agora;
        this.status = StatusSolicitacao.ATENDIDA;
        return escolhidas;
    }

    public boolean estaFinalizada() {
        return status.isFinalizada();
    }

    private static void validarPedido(TipoHemocomponente tipoHemocomponente, TipoSanguineo tipoSanguineo,
                                      double quantidade, Prioridade prioridade) {
        QuantidadeDeBolsas.de(quantidade);
        if (tipoHemocomponente == null) {
            throw new IllegalArgumentException("Informe o tipo de hemocomponente.");
        }
        if (tipoSanguineo == null) {
            throw new IllegalArgumentException("Informe o tipo sanguíneo do paciente.");
        }
        if (prioridade == null) {
            throw new IllegalArgumentException("Informe a prioridade da solicitação.");
        }
    }

    public Long getId() {
        return id;
    }

    public UnidadeSaude getUnidadeSaude() {
        return unidadeSaude;
    }

    public TipoHemocomponente getTipoHemocomponente() {
        return tipoHemocomponente;
    }

    public double getQuantidade() {
        return quantidade;
    }

    public StatusSolicitacao getStatus() {
        return status;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public TipoSanguineo getTipoSanguineo() {
        return tipoSanguineo;
    }

    public Prioridade getPrioridade() {
        return prioridade;
    }

    public LocalDateTime getDataAtendimento() {
        return dataAtendimento;
    }

    @Override
    public String toString() {
        return "Solicitacao{tipo=" + tipoHemocomponente + ", tipoSanguineo=" + tipoSanguineo
                + ", quantidade=" + quantidade + ", prioridade=" + prioridade + ", status=" + status + "}";
    }
}
