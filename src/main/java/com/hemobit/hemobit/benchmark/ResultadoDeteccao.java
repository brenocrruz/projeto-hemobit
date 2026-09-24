package com.hemobit.hemobit.benchmark;

import java.util.Map;

public class ResultadoDeteccao {

    private final Map<String, Long> contagensPorCategoria;
    private final long tempoMs;
    private final int threads;

    public ResultadoDeteccao(Map<String, Long> contagensPorCategoria, long tempoMs, int threads) {
        this.contagensPorCategoria = contagensPorCategoria;
        this.tempoMs = tempoMs;
        this.threads = threads;
    }

    public Map<String, Long> getContagensPorCategoria() {
        return contagensPorCategoria;
    }

    public long getTempoMs() {
        return tempoMs;
    }

    public int getThreads() {
        return threads;
    }
}