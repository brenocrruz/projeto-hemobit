package com.hemobit.hemobit.aed.ordenacao;

public class SelectionSort {

    public static void ordenar(int[] vetor) {
        int n = vetor.length;
        for (int i = 0; i < n - 1; i++) {
            int indiceMinimo = i;
            for (int j = i + 1; j < n; j++) {
                if (vetor[j] < vetor[indiceMinimo]) {
                    indiceMinimo = j;
                }
            }
            // Troca o menor encontrado com o atual
            if (indiceMinimo != i) {
                int temp = vetor[i];
                vetor[i] = vetor[indiceMinimo];
                vetor[indiceMinimo] = temp;
            }
        }
    }

    public static void main(String[] args) {
        int[] vetor = {64, 25, 12, 22, 11};
        
        System.out.print("Vetor original: ");
        imprimir(vetor);
        
        ordenar(vetor);
        
        System.out.print("Vetor ordenado (Selection Sort): ");
        imprimir(vetor);
    }

    public static void imprimir(int[] vetor) {
        for (int i = 0; i < vetor.length; i++) {
            System.out.print(vetor[i] + " ");
        }
        System.out.println();
    }
}