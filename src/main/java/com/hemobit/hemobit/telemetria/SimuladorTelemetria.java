package com.hemobit.hemobit.telemetria;

import com.hemobit.hemobit.rede.MetricasRedeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Simula os veículos de transporte enviando telemetria (GPS + temperatura + timestamp) por HTTP
 * para o endpoint de ingestão. Cada envio é medido no cliente (RTT, status, bytes), o que gera as
 * métricas de latência, jitter, perda e vazão do canal.
 *
 * O simulador não injeta falhas: a degradação do canal é aplicada na rede (tc netem no Nginx,
 * ver scripts/benchmark-rede.sh), para que perda e atraso sejam fenômenos reais de TCP/IP.
 * Fica desligado por padrão; o compose.yaml o liga no ambiente local.
 */
@Component
public class SimuladorTelemetria {

    private final MetricasRedeService metricas;
    private final HttpClient cliente;
    private final URI destino;
    private final boolean habilitado;
    private final Duration timeout;
    private final List<Veiculo> veiculos = List.of(
            new Veiculo("VTR-01", "CH", -8.0476, -34.8770, 4.0),
            new Veiculo("VTR-02", "CP", -8.0631, -34.8711, 22.0),
            new Veiculo("VTR-03", "PFC", -8.0890, -34.8950, -24.0));

    public SimuladorTelemetria(MetricasRedeService metricas,
                               @Value("${hemobit.telemetria.simulador.habilitado:false}") boolean habilitado,
                               @Value("${hemobit.telemetria.simulador.url:http://localhost:${server.port:8080}/api/v1/telemetria/leituras}") String url,
                               @Value("${hemobit.telemetria.simulador.timeout-ms:2000}") long timeoutMs) {
        this.metricas = metricas;
        this.habilitado = habilitado;
        this.destino = URI.create(url);
        this.timeout = Duration.ofMillis(timeoutMs);
        this.cliente = HttpClient.newBuilder().connectTimeout(timeout).build();
    }

    @Scheduled(initialDelayString = "${hemobit.telemetria.simulador.atraso-inicial-ms:5000}",
            fixedRateString = "${hemobit.telemetria.simulador.intervalo-ms:1000}")
    public void emitir() {
        if (!habilitado) return;
        veiculos.forEach(this::enviar);
    }

    private void enviar(Veiculo v) {
        String corpo = v.proximaLeitura(ThreadLocalRandom.current());
        long bytesEnviados = corpo.getBytes(StandardCharsets.UTF_8).length;
        HttpRequest requisicao = HttpRequest.newBuilder(destino)
                .timeout(timeout)
                .header("Content-Type", "application/json")
                .header("X-Origem", "simulador-telemetria")
                .POST(HttpRequest.BodyPublishers.ofString(corpo))
                .build();
        long inicio = System.nanoTime();
        cliente.sendAsync(requisicao, HttpResponse.BodyHandlers.ofString())
                .whenComplete((resposta, erro) -> {
                    double rttMs = (System.nanoTime() - inicio) / 1_000_000.0;
                    if (erro != null || resposta == null) {
                        // Timeout ou falha de conexão: conta como perda no cliente.
                        metricas.registrarEnvio(v.id, rttMs, 0, bytesEnviados);
                    } else {
                        long bytes = bytesEnviados + resposta.body().getBytes(StandardCharsets.UTF_8).length;
                        metricas.registrarEnvio(v.id, rttMs, resposta.statusCode(), bytes);
                    }
                });
    }

    public Map<String, Object> estado() {
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("habilitado", habilitado);
        r.put("destino", destino.toString());
        r.put("veiculos", veiculos.size());
        r.put("timeoutMs", timeout.toMillis());
        return r;
    }

    /** Veículo sintético que percorre um círculo em torno do ponto inicial. */
    private static final class Veiculo {
        private final String id;
        private final String tipo;
        private final double lat0;
        private final double lon0;
        private final double temperaturaAlvo;
        private long sequencia;

        Veiculo(String id, String tipo, double lat0, double lon0, double temperaturaAlvo) {
            this.id = id;
            this.tipo = tipo;
            this.lat0 = lat0;
            this.lon0 = lon0;
            this.temperaturaAlvo = temperaturaAlvo;
        }

        synchronized String proximaLeitura(ThreadLocalRandom rnd) {
            long seq = sequencia++;
            double angulo = seq / 60.0 * 2 * Math.PI;
            double lat = lat0 + 0.01 * Math.sin(angulo);
            double lon = lon0 + 0.01 * Math.cos(angulo);
            double temp = temperaturaAlvo + rnd.nextGaussian() * 0.8;
            return String.format(Locale.ROOT,
                    "{\"veiculo\":\"%s\",\"sequencia\":%d,\"tipoHemocomponente\":\"%s\","
                            + "\"latitude\":%.6f,\"longitude\":%.6f,\"temperaturaC\":%.2f,\"enviadoEm\":%d}",
                    id, seq, tipo, lat, lon, temp, System.currentTimeMillis());
        }
    }
}
