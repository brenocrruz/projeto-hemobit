package com.hemobit.hemobit.aed.sistema;

public class VizinhoMaisProximo {
    
    private static final int N = 4;
    
    // Matriz de distâncias
    private static final int[][] DISTANCIAS = {
        {0, 10, 15, 20},
        {10, 0, 35, 25},
        {15, 35, 0, 30},
        {20, 25, 30, 0}
    };

    public static void calcularRota(int inicio) {
        boolean[] visitados = new boolean[N];
        int atual = inicio;
        int totalDistancia = 0;
        
        System.out.println("Rota de entrega (Vizinho Mais Proximo):");
        System.out.println("Inicio no ponto " + atual);
        visitados[atual] = true;

        for (int passo = 1; passo < N; passo++) {
            int proximo = -1;
            int menorDistancia = 9999;

            for (int i = 0; i < N; i++) {
                if (!visitados[i] && DISTANCIAS[atual][i] < menorDistancia) {
                    menorDistancia = DISTANCIAS[atual][i];
                    proximo = i;
                }
            }

            if (proximo != -1) {
                System.out.println("-> Ponto " + proximo + " (Distancia percorrida: " + menorDistancia + ")");
                totalDistancia += menorDistancia;
                visitados[proximo] = true;
                atual = proximo;
            }
        }
        
        System.out.println("Distancia total da rota: " + totalDistancia);
    }

    public static void main(String[] args) {
        calcularRota(0);
    }
}