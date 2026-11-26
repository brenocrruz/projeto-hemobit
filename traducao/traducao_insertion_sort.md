# Tradução Comentada: Insertion Sort (C vs Java)

## 1. Código em C

```c
void insertionSort(int vetor[], int tamanho) {
    for (int i = 1; i < tamanho; i++) {
        int chave = vetor[i];
        int j = i - 1;
        while (j >= 0 && vetor[j] > chave) {
            vetor[j + 1] = vetor[j];
            j = j - 1;
        }
        vetor[j + 1] = chave;
    }
}
```

## 2. Código em Java

```java
public static void ordenar(int[] vetor) {
    for (int i = 1; i < vetor.length; i++) {
        int chave = vetor[i];
        int j = i - 1;
        while (j >= 0 && vetor[j] > chave) {
            vetor[j + 1] = vetor[j];
            j = j - 1;
        }
        vetor[j + 1] = chave;
    }
}
```

## 3. Análise da Tradução

* **Deslocamento de Elementos:** A operação de deslocar os elementos para a direita (`vetor[j + 1] = vetor[j]`) é idêntica. Em C, isso é feito diretamente na memória. Em Java, é feito através da referência do array, que também é eficiente.

* **Laço `while`:** O uso do laço `while` com a condição composta `j >= 0 && vetor[j] > chave` foi mantido exatamente igual, o que garante a parada correta tanto no início do array quanto ao encontrar a posição de inserção.

* **Aplicação:** Este é o algoritmo usado na nossa classe `Fefo` para ordenar as bolsas pela data de validade.

## 4. Complexidade

* **Tempo:** $O(n^2)$ no pior caso (vetor em ordem decrescente). $O(n)$ no melhor caso (vetor já ordenado).

* **Espaço:** $O(1)$ (*in-place*).