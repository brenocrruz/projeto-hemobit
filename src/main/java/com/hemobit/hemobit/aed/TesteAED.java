package com.hemobit.hemobit.aed;

import com.hemobit.hemobit.domain.Doacao;
import com.hemobit.hemobit.domain.Doador;
import com.hemobit.hemobit.domain.TipoSanguineo;
import java.time.LocalDateTime;
import java.util.List;

public class TesteAED {
    public static void main(String[] args) {
        GerenciadorHemocentro gerenciador = new GerenciadorHemocentro();

        System.out.println("--- TESTE DE ESTOQUE ---");
        Doador doador = new Doador();
        doador.setTipoSanguineo(TipoSanguineo.O_NEGATIVO);

        LocalDateTime dataHoje = LocalDateTime.now();
        LocalDateTime dataAntiga = LocalDateTime.now().minusDays(5);

        Doacao d1 = new Doacao();
        d1.setDataHora(dataHoje);
        d1.setDoador(doador);

        Doacao d2 = new Doacao();
        d2.setDataHora(dataAntiga);
        d2.setDoador(doador);

        gerenciador.registrarEntrada(d1);
        gerenciador.registrarEntrada(d2);

        Doacao bolsaRetirada = gerenciador.solicitarBolsa("O_NEGATIVO");
        if (bolsaRetirada != null && bolsaRetirada.getDataHora().equals(dataAntiga)) {
            System.out.println("SUCESSO: A bolsa mais antiga (FEFO) foi entregue.");
        } else {
            System.out.println("ERRO no sistema de estoque.");
        }

        System.out.println("\n--- TESTE DE ROTAS ---");
        gerenciador.adicionarLocalMapa("Hemocentro");
        gerenciador.adicionarLocalMapa("Cruz Vermelha");
        gerenciador.adicionarLocalMapa("Hospital Sao Jose");
        gerenciador.adicionarLocalMapa("Hospital Santa Maria");

        gerenciador.adicionarRota("Hemocentro", "Cruz Vermelha", 5.0);
        gerenciador.adicionarRota("Cruz Vermelha", "Hospital Sao Jose", 3.0);
        gerenciador.adicionarRota("Hemocentro", "Hospital Santa Maria", 10.0);
        gerenciador.adicionarRota("Hospital Santa Maria", "Hospital Sao Jose", 2.0);
        gerenciador.adicionarRota("Hemocentro", "Hospital Sao Jose", 15.0);

        List<String> rota = gerenciador.calcularMelhorRota("Hemocentro", "Hospital Sao Jose");
        
        System.out.println("A rota mais rapida para o Hospital Sao Jose e:");
        System.out.println(rota);
    }
}