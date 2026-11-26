#include <stdio.h>

// Função de Busca Sequencial em C
int buscaSequencial(int vetor[], int tamanho, int valor) {
    for (int i = 0; i < tamanho; i++) {
        if (vetor[i] == valor) {
            return i; // Retorna o índice se encontrar
        }
    }
    return -1; // Retorna -1 se não encontrar
}

int main() {
    // Vetor de exemplo (Slide 4 da Aula 06)
    int vetor[] = {30, 15, 20, 55, 8, 10};
    // Cálculo do tamanho do vetor em C
    int tamanho = sizeof(vetor) / sizeof(vetor[0]);
    int valorProcurado = 55;

    int posicao = buscaSequencial(vetor, tamanho, valorProcurado);

    if (posicao != -1) {
        printf("Valor %d encontrado na posicao: %d\n", valorProcurado, posicao);
    } else {
        printf("Valor %d nao encontrado.\n", valorProcurado);
    }

    return 0;
}