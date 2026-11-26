# Tradução Comentada: Vizinho Mais Próximo (C vs Java)

## 1. Código em C

```c
int distancias[N][N] = { ... };

void calcularRota(int inicio) {
    int visitados[N] = {0};
    int atual = inicio;
    visitados[atual] = 1;

    for (int passo = 1; passo < N; passo++) {
        int proximo = -1;
        int menorDistancia = 9999;

        for (int i = 0; i < N; i++) {
            if (!visitados[i] && distancias[atual][i] < menorDistancia) {
                menorDistancia = distancias[atual][i];
                proximo = i;
            }
        }

        if (proximo != -1) {
            visitados[proximo] = 1;
            atual = proximo;
        }
    }
}
```

## 2. Código em Java

```java
private static final int[][] DISTANCIAS = { ... };

public static void calcularRota(int inicio) {
    boolean[] visitados = new boolean[N];
    int atual = inicio;
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
            visitados[proximo] = true;
            atual = proximo;
        }
    }
}
```

## 3. Análise da Tradução

* **Vetor de Visitados:** Em C, usamos `int visitados[N] = {0}` (inicializado com zeros). Em Java, usamos `boolean[] visitados` (inicializado com `false` por padrão). A lógica é a mesma, mas o Java é mais expressivo ao usar `boolean` para representar estado lógico.

* **Matriz de Distâncias:** A matriz `int distancias[N][N]` foi traduzida para `int[][] DISTANCIAS`. Mantivemos como `int` e não como `double` porque as distâncias são inteiras no nosso modelo.

* **Variável de Controle:** A variável `atual` guarda o nó atual da rota. Em Java, como estamos lidando com referências, mas a variável é um `int` (índice), o comportamento é idêntico.

* **Critério de Parada:** O laço externo `for (int passo = 1; passo < N; passo++)` garante que todos os $N - 1$ nós restantes sejam visitados. O critério de parada é implícito na condição do laço.

## 4. Complexidade

* **Tempo:** $O(n^2)$ no pior caso (a cada passo, percorre todos os $N$ nós para encontrar o mais próximo).

* **Espaço:** $O(n)$ para o vetor de visitados (além da matriz de distâncias $O(n^2)$ se considerada).