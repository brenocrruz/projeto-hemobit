# Tradução Comentada: Busca Sequencial (C vs Java)

O algoritmo de busca sequencial percorre um vetor elemento por elemento
até encontrar a chave desejada ou chegar ao final.

## 1. Código em C

``` c
int buscaSequencial(int vetor[], int tamanho, int valor) {
    for (int i = 0; i < tamanho; i++) {
        if (vetor[i] == valor) {
            return i;
        }
    }

    return -1;
}
```

## 2. Código em Java

``` java
public static int buscar(int[] vetor, int valor) {
    for (int i = 0; i < vetor.length; i++) {
        if (vetor[i] == valor) {
            return i;
        }
    }

    return -1;
}
```

## 3. Análise da Tradução

-   **Assinatura do método:** No C, precisamos passar o tamanho do vetor
    explicitamente (`int tamanho`), porque a linguagem não armazena essa
    informação de forma acessível no próprio array. No Java, o array
    possui o atributo `.length`, então não precisamos passar o tamanho
    como parâmetro.
-   **Arrays:** O `int vetor[]` do C foi traduzido para `int[] vetor` no
    Java. Em C, o nome do array geralmente é convertido em um ponteiro
    para o primeiro elemento em expressões. Em Java, a variável contém
    uma referência para um objeto array.
-   **Gerenciamento de memória e limites:** C não faz verificação
    automática dos limites do array. Acessar uma posição fora dele causa
    comportamento indefinido. Em Java, esse acesso lança uma exceção
    `ArrayIndexOutOfBoundsException`.
-   **Laço de repetição:** O `for`, a estrutura condicional `if` e o
    retorno com `return -1` funcionam de maneira equivalente nas duas
    linguagens.

## 4. Complexidade

-   **Tempo:** `O(n)` no pior caso, quando o elemento está na última
    posição ou não existe no vetor.
-   **Espaço:** `O(1)`, pois o algoritmo utiliza apenas uma quantidade
    constante de memória extra.
