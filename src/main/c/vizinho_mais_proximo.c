#include <stdio.h>

#define N 4 // Número de locais (0 = Hemocentro, 1, 2, 3 = Hospitais)

// Matriz de distâncias entre os pontos
int distancias[N][N] = {
    {0, 10, 15, 20}, // Do ponto 0 para 0, 1, 2, 3
    {10, 0, 35, 25}, // Do ponto 1 para 0, 1, 2, 3
    {15, 35, 0, 30}, // Do ponto 2 para 0, 1, 2, 3
    {20, 25, 30, 0}  // Do ponto 3 para 0, 1, 2, 3
};

void calcularRota(int inicio) {
    int visitados[N] = {0};
    int atual = inicio;
    int totalDistancia = 0;
    
    printf("Rota de entrega (Vizinho Mais Proximo):\n");
    printf("Inicio no ponto %d\n", atual);
    visitados[atual] = 1; // Marca o início como visitado

    // Vai visitar todos os outros N-1 pontos
    for (int passo = 1; passo < N; passo++) {
        int proximo = -1;
        int menorDistancia = 9999;

        // Olha para todos os vizinhos
        for (int i = 0; i < N; i++) {
            // Se não foi visitado e a distância é menor que a atual
            if (!visitados[i] && distancias[atual][i] < menorDistancia) {
                menorDistancia = distancias[atual][i];
                proximo = i;
            }
        }

        // Vai para o vizinho mais próximo encontrado
        if (proximo != -1) {
            printf("-> Ponto %d (Distancia percorrida: %d)\n", proximo, menorDistancia);
            totalDistancia += menorDistancia;
            visitados[proximo] = 1; // Marca como visitado
            atual = proximo; // Atualiza a posição atual
        }
    }
    
    printf("Distancia total da rota: %d\n", totalDistancia);
}

int main() {
    calcularRota(0); // Iniciando no Hemocentro (ponto 0)
    return 0;
}