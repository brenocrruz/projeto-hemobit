package com.hemobit.hemobit.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StatusSolicitacaoTest {
    @Test
    void pendentePodeIrParaSeparacaoCancelamentoOuNegativa() {
        assertTrue(StatusSolicitacao.PENDENTE.podeIrPara(StatusSolicitacao.EM_SEPARACAO));
        assertTrue(StatusSolicitacao.PENDENTE.podeIrPara(StatusSolicitacao.CANCELADA));
        assertTrue(StatusSolicitacao.PENDENTE.podeIrPara(StatusSolicitacao.NEGADA));
        assertFalse(StatusSolicitacao.PENDENTE.podeIrPara(StatusSolicitacao.ATENDIDA));
        assertFalse(StatusSolicitacao.PENDENTE.podeIrPara(StatusSolicitacao.PENDENTE));
    }

    @Test
    void emSeparacaoSoPodeSerCanceladaOuNegada() {
        assertTrue(StatusSolicitacao.EM_SEPARACAO.podeIrPara(StatusSolicitacao.CANCELADA));
        assertTrue(StatusSolicitacao.EM_SEPARACAO.podeIrPara(StatusSolicitacao.NEGADA));
        assertFalse(StatusSolicitacao.EM_SEPARACAO.podeIrPara(StatusSolicitacao.PENDENTE));
        assertFalse(StatusSolicitacao.EM_SEPARACAO.podeIrPara(StatusSolicitacao.ATENDIDA));
    }

    @Test
    void statusFinalizadosNaoTemSaida() {
        for (StatusSolicitacao encerrado : new StatusSolicitacao[] {
                StatusSolicitacao.ATENDIDA, StatusSolicitacao.CANCELADA, StatusSolicitacao.NEGADA}) {
            assertTrue(encerrado.isFinalizada());
            for (StatusSolicitacao destino : StatusSolicitacao.values()) {
                assertFalse(encerrado.podeIrPara(destino));
            }
        }
        assertFalse(StatusSolicitacao.PENDENTE.isFinalizada());
        assertFalse(StatusSolicitacao.EM_SEPARACAO.isFinalizada());
    }
}
