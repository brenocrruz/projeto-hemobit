# Tradução Comentada: Selection Sort (C vs Java)

## 1. Código em C

```c
void selectionSort(int vetor[], int tamanho) {
    for (int i = 0; i < tamanho - 1; i++) {
        int indiceMinimo = i;
        for (int j = i + 1; j < tamanho; j++) {
            if (vetor[j] < vetor[indiceMinimo]) {
                indiceMinimo = j;
            }
        }
        if (indiceMinimo != i) {
            int temp = vetor[i];
            vetor[i] = vetor[indiceMinimo];
            vetor[indiceMinimo] = temp;
        }
    }
}
```

## 2. Código em Java

```java
public static void ordenar(int[] vetor) {
    int n = vetor.length;
    for (int i = 0; i < n - 1; i++) {
        int indiceMinimo = i;
        for (int j = i + 1; j < n; j++) {
            if (vetor[j] < vetor[indiceMinimo]) {
                indiceMinimo = j;
            }
        }
        if (indiceMinimo != i) {
            int temp = vetor[i];
            vetor[i] = vetor[indiceMinimo];
            vetor[indiceMinimo] = temp;
        }
    }
}
```

## 3. Análise da Tradução

* **Lógica:** A lógica de busca do menor elemento e troca é idêntica. O C usa o parâmetro `tamanho`, enquanto o Java usa `vetor.length`.

* **Controle de Fluxo:** A estrutura de laços aninhados e a condicional `if (indiceMinimo != i)` para evitar trocas desnecessárias foram mantidas exatamente.

## 4. Complexidade

* **Tempo:** $O(n^2)$ em todos os casos (sempre faz duas varreduras).

* **Espaço:** $O(1)$ (*in-place*).