package com.hemobit.hemobit.aed.estruturas;

import com.hemobit.hemobit.domain.Doacao;

public class TabelaHashEstoque {
    private FilaFEFO[] gavetas;

    public TabelaHashEstoque() {
        gavetas = new FilaFEFO[8];
        for (int i = 0; i < 8; i++) {
            gavetas[i] = new FilaFEFO(); 
        }
    }

    private int calcularIndice(String tipoSanguineo) {
        switch (tipoSanguineo.toUpperCase().trim()) {
            case "A+": case "A_POSITIVO": return 0;
            case "A-": case "A_NEGATIVO": return 1;
            case "B+": case "B_POSITIVO": return 2;
            case "B-": case "B_NEGATIVO": return 3;
            case "AB+": case "AB_POSITIVO": return 4;
            case "AB-": case "AB_NEGATIVO": return 5;
            case "O+": case "O_POSITIVO": return 6;
            case "O-": case "O_NEGATIVO": return 7;
            default: return 0;
        }
    }

    public void adicionar(Doacao doacao) {
        String tipo = doacao.getDoador().getTipoSanguineo().toString(); 
        int indice = calcularIndice(tipo);
        gavetas[indice].inserir(doacao);
    }

    public Doacao retirar(String tipoSanguineo) {
        int indice = calcularIndice(tipoSanguineo);
        return gavetas[indice].remover();
    }
}