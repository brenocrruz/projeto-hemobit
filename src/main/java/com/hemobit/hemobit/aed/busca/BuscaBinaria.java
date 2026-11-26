package com.hemobit.hemobit.aed.busca;

public class BuscaBinaria {

    // O vetor precisa estar ordenado
    public static int buscar(int[] vetor, int valor) {
        int inicio = 0;
        int fim = vetor.length - 1;

        while (inicio <= fim) {
            int meio = (inicio + fim) / 2;

            if (vetor[meio] == valor) {
                return meio;
            } else if (vetor[meio] < valor) {
                inicio = meio + 1;
            } else {
                fim = meio - 1;
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        int[] vetor = {4, 8, 15, 16, 23, 42};
        int valorProcurado = 23;
        
        int posicao = buscar(vetor, valorProcurado);
        if (posicao != -1) {
            System.out.println("Valor " + valorProcurado + " encontrado na posicao: " + posicao);
        } else {
            System.out.println("Valor " + valorProcurado + " nao encontrado.");
        }
    }
}