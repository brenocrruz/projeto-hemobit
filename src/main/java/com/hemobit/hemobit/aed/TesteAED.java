package com.hemobit.hemobit.aed;

import com.hemobit.hemobit.domain.Hemocomponente;
import com.hemobit.hemobit.domain.Solicitacao;
import com.hemobit.hemobit.domain.TipoHemocomponente;
import com.hemobit.hemobit.domain.UnidadeSaude;

import java.time.LocalDate;

public class TesteAED {
    public static void main(String[] args) {
        GerenciadorHemocentro gerenciador = new GerenciadorHemocentro();

        System.out.println("--- TESTANDO ESTOQUE (LISTA) ---");
        Hemocomponente bolsa1 = new Hemocomponente(
                TipoHemocomponente.CONCENTRADO_HEMACIAS, 450,
                LocalDate.now(), LocalDate.now().plusDays(35), null, null);
        Hemocomponente bolsa2 = new Hemocomponente(
                TipoHemocomponente.CONCENTRADO_PLAQUETAS, 420,
                LocalDate.now(), LocalDate.now().plusDays(5), null, null);
        gerenciador.adicionarBolsa(bolsa1);
        gerenciador.adicionarBolsa(bolsa2);
        System.out.println("Tamanho do estoque: " + gerenciador.getEstoque().tamanho());

        System.out.println("\n--- TESTANDO REQUISIÇÕES (FILA - FIFO) ---");
        UnidadeSaude hospitalDasClinicas = new UnidadeSaude("Hospital das Clínicas", "", "Recife", "");
        UnidadeSaude santaCasa = new UnidadeSaude("Santa Casa", "", "Recife", "");
        Solicitacao req1 = new Solicitacao(hospitalDasClinicas, TipoHemocomponente.CONCENTRADO_HEMACIAS, 450);
        Solicitacao req2 = new Solicitacao(santaCasa, TipoHemocomponente.CONCENTRADO_PLAQUETAS, 420);
        gerenciador.enfileirarRequisicao(req1);
        gerenciador.enfileirarRequisicao(req2);
        System.out.println("Atendendo primeiro: " + gerenciador.processarProximaRequisicao());
        System.out.println("Atendendo segundo: " + gerenciador.processarProximaRequisicao());

        System.out.println("\n--- TESTANDO HISTÓRICO (PILHA - LIFO) ---");
        System.out.println("Última operação registrada: " + gerenciador.consultarUltimaOperacao());
    }
}