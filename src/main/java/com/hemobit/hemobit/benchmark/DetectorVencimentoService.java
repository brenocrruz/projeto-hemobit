package com.hemobit.hemobit.benchmark;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class DetectorVencimentoService {

    private String classificar(HemocomponenteSintetico item, LocalDate hoje) {
        long diasParaVencer = ChronoUnit.DAYS.between(hoje, item.getDataValidade());

        if (diasParaVencer < 0) return "VENCIDO";
        if (diasParaVencer <= 7) return "URGENTE";
        if (diasParaVencer <= 30) return "ATENCAO";
        return "OK";
    }

    public void aquecerJvm(List<HemocomponenteSintetico> dados) {
        for (int i = 0; i < 3; i++) {
            detectarSequencial(dados);
            detectarParalelo(dados, 4);
        }
    }

    public ResultadoDeteccao detectarSequencial(List<HemocomponenteSintetico> dados) {
        long inicio = System.currentTimeMillis();

        LocalDate hoje = LocalDate.now();
        Map<String, Long> contagens = new HashMap<>();
        contagens.put("VENCIDO", 0L);
        contagens.put("URGENTE", 0L);
        contagens.put("ATENCAO", 0L);
        contagens.put("OK", 0L);

        for (HemocomponenteSintetico item : dados) {
            String categoria = classificar(item, hoje);
            contagens.merge(categoria, 1L, Long::sum);
        }

        long tempoMs = System.currentTimeMillis() - inicio;
        return new ResultadoDeteccao(contagens, tempoMs, 1);
    }

    public ResultadoDeteccao detectarParalelo(List<HemocomponenteSintetico> dados, int numThreads) {
        long inicio = System.currentTimeMillis();
        LocalDate hoje = LocalDate.now();

        int tamanhoTotal = dados.size();
        int tamanhoFatia = (int) Math.ceil((double) tamanhoTotal / numThreads);

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<Future<Map<String, Long>>> futures = new ArrayList<>();

        try {
            for (int t = 0; t < numThreads; t++) {
                int inicioFatia = t * tamanhoFatia;
                int fimFatia = Math.min(inicioFatia + tamanhoFatia, tamanhoTotal);

                if (inicioFatia >= fimFatia) {
                    continue;
                }

                List<HemocomponenteSintetico> fatia = dados.subList(inicioFatia, fimFatia);

                Callable<Map<String, Long>> tarefa = () -> {
                    Map<String, Long> contagensLocais = new HashMap<>();
                    contagensLocais.put("VENCIDO", 0L);
                    contagensLocais.put("URGENTE", 0L);
                    contagensLocais.put("ATENCAO", 0L);
                    contagensLocais.put("OK", 0L);

                    for (HemocomponenteSintetico item : fatia) {
                        String categoria = classificar(item, hoje);
                        contagensLocais.merge(categoria, 1L, Long::sum);
                    }
                    return contagensLocais;
                };

                futures.add(executor.submit(tarefa));
            }

            Map<String, Long> contagensFinais = new HashMap<>();
            contagensFinais.put("VENCIDO", 0L);
            contagensFinais.put("URGENTE", 0L);
            contagensFinais.put("ATENCAO", 0L);
            contagensFinais.put("OK", 0L);

            for (Future<Map<String, Long>> future : futures) {
                Map<String, Long> parcial = future.get();
                for (Map.Entry<String, Long> entrada : parcial.entrySet()) {
                    contagensFinais.merge(entrada.getKey(), entrada.getValue(), Long::sum);
                }
            }

            long tempoMs = System.currentTimeMillis() - inicio;
            return new ResultadoDeteccao(contagensFinais, tempoMs, numThreads);

        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Erro ao processar detecção em paralelo.", e);
        } finally {
            executor.shutdown();
        }
    }

    public ResultadoDeteccao detectarParaleloVirtualThreads(List<HemocomponenteSintetico> dados, int numThreads) {
        long inicio = System.currentTimeMillis();
        LocalDate hoje = LocalDate.now();

        int tamanhoTotal = dados.size();
        int tamanhoFatia = (int) Math.ceil((double) tamanhoTotal / numThreads);

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<Map<String, Long>>> futures = new ArrayList<>();

            for (int t = 0; t < numThreads; t++) {
                int inicioFatia = t * tamanhoFatia;
                int fimFatia = Math.min(inicioFatia + tamanhoFatia, tamanhoTotal);
                if (inicioFatia >= fimFatia) continue;

                List<HemocomponenteSintetico> fatia = dados.subList(inicioFatia, fimFatia);

                futures.add(executor.submit(() -> {
                    Map<String, Long> contagensLocais = new HashMap<>();
                    contagensLocais.put("VENCIDO", 0L);
                    contagensLocais.put("URGENTE", 0L);
                    contagensLocais.put("ATENCAO", 0L);
                    contagensLocais.put("OK", 0L);
                    for (HemocomponenteSintetico item : fatia) {
                        contagensLocais.merge(classificar(item, hoje), 1L, Long::sum);
                    }
                    return contagensLocais;
                }));
            }

            Map<String, Long> contagensFinais = new HashMap<>();
            contagensFinais.put("VENCIDO", 0L);
            contagensFinais.put("URGENTE", 0L);
            contagensFinais.put("ATENCAO", 0L);
            contagensFinais.put("OK", 0L);

            for (Future<Map<String, Long>> future : futures) {
                Map<String, Long> parcial = future.get();
                for (Map.Entry<String, Long> entrada : parcial.entrySet()) {
                    contagensFinais.merge(entrada.getKey(), entrada.getValue(), Long::sum);
                }
            }

            long tempoMs = System.currentTimeMillis() - inicio;
            return new ResultadoDeteccao(contagensFinais, tempoMs, numThreads);

        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Erro ao processar com virtual threads.", e);
        }
    }
}