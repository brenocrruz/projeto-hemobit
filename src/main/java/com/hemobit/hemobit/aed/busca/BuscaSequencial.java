package com.hemobit.hemobit.aed.busca;

public class BuscaSequencial {
    
    // Retorna o índice do valor ou -1 se não encontrar
    public static int buscar(int[] vetor, int valor) {
        for (int i = 0; i < vetor.length; i++) {
            if (vetor[i] == valor) {
                return i;
            }
        }
        return -1;
    }

    // Método main apenas para testar a classe isoladamente
    public static void main(String[] args) {
        int[] vetor = {30, 15, 20, 55, 8, 10};
        int valorProcurado = 55;
        
        int posicao = buscar(vetor, valorProcurado);
        if (posicao != -1) {
            System.out.println("Valor " + valorProcurado + " encontrado na posicao: " + posicao);
        } else {
            System.out.println("Valor " + valorProcurado + " nao encontrado.");
        }
    }
}