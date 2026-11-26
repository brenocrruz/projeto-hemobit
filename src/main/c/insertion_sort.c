#include <stdio.h>

void imprimirVetor(int vetor[], int tamanho) {
    for (int i = 0; i < tamanho; i++) {
        printf("%d ", vetor[i]);
    }
    printf("\n");
}

void insertionSort(int vetor[], int tamanho) {
    for (int i = 1; i < tamanho; i++) {
        int chave = vetor[i];
        int j = i - 1;
        
        // Move os elementos maiores que a chave para uma posição à frente
        while (j >= 0 && vetor[j] > chave) {
            vetor[j + 1] = vetor[j];
            j = j - 1;
        }
        vetor[j + 1] = chave;
    }
}

int main() {
    int vetor[] = {12, 11, 13, 5, 6};
    int tamanho = sizeof(vetor) / sizeof(vetor[0]);

    printf("Vetor original: ");
    imprimirVetor(vetor, tamanho);

    insertionSort(vetor, tamanho);

    printf("Vetor ordenado (Insertion Sort): ");
    imprimirVetor(vetor, tamanho);

    return 0;
}