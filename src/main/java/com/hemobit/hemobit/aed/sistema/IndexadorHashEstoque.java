package com.hemobit.hemobit.aed.sistema;

import com.hemobit.hemobit.domain.Hemocomponente;
import com.hemobit.hemobit.domain.TipoSanguineo;

import java.util.ArrayList;
import java.util.List;

public class IndexadorHashEstoque {
    // Usaremos 8 posições para os 8 tipos sanguíneos
    private static final int TAM = 8;
    private final List<Hemocomponente>[] tabela;

    @SuppressWarnings("unchecked")
    public IndexadorHashEstoque() {
        tabela = new ArrayList[TAM];
        for (int i = 0; i < TAM; i++) {
            tabela[i] = new ArrayList<>();
        }
    }

    // Função hash baseada no tipo sanguíneo (0 a 7)
    private int hash(TipoSanguineo tipo) {
        return tipo.ordinal() % TAM;
    }

    public void inserir(Hemocomponente hemocomponente) {
        int indice = hash(hemocomponente.getTipoSanguineo());
        tabela[indice].add(hemocomponente);
    }

    // Recupera as bolsas de um tipo específico (Busca O(1) na tabela)
    public List<Hemocomponente> buscarPorTipo(TipoSanguineo tipo) {
        int indice = hash(tipo);
        return tabela[indice];
    }
}