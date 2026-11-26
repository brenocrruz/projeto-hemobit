# Tradução Comentada: Busca Binária (C vs Java)

A busca binária divide a lista ordenada ao meio sucessivamente até
encontrar o valor desejado ou concluir que ele não está presente. **O
vetor precisa estar ordenado** para que o algoritmo funcione
corretamente.

## 1. Código em C

``` c
int buscaBinaria(int vetor[], int tamanho, int valor) {
    int inicio = 0;
    int fim = tamanho - 1;
    int meio;

    while (inicio <= fim) {
        meio = (inicio + fim) / 2;

        if (vetor[meio] == valor) {
            return meio;
        } else if (vetor[meio] < valor) {
            inicio = meio + 1;
        } else {
            fim = meio - 1;
        }
    }

    return -1;
}
```

## 2. Código em Java

``` java
public static int buscar(int[] vetor, int valor) {
    int inicio = 0;
    int fim = vetor.length - 1;

    while (inicio <= fim) {
        int meio = (inicio + fim) / 2;

        if (vetor[meio] == valor) {
            return meio;
        } else if (vetor[meio] < valor) {
            inicio = meio + 1;
        } else {
            fim = meio - 1;
        }
    }

    return -1;
}
```

## 3. Análise da Tradução

-   **Variáveis locais:** Em C, as variáveis `inicio`, `fim` e `meio`
    são declaradas no início da função. Em Java, podemos declarar
    `inicio` e `fim` no início e `meio` dentro do laço `while`, mantendo
    a mesma lógica.
-   **Acesso ao tamanho:** A diferença é o uso de `tamanho - 1` em C e
    `vetor.length - 1` em Java para definir a última posição do vetor.
-   **Divisão inteira:** A expressão `(inicio + fim) / 2` funciona de
    maneira equivalente nas duas linguagens quando os operandos são
    inteiros: a parte decimal é descartada.
-   **Condicionais:** A estrutura `if / else if / else` foi traduzida
    diretamente, preservando as decisões do algoritmo.
-   **Pré-requisito:** O vetor deve estar ordenado em ordem crescente.
    Sem essa condição, a busca binária pode produzir um resultado
    incorreto.

## 4. Complexidade

-   **Tempo:** `O(log n)` no pior caso, pois a cada iteração o espaço de
    busca é dividido pela metade.
-   **Espaço:** `O(1)`, pois a versão iterativa utiliza apenas uma
    quantidade constante de memória extra.
