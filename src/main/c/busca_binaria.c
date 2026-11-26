#include <stdio.h>

// Função de Busca Binária em C
int buscaBinaria(int vetor[], int tamanho, int valor) {
    int inicio = 0;
    int fim = tamanho - 1;
    int meio;

    while (inicio <= fim) {
        meio = (inicio + fim) / 2;

        if (vetor[meio] == valor) {
            return meio; // Valor encontrado
        } else if (vetor[meio] < valor) {
            inicio = meio + 1; // Busca na metade superior
        } else {
            fim = meio - 1; // Busca na metade inferior
        }
    }
    return -1; // Valor não encontrado
}

int main() {
    // O vetor PRECISA estar ordenado para a busca binária funcionar
    int vetor[] = {4, 8, 15, 16, 23, 42};
    int tamanho = sizeof(vetor) / sizeof(vetor[0]);
    int valorProcurado = 23;

    int posicao = buscaBinaria(vetor, tamanho, valorProcurado);

    if (posicao != -1) {
        printf("Valor %d encontrado na posicao: %d\n", valorProcurado, posicao);
    } else {
        printf("Valor %d nao encontrado.\n", valorProcurado);
    }

    return 0;
}