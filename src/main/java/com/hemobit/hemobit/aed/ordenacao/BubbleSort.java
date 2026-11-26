package com.hemobit.hemobit.aed.ordenacao;

public class BubbleSort {

    public static void ordenar(int[] vetor) {
        int n = vetor.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - 1 - i; j++) {
                if (vetor[j] > vetor[j + 1]) {
                    // Troca
                    int temp = vetor[j];
                    vetor[j] = vetor[j + 1];
                    vetor[j + 1] = temp;
                }
            }
        }
    }

    public static void main(String[] args) {
        int[] vetor = {64, 34, 25, 12, 22, 11, 90};
        
        System.out.print("Vetor original: ");
        imprimir(vetor);
        
        ordenar(vetor);
        
        System.out.print("Vetor ordenado (Bubble Sort): ");
        imprimir(vetor);
    }

    public static void imprimir(int[] vetor) {
        for (int i = 0; i < vetor.length; i++) {
            System.out.print(vetor[i] + " ");
        }
        System.out.println();
    }
}