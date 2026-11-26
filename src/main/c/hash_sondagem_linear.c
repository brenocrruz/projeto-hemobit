#include <stdio.h>

#define TAM 10
#define VAZIO -1
#define REMOVIDO -2

int tabelaHash[TAM];

// Inicializa a tabela com VAZIO
void inicializarTabela() {
    for (int i = 0; i < TAM; i++) {
        tabelaHash[i] = VAZIO;
    }
}

// Função hash simples
int hash(int chave) {
    return chave % TAM;
}

// Inserção com sondagem linear
void inserir(int chave) {
    int indice = hash(chave);
    for (int i = 0; i < TAM; i++) {
        int tentativa = (indice + i) % TAM;
        // Se encontrar espaço vazio ou removido, insere aqui
        if (tabelaHash[tentativa] == VAZIO || tabelaHash[tentativa] == REMOVIDO) {
            tabelaHash[tentativa] = chave;
            printf("Inserido %d na posicao %d\n", chave, tentativa);
            return;
        }
    }
    printf("Tabela cheia! Nao foi possivel inserir %d\n", chave);
}

// Busca com sondagem linear
int buscar(int chave) {
    int indice = hash(chave);
    for (int i = 0; i < TAM; i++) {
        int tentativa = (indice + i) % TAM;
        
        // Se encontrar um espaço VAZIO, significa que o elemento nunca foi inserido
        if (tabelaHash[tentativa] == VAZIO) {
            return -1; 
        }
        
        // Se encontrar a chave, retorna o índice
        if (tabelaHash[tentativa] == chave) {
            return tentativa;
        }
    }
    return -1; // Percorreu toda a tabela e não encontrou
}

// Remoção com marcação de REMOVIDO (para não quebrar a busca)
int remover(int chave) {
    int posicao = buscar(chave);
    if (posicao != -1) {
        tabelaHash[posicao] = REMOVIDO;
        printf("Valor %d removido da posicao %d\n", chave, posicao);
        return 1;
    }
    return 0; // Não encontrado
}

// Função para imprimir a tabela (apenas para visualização)
void imprimirTabela() {
    printf("\nTabela Hash:\n");
    for (int i = 0; i < TAM; i++) {
        if (tabelaHash[i] == VAZIO) printf("[%d]: VAZIO\n", i);
        else if (tabelaHash[i] == REMOVIDO) printf("[%d]: REMOVIDO\n", i);
        else printf("[%d]: %d\n", i, tabelaHash[i]);
    }
    printf("\n");
}

int main() {
    inicializarTabela();
    
    // Inserindo valores que vão colidir (Slide 16)
    // 15 % 10 = 5
    // 25 % 10 = 5 (colisão, vai para 6)
    // 35 % 10 = 5 (colisão, vai para 7)
    // 7 % 10 = 7 (colisão, vai para 8)
    // 17 % 10 = 7 (colisão, vai para 9)
    inserir(15);
    inserir(25);
    inserir(35);
    inserir(7);
    inserir(17);
    
    imprimirTabela();
    
    // Testando a busca
    int busca = 25;
    int pos = buscar(busca);
    if (pos != -1) {
        printf("Elemento %d encontrado na posicao: %d\n", busca, pos);
    } else {
        printf("Elemento %d nao encontrado\n", busca);
    }
    
    // Testando a remoção
    remover(25);
    imprimirTabela();
    
    // Tentando buscar um valor removido
    pos = buscar(25);
    if (pos == -1) {
        printf("Elemento 25 nao encontrado apos remocao (correto!)\n");
    }

    return 0;
}