package com.hemobit.hemobit.aed.sistema;

import com.hemobit.hemobit.domain.Hemocomponente;

public class Fefo {
    
    public static class Bolsa {
        public int id;
        public int diasParaVencer;
        public Hemocomponente hemocomponenteOriginal; // <-- Adicionado

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
}