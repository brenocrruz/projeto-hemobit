package com.hemobit.hemobit.rede;

import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.LongSupplier;

/**
 * Coleta as métricas de rede do canal de telemetria e das APIs em uma janela deslizante.
 *
 * Duas fontes alimentam o serviço:
 * - CLIENTE: o simulador de telemetria mede o RTT de cada envio HTTP (latência, jitter, perda, vazão);
 * - SERVIDOR: o filtro HTTP registra status e tempo de processamento de cada requisição da API (4xx/5xx).
 */
@Service
public class MetricasRedeService {

    public static final long JANELA_MS = 60_000;
    private static final int LIMITE_AMOSTRAS = 20_000;

    private final LongSupplier relogio;
    private final Deque<Amostra> envios = new ArrayDeque<>();
    private final Deque<Amostra> requisicoes = new ArrayDeque<>();
    private long perdasSequencia;
    private long esperadosSequencia;
    private long totalEnvios;
    private long totalRequisicoes;
    private final long inicio;

    public MetricasRedeService() {
        this(System::currentTimeMillis);
    }

    MetricasRedeService(LongSupplier relogio) {
        this.relogio = relogio;
        this.inicio = relogio.getAsLong();
    }

    /** Envio de telemetria medido no cliente. status = 0 indica timeout ou falha de conexão. */
    public synchronized void registrarEnvio(String veiculo, double rttMs, int status, long bytes) {
        adicionar(envios, new Amostra(relogio.getAsLong(), rttMs, status, bytes, veiculo));
        totalEnvios++;
    }

    /** Requisição atendida pela API, medida no servidor. */
    public synchronized void registrarRequisicao(String rota, int status, double duracaoMs, long bytes) {
        adicionar(requisicoes, new Amostra(relogio.getAsLong(), duracaoMs, status, bytes, rota));
        totalRequisicoes++;
    }

    /** Lacunas de sequência detectadas na ingestão (perda observada no lado do servidor). */
    public synchronized void registrarSequencia(long esperados, long perdidos) {
        esperadosSequencia += esperados;
        perdasSequencia += perdidos;
    }

    /** Leitura que chegou fora de ordem e preencheu uma lacuna já contada como perda. */
    public synchronized void registrarLacunaRecuperada() {
        if (perdasSequencia > 0) perdasSequencia--;
    }

    public synchronized void reiniciar() {
        envios.clear();
        requisicoes.clear();
        perdasSequencia = 0;
        esperadosSequencia = 0;
        totalEnvios = 0;
        totalRequisicoes = 0;
    }

    public synchronized Map<String, Object> snapshot() {
        long agora = relogio.getAsLong();
        descartarAntigas(envios, agora);
        descartarAntigas(requisicoes, agora);

        Map<String, Object> resposta = new LinkedHashMap<>();
        resposta.put("geradoEm", agora);
        resposta.put("janelaSegundos", JANELA_MS / 1000);
        resposta.put("telemetria", resumoTelemetria(agora));
        resposta.put("api", resumoApi(agora));
        resposta.put("serie", serieTemporal(agora));
        resposta.put("totais", Map.of(
                "enviosTelemetria", totalEnvios,
                "requisicoesApi", totalRequisicoes,
                "esperadosSequencia", esperadosSequencia,
                "perdidosSequencia", perdasSequencia,
                "uptimeSegundos", (agora - inicio) / 1000));
        return resposta;
    }

    private Map<String, Object> resumoTelemetria(long agora) {
        List<Amostra> lista = new ArrayList<>(envios);
        double[] latencias = lista.stream().filter(Amostra::entregue).mapToDouble(Amostra::valorMs).toArray();
        long entregues = latencias.length;
        long perdidos = lista.stream().filter(a -> a.status() == 0).count();
        long bytes = lista.stream().mapToLong(Amostra::bytes).sum();
        double segundos = duracaoEfetiva(lista, agora);

        Map<String, Object> r = new LinkedHashMap<>();
        r.put("amostras", lista.size());
        r.put("latenciaMs", estatisticas(latencias));
        r.put("jitterMs", arredondar(jitterPorVeiculo(lista)));
        r.put("perdaPercentual", lista.isEmpty() ? 0.0 : arredondar(100.0 * perdidos / lista.size()));
        r.put("perdidos", perdidos);
        r.put("vazaoMsgPorSegundo", arredondar(entregues / segundos));
        r.put("vazaoKbps", arredondar(bytes * 8 / 1000.0 / segundos));
        r.put("erros4xx", lista.stream().filter(a -> a.status() >= 400 && a.status() < 500).count());
        r.put("erros5xx", lista.stream().filter(a -> a.status() >= 500).count());
        r.put("perdaSequenciaPercentual", esperadosSequencia == 0 ? 0.0
                : arredondar(100.0 * perdasSequencia / esperadosSequencia));
        return r;
    }

    private Map<String, Object> resumoApi(long agora) {
        List<Amostra> lista = new ArrayList<>(requisicoes);
        long e4 = lista.stream().filter(a -> a.status() >= 400 && a.status() < 500).count();
        long e5 = lista.stream().filter(a -> a.status() >= 500).count();
        double segundos = duracaoEfetiva(lista, agora);

        Map<String, Object> r = new LinkedHashMap<>();
        r.put("requisicoes", lista.size());
        r.put("vazaoReqPorSegundo", arredondar(lista.size() / segundos));
        r.put("tempoServidorMs", estatisticas(lista.stream().mapToDouble(Amostra::valorMs).toArray()));
        r.put("erros4xx", e4);
        r.put("erros5xx", e5);
        r.put("taxaErro4xxPercentual", lista.isEmpty() ? 0.0 : arredondar(100.0 * e4 / lista.size()));
        r.put("taxaErro5xxPercentual", lista.isEmpty() ? 0.0 : arredondar(100.0 * e5 / lista.size()));

        Map<String, List<Amostra>> porRota = new TreeMap<>();
        lista.forEach(a -> porRota.computeIfAbsent(a.rota(), k -> new ArrayList<>()).add(a));
        List<Map<String, Object>> rotas = new ArrayList<>();
        porRota.forEach((rota, amostras) -> {
            double[] tempos = amostras.stream().mapToDouble(Amostra::valorMs).toArray();
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("rota", rota);
            item.put("requisicoes", amostras.size());
            item.put("p95Ms", arredondar(percentil(tempos, 95)));
            item.put("erros4xx", amostras.stream().filter(a -> a.status() >= 400 && a.status() < 500).count());
            item.put("erros5xx", amostras.stream().filter(a -> a.status() >= 500).count());
            rotas.add(item);
        });
        rotas.sort((a, b) -> Integer.compare((int) b.get("requisicoes"), (int) a.get("requisicoes")));
        r.put("rotas", rotas);
        return r;
    }

    /** Buckets de 1 s com os últimos 60 s, para os gráficos do painel. */
    private List<Map<String, Object>> serieTemporal(long agora) {
        long fim = agora / 1000;
        long inicioSerie = fim - JANELA_MS / 1000 + 1;
        List<List<Amostra>> buckets = new ArrayList<>();
        for (long s = inicioSerie; s <= fim; s++) buckets.add(new ArrayList<>());
        for (Amostra a : envios) {
            int i = (int) (a.instante() / 1000 - inicioSerie);
            if (i >= 0 && i < buckets.size()) buckets.get(i).add(a);
        }
        int[] erros = new int[buckets.size()];
        for (Amostra a : requisicoes) {
            int i = (int) (a.instante() / 1000 - inicioSerie);
            if (i >= 0 && i < erros.length && a.status() >= 400) erros[i]++;
        }
        List<Map<String, Object>> serie = new ArrayList<>();
        for (int i = 0; i < buckets.size(); i++) {
            List<Amostra> b = buckets.get(i);
            double[] lat = b.stream().filter(Amostra::entregue).mapToDouble(Amostra::valorMs).toArray();
            Map<String, Object> ponto = new LinkedHashMap<>();
            ponto.put("t", (inicioSerie + i) * 1000);
            ponto.put("envios", b.size());
            ponto.put("latenciaMediaMs", lat.length == 0 ? null : arredondar(Arrays.stream(lat).average().orElse(0)));
            ponto.put("latenciaP95Ms", lat.length == 0 ? null : arredondar(percentil(lat, 95)));
            ponto.put("perdidos", b.stream().filter(a -> a.status() == 0).count());
            ponto.put("errosApi", erros[i]);
            serie.add(ponto);
        }
        return serie;
    }

    static Map<String, Object> estatisticas(double[] valores) {
        Map<String, Object> r = new LinkedHashMap<>();
        if (valores.length == 0) {
            r.put("media", null);
            r.put("p50", null);
            r.put("p95", null);
            r.put("p99", null);
            r.put("max", null);
            return r;
        }
        r.put("media", arredondar(Arrays.stream(valores).average().orElse(0)));
        r.put("p50", arredondar(percentil(valores, 50)));
        r.put("p95", arredondar(percentil(valores, 95)));
        r.put("p99", arredondar(percentil(valores, 99)));
        r.put("max", arredondar(Arrays.stream(valores).max().orElse(0)));
        return r;
    }

    /** Percentil pelo método nearest-rank. */
    static double percentil(double[] valores, double p) {
        if (valores.length == 0) return 0;
        double[] ordenados = valores.clone();
        Arrays.sort(ordenados);
        int rank = (int) Math.ceil(p / 100.0 * ordenados.length);
        return ordenados[Math.max(0, Math.min(rank, ordenados.length) - 1)];
    }

    /**
     * Jitter calculado separadamente para cada veículo (cada um é um fluxo, como um SSRC no RTP) e
     * combinado pela média ponderada pelo número de amostras. Misturar fluxos compararia latências
     * de veículos diferentes e inflaria o valor.
     */
    static double jitterPorVeiculo(List<Amostra> amostras) {
        Map<String, List<Double>> porVeiculo = new TreeMap<>();
        amostras.stream().filter(Amostra::entregue)
                .forEach(a -> porVeiculo.computeIfAbsent(a.rota(), k -> new ArrayList<>()).add(a.valorMs()));
        double soma = 0;
        int peso = 0;
        for (List<Double> fluxo : porVeiculo.values()) {
            soma += jitter(fluxo.stream().mapToDouble(Double::doubleValue).toArray()) * fluxo.size();
            peso += fluxo.size();
        }
        return peso == 0 ? 0 : soma / peso;
    }

    /** Jitter interchegada (RFC 3550, seção 6.4.1): J += (|D| - J) / 16 sobre latências consecutivas. */
    static double jitter(double[] latencias) {
        double j = 0;
        for (int i = 1; i < latencias.length; i++) {
            j += (Math.abs(latencias[i] - latencias[i - 1]) - j) / 16.0;
        }
        return j;
    }

    private double duracaoEfetiva(List<Amostra> lista, long agora) {
        long desde = Math.max(inicio, agora - JANELA_MS);
        if (!lista.isEmpty()) desde = Math.max(desde, Math.min(lista.get(0).instante(), agora - 1000));
        return Math.max(1.0, (agora - desde) / 1000.0);
    }

    private void adicionar(Deque<Amostra> fila, Amostra amostra) {
        fila.addLast(amostra);
        while (fila.size() > LIMITE_AMOSTRAS) fila.removeFirst();
        descartarAntigas(fila, amostra.instante());
    }

    private void descartarAntigas(Deque<Amostra> fila, long agora) {
        while (!fila.isEmpty() && fila.peekFirst().instante() < agora - JANELA_MS) fila.removeFirst();
    }

    private static double arredondar(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }

    record Amostra(long instante, double valorMs, int status, long bytes, String rota) {
        boolean entregue() {
            return status >= 200 && status < 400;
        }
    }
}
