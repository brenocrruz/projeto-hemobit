package com.hemobit.hemobit.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.BiPredicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SolicitacaoTest {
    private static final LocalDate HOJE = LocalDate.of(2026, 11, 18);
    private static final LocalDateTime AGORA = LocalDateTime.of(2026, 11, 18, 10, 30);
    private static final BiPredicate<TipoSanguineo, TipoSanguineo> SO_IGUAIS_OU_O_NEGATIVO =
            (paciente, doador) -> paciente == doador || doador == TipoSanguineo.O_NEGATIVO;
    private static final UnidadeSaude HOSPITAL = new UnidadeSaude("Hospital São Marcos", "Rua A", "Recife", "8130000000");

    @Test
    void abrirCriaSolicitacaoPendenteComOsDadosDoPedido() {
        Solicitacao solicitacao = Solicitacao.abrir(HOSPITAL, TipoHemocomponente.CONCENTRADO_HEMACIAS,
                TipoSanguineo.A_POSITIVO, 2, Prioridade.EMERGENCIA);

        assertEquals(StatusSolicitacao.PENDENTE, solicitacao.getStatus());
        assertEquals(2.0, solicitacao.getQuantidade());
        assertEquals(TipoSanguineo.A_POSITIVO, solicitacao.getTipoSanguineo());
        assertEquals(Prioridade.EMERGENCIA, solicitacao.getPrioridade());
        assertSame(HOSPITAL, solicitacao.getUnidadeSaude());
        assertNotNull(solicitacao.getDataHora());
        assertNull(solicitacao.getDataAtendimento());
    }

    @Test
    void abrirRejeitaPedidosInvalidos() {
        assertEquals("A quantidade solicitada deve ser de no mínimo 1 bolsa.", erroAbrir(HOSPITAL,
                TipoHemocomponente.CONCENTRADO_HEMACIAS, TipoSanguineo.A_POSITIVO, 0, Prioridade.ROTINA));
        assertEquals("A quantidade máxima por solicitação é 50.", erroAbrir(HOSPITAL,
                TipoHemocomponente.CONCENTRADO_HEMACIAS, TipoSanguineo.A_POSITIVO, 51, Prioridade.ROTINA));
        assertEquals("A quantidade solicitada deve ser um número inteiro de bolsas.", erroAbrir(HOSPITAL,
                TipoHemocomponente.CONCENTRADO_HEMACIAS, TipoSanguineo.A_POSITIVO, 1.5, Prioridade.ROTINA));
        assertEquals("Informe o tipo de hemocomponente.", erroAbrir(HOSPITAL, null, TipoSanguineo.A_POSITIVO, 1,
                Prioridade.ROTINA));
        assertEquals("Informe o tipo sanguíneo do paciente.", erroAbrir(HOSPITAL,
                TipoHemocomponente.CONCENTRADO_HEMACIAS, null, 1, Prioridade.ROTINA));
        assertEquals("Informe a prioridade da solicitação.", erroAbrir(HOSPITAL,
                TipoHemocomponente.CONCENTRADO_HEMACIAS, TipoSanguineo.A_POSITIVO, 1, null));
        assertEquals("Solicitação precisa estar vinculada a uma unidade de saúde.", erroAbrir(null,
                TipoHemocomponente.CONCENTRADO_HEMACIAS, TipoSanguineo.A_POSITIVO, 1, Prioridade.ROTINA));
    }

    @Test
    void alterarPedidoAtualizaSolicitacaoPendente() {
        Solicitacao solicitacao = pedido(1);

        solicitacao.alterarPedido(TipoHemocomponente.PLASMA_FRESCO_CONGELADO, TipoSanguineo.B_NEGATIVO, 3,
                Prioridade.EMERGENCIA);

        assertEquals(TipoHemocomponente.PLASMA_FRESCO_CONGELADO, solicitacao.getTipoHemocomponente());
        assertEquals(TipoSanguineo.B_NEGATIVO, solicitacao.getTipoSanguineo());
        assertEquals(3.0, solicitacao.getQuantidade());
        assertEquals(Prioridade.EMERGENCIA, solicitacao.getPrioridade());
    }

    @Test
    void alterarPedidoRejeitaSolicitacaoForaDoStatusPendenteEDadosInvalidos() {
        Solicitacao emSeparacao = pedido(1);
        emSeparacao.mudarStatus(StatusSolicitacao.EM_SEPARACAO);
        Solicitacao pendente = pedido(1);

        assertEquals("Só é possível editar solicitações com status PENDENTE.", assertThrows(
                IllegalStateException.class, () -> emSeparacao.alterarPedido(TipoHemocomponente.CONCENTRADO_HEMACIAS,
                        TipoSanguineo.A_POSITIVO, 2, Prioridade.ROTINA)).getMessage());
        assertEquals("A quantidade solicitada deve ser de no mínimo 1 bolsa.", assertThrows(
                IllegalArgumentException.class, () -> pendente.alterarPedido(TipoHemocomponente.CONCENTRADO_HEMACIAS,
                        TipoSanguineo.A_POSITIVO, 0, Prioridade.ROTINA)).getMessage());
        assertEquals(1.0, pendente.getQuantidade());
    }

    @Test
    void mudarStatusRetornaOStatusAnteriorEAplicaOCaminhoPermitido() {
        Solicitacao solicitacao = pedido(1);

        assertEquals(StatusSolicitacao.PENDENTE, solicitacao.mudarStatus(StatusSolicitacao.EM_SEPARACAO));
        assertEquals(StatusSolicitacao.EM_SEPARACAO, solicitacao.mudarStatus(StatusSolicitacao.CANCELADA));
        assertEquals(StatusSolicitacao.CANCELADA, solicitacao.getStatus());
        assertTrue(solicitacao.estaFinalizada());
    }

    @Test
    void mudarStatusRejeitaTransicoesInvalidas() {
        Solicitacao solicitacao = pedido(1);
        solicitacao.mudarStatus(StatusSolicitacao.EM_SEPARACAO);

        assertEquals("A solicitação já está com o status EM_SEPARACAO.", assertThrows(IllegalStateException.class,
                () -> solicitacao.mudarStatus(StatusSolicitacao.EM_SEPARACAO)).getMessage());
        assertEquals("Transição de status inválida: EM_SEPARACAO -> PENDENTE.", assertThrows(
                IllegalStateException.class, () -> solicitacao.mudarStatus(StatusSolicitacao.PENDENTE)).getMessage());
        assertEquals("Transição de status inválida: EM_SEPARACAO -> ATENDIDA.", assertThrows(
                IllegalStateException.class, () -> solicitacao.mudarStatus(StatusSolicitacao.ATENDIDA)).getMessage());
        assertEquals("Informe o novo status da solicitação.", assertThrows(IllegalArgumentException.class,
                () -> solicitacao.mudarStatus(null)).getMessage());
    }

    @Test
    void mudarStatusRejeitaSolicitacaoJaFinalizada() {
        Solicitacao solicitacao = pedido(1);
        solicitacao.mudarStatus(StatusSolicitacao.NEGADA);

        assertEquals("Não é permitido alterar o status de solicitações já finalizadas.", assertThrows(
                IllegalStateException.class, () -> solicitacao.mudarStatus(StatusSolicitacao.CANCELADA)).getMessage());
    }

    @Test
    void atenderReservaAsPrimeirasBolsasCompativeisNaOrdemRecebida() {
        Solicitacao solicitacao = Solicitacao.abrir(HOSPITAL, TipoHemocomponente.CONCENTRADO_HEMACIAS,
                TipoSanguineo.A_POSITIVO, 2, Prioridade.URGENTE);
        Hemocomponente incompativel = bolsa("B1", TipoSanguineo.B_POSITIVO, 3);
        Hemocomponente primeira = bolsa("B2", TipoSanguineo.O_NEGATIVO, 5);
        Hemocomponente segunda = bolsa("B3", TipoSanguineo.A_POSITIVO, 8);
        Hemocomponente sobra = bolsa("B4", TipoSanguineo.A_POSITIVO, 12);

        List<Hemocomponente> reservadas = solicitacao.atender(List.of(incompativel, primeira, segunda, sobra),
                SO_IGUAIS_OU_O_NEGATIVO, HOJE, AGORA);

        assertEquals(List.of(primeira, segunda), reservadas);
        assertEquals(StatusSolicitacao.ATENDIDA, solicitacao.getStatus());
        assertEquals(AGORA, solicitacao.getDataAtendimento());
        assertEquals(StatusHemocomponente.RESERVADO, primeira.getStatus());
        assertSame(solicitacao, segunda.getSolicitacao());
        assertEquals(StatusHemocomponente.DISPONIVEL, incompativel.getStatus());
        assertEquals(StatusHemocomponente.DISPONIVEL, sobra.getStatus());
    }

    @Test
    void atenderComEstoqueInsuficienteNaoAlteraNada() {
        Solicitacao solicitacao = Solicitacao.abrir(HOSPITAL, TipoHemocomponente.CONCENTRADO_HEMACIAS,
                TipoSanguineo.A_POSITIVO, 2, Prioridade.URGENTE);
        Hemocomponente unica = bolsa("B1", TipoSanguineo.A_POSITIVO, 5);

        IllegalStateException erro = assertThrows(IllegalStateException.class,
                () -> solicitacao.atender(List.of(unica), SO_IGUAIS_OU_O_NEGATIVO, HOJE, AGORA));

        assertEquals("Estoque compatível insuficiente: solicitadas 2 bolsa(s) de CONCENTRADO_HEMACIAS para paciente "
                + "A_POSITIVO, mas há apenas 1 compatível(is) e dentro da validade.", erro.getMessage());
        assertEquals(StatusSolicitacao.PENDENTE, solicitacao.getStatus());
        assertNull(solicitacao.getDataAtendimento());
        assertEquals(StatusHemocomponente.DISPONIVEL, unica.getStatus());
        assertNull(unica.getSolicitacao());
    }

    @Test
    void atenderExigeTipoSanguineoDoPacienteESolicitacaoEmAberto() {
        Solicitacao semSangue = new Solicitacao(HOSPITAL, TipoHemocomponente.CONCENTRADO_HEMACIAS, 1);
        Solicitacao finalizada = pedido(1);
        finalizada.mudarStatus(StatusSolicitacao.CANCELADA);

        assertEquals("Solicitação sem tipo sanguíneo do paciente: edite o pedido antes de atender.", assertThrows(
                IllegalStateException.class, () -> semSangue.atender(List.of(), SO_IGUAIS_OU_O_NEGATIVO, HOJE, AGORA))
                .getMessage());
        assertEquals("Não é permitido alterar o status de solicitações já finalizadas.", assertThrows(
                IllegalStateException.class, () -> finalizada.atender(List.of(), SO_IGUAIS_OU_O_NEGATIVO, HOJE, AGORA))
                .getMessage());
    }

    @Test
    void atenderPodePartirDeSolicitacaoEmSeparacao() {
        Solicitacao solicitacao = pedido(1);
        solicitacao.mudarStatus(StatusSolicitacao.EM_SEPARACAO);

        solicitacao.atender(List.of(bolsa("B1", TipoSanguineo.A_POSITIVO, 5)), SO_IGUAIS_OU_O_NEGATIVO, HOJE, AGORA);

        assertEquals(StatusSolicitacao.ATENDIDA, solicitacao.getStatus());
    }

    private String erroAbrir(UnidadeSaude unidade, TipoHemocomponente tipo, TipoSanguineo sangue, double quantidade,
                             Prioridade prioridade) {
        return assertThrows(IllegalArgumentException.class,
                () -> Solicitacao.abrir(unidade, tipo, sangue, quantidade, prioridade)).getMessage();
    }

    private Solicitacao pedido(double quantidade) {
        return Solicitacao.abrir(HOSPITAL, TipoHemocomponente.CONCENTRADO_HEMACIAS, TipoSanguineo.A_POSITIVO,
                quantidade, Prioridade.ROTINA);
    }

    private Hemocomponente bolsa(String codigo, TipoSanguineo sangue, int diasParaVencer) {
        Doador doador = new Doador();
        doador.setTipoSanguineo(sangue);
        Doacao doacao = new Doacao();
        doacao.setDoador(doador);
        return Hemocomponente.registrar(codigo, TipoHemocomponente.CONCENTRADO_HEMACIAS, 300, HOJE.minusDays(5),
                HOJE.plusDays(diasParaVencer), doacao, null, HOJE);
    }
}
