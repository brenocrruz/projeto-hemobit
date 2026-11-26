#include <stdio.h>

// Estrutura que representa uma Bolsa de Sangue
typedef struct {
    int id;
    int diasParaVencer; // Quanto menor, mais urgente
} Bolsa;

// Imprimir o vetor de bolsas
void imprimirBolsas(Bolsa bolsas[], int n) {
    for (int i = 0; i < n; i++) {
        printf("Bolsa ID: %d | Vence em: %d dias\n", bolsas[i].id, bolsas[i].diasParaVencer);
    }
    printf("\n");
}

// Algoritmo FEFO usando a lógica do Insertion Sort
void ordenarFEFO(Bolsa bolsas[], int n) {
    for (int i = 1; i < n; i++) {
        Bolsa chave = bolsas[i];
        int j = i - 1;
        
        // Move as bolsas com validade MAIOR para frente, abrindo espaço para a mais urgente
        while (j >= 0 && bolsas[j].diasParaVencer > chave.diasParaVencer) {
            bolsas[j + 1] = bolsas[j];
            j = j - 1;
        }
        bolsas[j + 1] = chave;
    }
}

int main() {
    // Estoque desordenado
    Bolsa estoque[] = {
        {101, 15}, // Vence em 15 dias
        {102, 5},  // Vence em 5 dias (Mais urgente)
        {103, 30}, // Vence em 30 dias
        {104, 2}   // Vence em 2 dias (O mais urgente de todos)
    };
    int n = sizeof(estoque) / sizeof(estoque[0]);

    printf("--- Estoque ANTES do FEFO ---\n");
    imprimirBolsas(estoque, n);

    ordenarFEFO(estoque, n);

    printf("--- Estoque DEPOIS do FEFO (Priorizado por validade) ---\n");
    imprimirBolsas(estoque, n);
    
    printf("A proxima bolsa a ser usada deve ser a ID: %d\n", estoque[0].id);

    return 0;
}