package com.hemobit.hemobit.rede;

import com.hemobit.hemobit.telemetria.IngestaoTelemetriaService;
import com.hemobit.hemobit.telemetria.SimuladorTelemetria;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

/** Monta o estado completo do painel de rede, usado pelo endpoint REST e pelo canal WebSocket. */
@Service
public class PainelRedeService {

    private final MetricasRedeService metricas;
    private final IngestaoTelemetriaService ingestao;
    private final SimuladorTelemetria simulador;
    private final boolean controleHabilitado;

    public PainelRedeService(MetricasRedeService metricas, IngestaoTelemetriaService ingestao,
                             SimuladorTelemetria simulador,
                             @Value("${hemobit.rede.controle.habilitado:false}") boolean controleHabilitado) {
        this.metricas = metricas;
        this.ingestao = ingestao;
        this.simulador = simulador;
        this.controleHabilitado = controleHabilitado;
    }

    public Map<String, Object> estado() {
        Map<String, Object> resposta = new LinkedHashMap<>(metricas.snapshot());
        resposta.put("veiculos", ingestao.estado());
        resposta.put("simulador", simulador.estado());
        resposta.put("controleHabilitado", controleHabilitado);
        return resposta;
    }

    public boolean controleHabilitado() {
        return controleHabilitado;
    }

    /** Zera a janela; devolve false quando o controle está desabilitado neste ambiente. */
    public boolean reiniciar() {
        if (!controleHabilitado) return false;
        metricas.reiniciar();
        ingestao.reiniciar();
        return true;
    }
}
