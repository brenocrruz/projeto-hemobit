package com.hemobit.hemobit.service;

import com.hemobit.hemobit.dto.IndicadoresDTO;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EstatisticaService {

    public double calcularMedia(List<Double> valores) {
        if (valores == null || valores.isEmpty()) return 0.0;
        return valores.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }

    public double calcularMediana(List<Double> valores) {
        if (valores == null || valores.isEmpty()) return 0.0;
        List<Double> ordenados = new ArrayList<>(valores);
        Collections.sort(ordenados);
        int n = ordenados.size();
        if (n % 2 == 1) return ordenados.get(n / 2);
        return (ordenados.get((n / 2) - 1) + ordenados.get(n / 2)) / 2.0;
    }

    public double calcularModa(List<Double> valores) {
        if (valores == null || valores.isEmpty()) return 0.0;
        Map<Double, Long> freq = valores.stream()
                .collect(Collectors.groupingBy(v -> v, Collectors.counting()));
        return Collections.max(freq.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

    public double calcularVariancia(List<Double> valores) {
        if (valores == null || valores.size() < 2) return 0.0;
        double media = calcularMedia(valores);
        double somaQuadrados = 0.0;
        for (Double v : valores) {
            somaQuadrados += Math.pow(v - media, 2);
        }
        return somaQuadrados / (valores.size() - 1);
    }

    public double calcularDesvioPadrao(List<Double> valores) {
        return Math.sqrt(calcularVariancia(valores));
    }

    public Map<String, Long> calcularDistribuicaoFrequencia(List<Double> valores) {
        if (valores == null || valores.isEmpty()) return new LinkedHashMap<>();
        Map<String, Long> freq = new LinkedHashMap<>();
        for (Double v : valores) {
            String chave = String.valueOf(v);
            freq.put(chave, freq.getOrDefault(chave, 0L) + 1);
        }
        return freq;
    }

    public IndicadoresDTO gerarRelatorioIndicador(String nome, List<Double> dados) {
        return new IndicadoresDTO(
                nome,
                calcularMedia(dados),
                calcularMediana(dados),
                calcularModa(dados),
                calcularDesvioPadrao(dados),
                calcularVariancia(dados),
                calcularDistribuicaoFrequencia(dados)
        );
    }
}
