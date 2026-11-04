package com.hemobit.hemobit.telemetria;

import com.hemobit.hemobit.rede.MetricasRedeService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Recebe as leituras de telemetria, detecta lacunas de sequência (perda no servidor) e
 * avalia a faixa de temperatura da cadeia fria (valores de data/grafo-rotas.json).
 * Quando um veículo sai da faixa, publica um {@link AlertaTemperatura} (enviado pelo /ws/telemetria).
 */
@Service
public class IngestaoTelemetriaService {

    /** Faixas de conservação em °C por tipo: {mínima, máxima}. */
    static final Map<String, double[]> FAIXAS = Map.of(
            "CH", new double[]{2, 6},
            "CP", new double[]{20, 24},
            "PFC", new double[]{-30, -18});

    private final MetricasRedeService metricas;
    private final ApplicationEventPublisher eventos;
    /** Limite de lacunas pendentes por veículo, para não crescer sem fim se um veículo sumir. */
    private static final int LIMITE_LACUNAS = 1000;

    private final Map<String, Long> ultimaSequencia = new HashMap<>();
    private final Map<String, Set<Long>> lacunas = new HashMap<>();
    private final Map<String, Map<String, Object>> ultimaLeitura = new TreeMap<>();
    private long foraDeOrdem;

    public IngestaoTelemetriaService(MetricasRedeService metricas, ApplicationEventPublisher eventos) {
        this.metricas = metricas;
        this.eventos = eventos;
    }

    public List<String> validar(LeituraTelemetria l) {
        List<String> erros = new ArrayList<>();
        if (l.getVeiculo() == null || l.getVeiculo().isBlank()) erros.add("veiculo é obrigatório");
        if (l.getSequencia() == null || l.getSequencia() < 0) erros.add("sequencia deve ser >= 0");
        if (l.getTipoHemocomponente() == null || !FAIXAS.containsKey(l.getTipoHemocomponente()))
            erros.add("tipoHemocomponente deve ser CH, CP ou PFC");
        if (l.getLatitude() == null || Math.abs(l.getLatitude()) > 90) erros.add("latitude inválida");
        if (l.getLongitude() == null || Math.abs(l.getLongitude()) > 180) erros.add("longitude inválida");
        if (l.getTemperaturaC() == null || l.getTemperaturaC() < -60 || l.getTemperaturaC() > 60)
            erros.add("temperaturaC deve estar entre -60 e 60");
        if (l.getEnviadoEm() == null || l.getEnviadoEm() <= 0) erros.add("enviadoEm é obrigatório");
        return erros;
    }

    public synchronized Map<String, Object> registrar(LeituraTelemetria l, long recebidoEm) {
        Long anterior = ultimaSequencia.get(l.getVeiculo());
        long seq = l.getSequencia();
        if (anterior == null) {
            metricas.registrarSequencia(1, 0);
            ultimaSequencia.put(l.getVeiculo(), seq);
        } else if (seq > anterior) {
            metricas.registrarSequencia(seq - anterior, seq - anterior - 1);
            ultimaSequencia.put(l.getVeiculo(), seq);
            Set<Long> pendentes = lacunas.computeIfAbsent(l.getVeiculo(), k -> new HashSet<>());
            for (long faltante = anterior + 1; faltante < seq && pendentes.size() < LIMITE_LACUNAS; faltante++) {
                pendentes.add(faltante);
            }
        } else {
            // Chegou atrasada (ex.: retransmissão TCP): se preenche uma lacuna, não era perda.
            foraDeOrdem++;
            Set<Long> pendentes = lacunas.get(l.getVeiculo());
            if (pendentes != null && pendentes.remove(seq)) metricas.registrarLacunaRecuperada();
        }

        double[] faixa = FAIXAS.get(l.getTipoHemocomponente());
        boolean conforme = l.getTemperaturaC() >= faixa[0] && l.getTemperaturaC() <= faixa[1];

        Map<String, Object> leitura = new LinkedHashMap<>();
        leitura.put("veiculo", l.getVeiculo());
        leitura.put("sequencia", seq);
        leitura.put("tipoHemocomponente", l.getTipoHemocomponente());
        leitura.put("latitude", l.getLatitude());
        leitura.put("longitude", l.getLongitude());
        leitura.put("temperaturaC", l.getTemperaturaC());
        leitura.put("faixaC", faixa);
        leitura.put("temperaturaConforme", conforme);
        leitura.put("atrasoUnidirecionalMs", Math.max(0, recebidoEm - l.getEnviadoEm()));
        leitura.put("recebidoEm", recebidoEm);
        Map<String, Object> anteriorLeitura = ultimaLeitura.put(l.getVeiculo(), leitura);
        boolean estavaConforme = anteriorLeitura == null || Boolean.TRUE.equals(anteriorLeitura.get("temperaturaConforme"));
        if (!conforme && estavaConforme) eventos.publishEvent(new AlertaTemperatura(leitura));
        return leitura;
    }

    public synchronized Map<String, Object> estado() {
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("veiculos", new ArrayList<>(ultimaLeitura.values()));
        r.put("foraDeOrdem", foraDeOrdem);
        return r;
    }

    public synchronized void reiniciar() {
        ultimaSequencia.clear();
        lacunas.clear();
        ultimaLeitura.clear();
        foraDeOrdem = 0;
    }
}
