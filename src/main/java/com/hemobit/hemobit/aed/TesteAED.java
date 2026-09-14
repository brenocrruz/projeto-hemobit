package com.hemobit.hemobit.aed;

public class TesteAED {
    public static void main(String[] args) {
        GerenciadorHemocentro gerenciador = new GerenciadorHemocentro();

        System.out.println("--- TESTANDO ESTOQUE (LISTA) ---");
        gerenciador.adicionarBolsa("Bolsa O- (450ml)");
        gerenciador.adicionarBolsa("Bolsa A+ (420ml)");
        System.out.println("Tamanho do estoque: " + gerenciador.getEstoque().tamanho());

        System.out.println("\n--- TESTANDO REQUISIÇÕES (FILA - FIFO) ---");
        gerenciador.enfileirarRequisicao("REQ-01 (Hospital das Clínicas)");
        gerenciador.enfileirarRequisicao("REQ-02 (Santa Casa)");
        System.out.println("Atendendo primeiro: " + gerenciador.processarProximaRequisicao());
        System.out.println("Atendendo segundo: " + gerenciador.processarProximaRequisicao());

        System.out.println("\n--- TESTANDO HISTÓRICO (PILHA - LIFO) ---");
        System.out.println("Última operação registrada: " + gerenciador.consultarUltimaOperacao());
    }
}