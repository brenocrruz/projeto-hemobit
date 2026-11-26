package com.hemobit.hemobit.aed.busca;

public class HashLinear {
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
                System.out.println("Inserido " + chave + " na posicao " + tentativa);
                return;
            }
        }
        System.out.println("Tabela cheia! Nao foi possivel inserir " + chave);
    }

    public int buscar(int chave) {
        int indice = hash(chave);
        for (int i = 0; i < TAM; i++) {
            int tentativa = (indice + i) % TAM;
            if (tabela[tentativa] == VAZIO) {
                return -1; // Nunca foi inserido
            }
            if (tabela[tentativa] == chave) {
                return tentativa;
            }
        }
        return -1;
    }

    public boolean remover(int chave) {
        int posicao = buscar(chave);
        if (posicao != -1) {
            tabela[posicao] = REMOVIDO;
            System.out.println("Valor " + chave + " removido da posicao " + posicao);
            return true;
        }
        return false;
    }

    public void imprimirTabela() {
        System.out.println("\nTabela Hash:");
        for (int i = 0; i < TAM; i++) {
            if (tabela[i] == VAZIO) System.out.println("[" + i + "]: VAZIO");
            else if (tabela[i] == REMOVIDO) System.out.println("[" + i + "]: REMOVIDO");
            else System.out.println("[" + i + "]: " + tabela[i]);
        }
        System.out.println();
    }

    public static void main(String[] args) {
        HashLinear hash = new HashLinear();
        
        hash.inserir(15);
        hash.inserir(25);
        hash.inserir(35);
        hash.inserir(7);
        hash.inserir(17);
        
        hash.imprimirTabela();
        
        int busca = 25;
        int pos = hash.buscar(busca);
        System.out.println("Elemento " + busca + (pos != -1 ? " encontrado na posicao: " + pos : " nao encontrado"));
        
        hash.remover(25);
        hash.imprimirTabela();
        
        System.out.println("Elemento 25 apos remocao: " + (hash.buscar(25) == -1 ? "nao encontrado (correto)" : "encontrado (erro)"));
    }
}