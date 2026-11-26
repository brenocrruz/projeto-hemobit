# Tradução Comentada: Bubble Sort (C vs Java)

## 1. Código em C

```
void bubbleSort(int vetor[], int tamanho) {
    for (int i = 0; i < tamanho - 1; i++) {
        for (int j = 0; j < tamanho - 1 - i; j++) {
            if (vetor[j] > vetor[j + 1]) {
                int temp = vetor[j];
                vetor[j] = vetor[j + 1];
                vetor[j + 1] = temp;
            }
        }
    }
}

```

## 2. Código em Java

```
public static void ordenar(int[] vetor) {
    for (int i = 0; i < vetor.length - 1; i++) {
        for (int j = 0; j < vetor.length - 1 - i; j++) {
            if (vetor[j] > vetor[j + 1]) {
                int temp = vetor[j];
                vetor[j] = vetor[j + 1];
                vetor[j + 1] = temp;
            }
        }
    }
}

```

## 3. Análise da Tradução

* **Passagem de Parâmetros:** A tradução é quase 1:1. A única mudança estrutural é que o Java não precisa receber o tamanho como parâmetro, pois acessa `vetor.length`.

* **Troca de Valores:** A lógica de troca usando uma variável temporária (`temp`) é idêntica nas duas linguagens, pois ambas manipulam valores primitivos inteiros.

* **Encapsulamento:** Em Java, o método foi colocado como `static` dentro de uma classe utilitária (`BubbleSort`), já que não precisa manter estado.

## 4. Complexidade

* **Tempo:** $O(n^2)$ no pior e médio caso (vetor desordenado). $O(n)$ no melhor caso (vetor já ordenado, com otimização).

* **Espaço:** $O(1)$ (ordenação *in-place*).