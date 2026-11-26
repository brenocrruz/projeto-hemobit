#include <stdio.h>

void imprimirVetor(int vetor[], int tamanho) {
    for (int i = 0; i < tamanho; i++) {
        printf("%d ", vetor[i]);
    }
    printf("\n");
}

void selectionSort(int vetor[], int tamanho) {
    for (int i = 0; i < tamanho - 1; i++) {
        int indiceMinimo = i;
        // Encontra o menor elemento no restante do vetor
        for (int j = i + 1; j < tamanho; j++) {
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

int main() {
    int vetor[] = {64, 25, 12, 22, 11};
    int tamanho = sizeof(vetor) / sizeof(vetor[0]);

    printf("Vetor original: ");
    imprimirVetor(vetor, tamanho);

    selectionSort(vetor, tamanho);

    printf("Vetor ordenado (Selection Sort): ");
    imprimirVetor(vetor, tamanho);

    return 0;
}