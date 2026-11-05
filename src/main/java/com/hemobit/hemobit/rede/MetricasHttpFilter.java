package com.hemobit.hemobit.rede;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerMapping;

import java.io.IOException;

/**
 * Mede, no servidor, o status HTTP e o tempo de processamento de toda requisição da API.
 * As consultas do próprio painel (/api/v1/rede/**) ficam de fora para não contaminar as métricas.
 * O serviço é opcional para que fatias de teste (@WebMvcTest) que não o carregam continuem subindo.
 */
@Component
public class MetricasHttpFilter extends OncePerRequestFilter {

    private final ObjectProvider<MetricasRedeService> metricas;

    public MetricasHttpFilter(ObjectProvider<MetricasRedeService> metricas) {
        this.metricas = metricas;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return !uri.startsWith("/api/") || uri.startsWith("/api/v1/rede/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        long inicio = System.nanoTime();
        int status = 500;
        try {
            chain.doFilter(request, response);
            status = response.getStatus();
        } finally {
            double duracaoMs = (System.nanoTime() - inicio) / 1_000_000.0;
            Object padrao = request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
            String rota = padrao != null ? padrao.toString() : request.getRequestURI().replaceAll("/\\d+", "/{id}");
            long bytes = Math.max(0, request.getContentLengthLong());
            MetricasRedeService servico = metricas.getIfAvailable();
            if (servico != null) servico.registrarRequisicao(request.getMethod() + " " + rota, status, duracaoMs, bytes);
        }
    }
}
