package com.hemobit.hemobit.benchmark;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GeradorDadosSinteticos {

    public static List<HemocomponenteSintetico> gerar(int tamanho) {
        Random random = new Random(42);
        String[] tipos = {"CONCENTRADO_HEMACIAS", "CONCENTRADO_PLAQUETAS", "PLASMA_FRESCO_CONGELADO"};
        List<HemocomponenteSintetico> dados = new ArrayList<>(tamanho);

        LocalDate hoje = LocalDate.now();

        for (int i = 0; i < tamanho; i++) {
            String tipo = tipos[random.nextInt(tipos.length)];
            int deslocamentoDias = random.nextInt(151) - 30;
            LocalDate dataValidade = hoje.plusDays(deslocamentoDias);
            dados.add(new HemocomponenteSintetico(i, tipo, dataValidade));
        }

        return dados;
    }
}