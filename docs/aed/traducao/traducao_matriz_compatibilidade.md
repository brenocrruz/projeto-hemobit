# Tradução Comentada: Matriz de Compatibilidade ABO/Rh (C vs Java)

## 1. Código em C

```c
int matrizCompatibilidade[8][8] = {
    {1, 1, 0, 0, 0, 0, 1, 1},
    {0, 1, 0, 0, 0, 0, 0, 1},
    // ... demais linhas
};

int isCompativel(int receptor, int doador) {
    return matrizCompatibilidade[receptor][doador];
}
```

## 2. Código em Java

```java
private static final int[][] MATRIZ = {
    {1, 1, 0, 0, 0, 0, 1, 1},
    {0, 1, 0, 0, 0, 0, 0, 1},
    // ... demais linhas
};

public static boolean isCompativel(int indiceReceptor, int indiceDoador) {
    return MATRIZ[indiceReceptor][indiceDoador] == 1;
}
```

## 3. Análise da Tradução

* **Array Bidimensional:** O `int matriz[8][8]` do C foi traduzido para `int[][] MATRIZ` no Java. Em C, a matriz é um bloco contínuo de memória (linha por linha). Em Java, é um array de referências para arrays de inteiros (array de arrays), o que permite linhas de tamanhos diferentes (*jagged array*), mas no nosso caso mantém o formato $8 \times 8$.

* **Booleanos:** Em C, usamos `1` e `0` para representar verdadeiro e falso (já que o C clássico não tem um tipo booleano nativo antes do C99). Em Java, poderíamos usar `boolean[][]`, mas mantivemos `int[][]` e fizemos a conversão com `== 1` para manter a equivalência direta com a lógica de C.

* **Imutabilidade:** Em Java, declaramos a matriz como `final` e `static` para garantir que ela não seja modificada em tempo de execução, mantendo a integridade das regras de compatibilidade.

## 4. Complexidade

* **Tempo:** $O(1)$ (acesso direto a um índice de matriz).

* **Espaço:** $O(1)$ (tamanho fixo de $8 \times 8 = 64$ inteiros).