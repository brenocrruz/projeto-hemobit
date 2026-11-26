package com.hemobit.hemobit.aed.ordenacao;

public class InsertionSort {

    public static void ordenar(int[] vetor) {
        int n = vetor.length;
        for (int i = 1; i < n; i++) {
            int chave = vetor[i];
            int j = i - 1;

            while (j >= 0 && vetor[j] > chave) {
                vetor[j + 1] = vetor[j];
                j = j - 1;
            }
            vetor[j + 1] = chave;
        }
    }

    public static void main(String[] args) {
        int[] vetor = {12, 11, 13, 5, 6};
        
        System.out.print("Vetor original: ");
        imprimir(vetor);
        
        ordenar(vetor);
        
        System.out.print("Vetor ordenado (Insertion Sort): ");
        imprimir(vetor);
    }

    public static void imprimir(int[] vetor) {
        for (int i = 0; i < vetor.length; i++) {
            System.out.print(vetor[i] + " ");
        }
        System.out.println();
    }
}