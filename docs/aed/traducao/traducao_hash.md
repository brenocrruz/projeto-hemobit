# Tradução Comentada: Busca por Hash com Sondagem Linear (C vs Java)

## 1. Código em C (trecho de inserção e busca)

```c
#define TAM 10
#define VAZIO -1
#define REMOVIDO -2

int tabelaHash[TAM];

int hash(int chave) { 
    return chave % TAM; 
}

void inserir(int chave) {
    int indice = hash(chave);
    for (int i = 0; i < TAM; i++) {
        int tentativa = (indice + i) % TAM;
        if (tabelaHash[tentativa] == VAZIO || tabelaHash[tentativa] == REMOVIDO) {
            tabelaHash[tentativa] = chave;
            return;
        }
    }
}
```

## 2. Código em Java (trecho de inserção e busca)

```java
private static final int TAM = 10;
private static final int VAZIO = -1;
private static final int REMOVIDO = -2;

private int[] tabela;

public HashLinear() {
    tabela = new int[TAM];
    for (int i = 0; i < TAM; i++) {
        tabela[i] = VAZIO;
    }
}

private int hash(int chave) { 
    return chave % TAM; 
}

public void inserir(int chave) {
    int indice = hash(chave);
    for (int i = 0; i < TAM; i++) {
        int tentativa = (indice + i) % TAM;
        if (tabela[tentativa] == VAZIO || tabela[tentativa] == REMOVIDO) {
            tabela[tentativa] = chave;
            return;
        }
    }
}
```

## 3. Análise da Tradução

* **Alocação de Memória:** Em C, o vetor `int tabelaHash[TAM]` é alocado estaticamente na memória global. Em Java, a tabela é um atributo de instância (`int[] tabela`) alocado dinamicamente no construtor da classe com a palavra-chave `new`.
* **Constantes:** O `#define` do C foi traduzido para `private static final int` em Java. Isso é uma boa prática em Java para definir constantes de classe.
* **Inicialização:** Em C, a inicialização da tabela com `VAZIO` precisa ser feita explicitamente por um laço (`for`). Em Java, o array é inicializado com `0` por padrão, então também precisamos do laço para preencher com `-1`, mantendo a semântica idêntica.
* **Estado Global vs Instância:** O C usa variáveis globais (ou estáticas) para a tabela. O Java encapsula a tabela dentro de um objeto (`HashLinear`), o que é o pilar do encapsulamento da POO.

## 4. Complexidade

* **Inserção:** $O(1)$ no caso médio (sem colisão). $O(n)$ no pior caso (tabela cheia ou muitas colisões).
* **Busca:** $O(1)$ no caso médio. $O(n)$ no pior caso.
* **Espaço:** $O(n)$ para armazenar os $n$ elementos na tabela.