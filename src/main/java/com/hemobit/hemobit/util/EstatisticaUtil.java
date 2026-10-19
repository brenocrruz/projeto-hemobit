package com.hemobit.hemobit.util;

public final class EstatisticaUtil {

    private EstatisticaUtil() {
    }


    public static double probabilidadeDesabastecimento(double lambda, int estoque) {
        if (lambda <= 0) {
            return 0.0;
        }
        if (estoque < 0) {
            return 1.0;
        }
        double termo = Math.exp(-lambda); // P(D = 0)
        double acumulada = termo;
        for (int k = 1; k <= estoque; k++) {
            termo = termo * lambda / k;   // P(D = k)
            acumulada += termo;
        }
        return Math.max(0.0, Math.min(1.0, 1 - acumulada));
    }

    public static double taxaDescarte(long vencidas, long total) {
        if (total <= 0) {
            return 0.0;
        }
        return vencidas * 100.0 / total;
    }

    public static double lambdaHorizonte(double demandaTotal, int diasObservados, int diasHorizonte) {
        if (diasObservados <= 0) {
            return 0.0;
        }
        return demandaTotal / diasObservados * diasHorizonte;
    }
}