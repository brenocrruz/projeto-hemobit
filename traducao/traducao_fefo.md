# Tradução Comentada: FEFO (C vs Java)

## 1. Código em C

```c
typedef struct {
    int id;
    int diasParaVencer;
} Bolsa;

void ordenarFEFO(Bolsa bolsas[], int n) {
    for (int i = 1; i < n; i++) {
        Bolsa chave = bolsas[i];
        int j = i - 1;
        while (j >= 0 && bolsas[j].diasParaVencer > chave.diasParaVencer) {
            bolsas[j + 1] = bolsas[j];
            j = j - 1;
        }
        bolsas[j + 1] = chave;
    }
}
```

## 2. Código em Java

```java
public static class Bolsa {
    public int id;
    public int diasParaVencer;
    public Hemocomponente hemocomponenteOriginal;
    
    public Bolsa(int id, int diasParaVencer, Hemocomponente original) {
        this.id = id;
        this.diasParaVencer = diasParaVencer;
        this.hemocomponenteOriginal = original;
    }
}

public static void ordenarFEFO(Bolsa[] bolsas) {
    for (int i = 1; i < bolsas.length; i++) {
        Bolsa chave = bolsas[i];
        int j = i - 1;
        while (j >= 0 && bolsas[j].diasParaVencer > chave.diasParaVencer) {
            bolsas[j + 1] = bolsas[j];
            j = j - 1;
        }
        bolsas[j + 1] = chave;
    }
}
```

## 3. Análise da Tradução

* **Estrutura de Dados:** Em C, usamos uma `struct` (`typedef struct { ... } Bolsa;`) para agrupar os dados da bolsa. Em Java, usamos uma classe (`public static class Bolsa`). A diferença fundamental é que a `struct` do C armazena os dados diretamente na memória (por valor), enquanto a classe Java armazena uma referência ao objeto.

* **Atribuição e Cópia:** Quando fazemos `Bolsa chave = bolsas[i]` em Java, estamos copiando a referência do objeto, não o objeto em si. Em C, essa mesma linha copia os valores dos campos da `struct`. Isso é uma diferença crucial de gerenciamento de memória. Na nossa lógica, como apenas movemos as referências, o comportamento final é o mesmo.

* **Atributo Extra:** No Java, adicionamos o campo `hemocomponenteOriginal` para não perdermos a referência à entidade original do banco de dados, o que é uma necessidade do sistema (diferente do exemplo puramente acadêmico em C).

## 4. Complexidade

* **Tempo:** $O(n^2)$ no pior caso (bolsas em ordem crescente de validade). $O(n)$ no melhor caso (bolsas já ordenadas).

* **Espaço:** $O(1)$ (*in-place*).