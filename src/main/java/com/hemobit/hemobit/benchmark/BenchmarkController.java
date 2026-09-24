package com.hemobit.hemobit.benchmark;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/benchmark/deteccao-vencimento")
public class BenchmarkController {

    private final DetectorVencimentoService detectorVencimentoService;

    @Autowired
    public BenchmarkController(DetectorVencimentoService detectorVencimentoService) {
        this.detectorVencimentoService = detectorVencimentoService;
    }

    @GetMapping
    public Map<String, Object> comparar(
            @RequestParam(defaultValue = "100000") int tamanho,
            @RequestParam(defaultValue = "4") int threads) {

        List<HemocomponenteSintetico> dados = GeradorDadosSinteticos.gerar(tamanho);

        ResultadoDeteccao sequencial = detectorVencimentoService.detectarSequencial(dados);
        ResultadoDeteccao paralelo = detectorVencimentoService.detectarParalelo(dados, threads);

        boolean resultadosIguais = sequencial.getContagensPorCategoria()
                .equals(paralelo.getContagensPorCategoria());

        double speedup = paralelo.getTempoMs() == 0
                ? 0
                : (double) sequencial.getTempoMs() / paralelo.getTempoMs();

        Map<String, Object> resposta = new HashMap<>();
        resposta.put("tamanho", tamanho);
        resposta.put("threads", threads);
        resposta.put("tempoSequencialMs", sequencial.getTempoMs());
        resposta.put("tempoParaleloMs", paralelo.getTempoMs());
        resposta.put("speedup", speedup);
        resposta.put("contagensSequencial", sequencial.getContagensPorCategoria());
        resposta.put("contagensParalelo", paralelo.getContagensPorCategoria());
        resposta.put("resultadosIguais", resultadosIguais);

        return resposta;
    }

    @GetMapping("/suite")
    public List<Map<String, Object>> suiteCompleta() {
        int[] tamanhos = {100_000, 1_000_000};
        int[] configuracoesThreads = {2, 4, 8};

        List<Map<String, Object>> resultados = new ArrayList<>();

        for (int tamanho : tamanhos) {
            List<HemocomponenteSintetico> dados = GeradorDadosSinteticos.gerar(tamanho);

            detectorVencimentoService.aquecerJvm(dados);
            
            ResultadoDeteccao sequencial = detectorVencimentoService.detectarSequencial(dados);
            resultados.add(linhaResultado(tamanho, 1, sequencial.getTempoMs(), sequencial.getTempoMs()));

            for (int threads : configuracoesThreads) {
                ResultadoDeteccao paralelo = detectorVencimentoService.detectarParalelo(dados, threads);
                resultados.add(linhaResultado(tamanho, threads, sequencial.getTempoMs(), paralelo.getTempoMs()));
            }
        }

        return resultados;
    }

    private Map<String, Object> linhaResultado(int tamanho, int threads, long tempoSequencialMs, long tempoMs) {
        Map<String, Object> linha = new HashMap<>();
        linha.put("tamanho", tamanho);
        linha.put("threads", threads);
        linha.put("tempoMs", tempoMs);
        linha.put("speedup", tempoMs == 0 ? 0 : (double) tempoSequencialMs / tempoMs);
        return linha;
    }
}