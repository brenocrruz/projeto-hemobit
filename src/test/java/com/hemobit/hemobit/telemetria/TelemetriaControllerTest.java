package com.hemobit.hemobit.telemetria;

import com.hemobit.hemobit.rede.MetricasRedeService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TelemetriaControllerTest {

    private final MetricasRedeService metricas = new MetricasRedeService();
    private final List<Object> eventos = new ArrayList<>();
    private final IngestaoTelemetriaService ingestao = new IngestaoTelemetriaService(metricas, eventos::add);
    private final SimuladorTelemetria simulador = new SimuladorTelemetria(metricas, false,
            "http://localhost:8080/api/v1/telemetria/leituras", 2000);
    private final TelemetriaController controller = new TelemetriaController(ingestao, simulador);

    private static LeituraTelemetria leitura(long seq, double temperatura) {
        LeituraTelemetria l = new LeituraTelemetria();
        l.setVeiculo("VTR-01");
        l.setSequencia(seq);
        l.setTipoHemocomponente("CH");
        l.setLatitude(-8.05);
        l.setLongitude(-34.88);
        l.setTemperaturaC(temperatura);
        l.setEnviadoEm(System.currentTimeMillis());
        return l;
    }

    @Test
    void aceitaLeituraValidaComStatus202() {
        ResponseEntity<Map<String, Object>> resposta = controller.receber(leitura(0, 4.0));
        assertEquals(HttpStatus.ACCEPTED, resposta.getStatusCode());
        assertEquals(true, resposta.getBody().get("temperaturaConforme"));
    }

    @Test
    void rejeitaLeituraSemTemperaturaComStatus400() {
        LeituraTelemetria l = leitura(0, 4.0);
        l.setTemperaturaC(null);
        ResponseEntity<Map<String, Object>> resposta = controller.receber(l);
        assertEquals(HttpStatus.BAD_REQUEST, resposta.getStatusCode());
        assertFalse(((List<?>) resposta.getBody().get("erros")).isEmpty());
    }

    @Test
    void sinalizaTemperaturaForaDaFaixaDaCadeiaFria() {
        ResponseEntity<Map<String, Object>> resposta = controller.receber(leitura(0, 9.5));
        assertEquals(false, resposta.getBody().get("temperaturaConforme"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void detectaLacunaDeSequenciaComoPerda() {
        controller.receber(leitura(0, 4.0));
        controller.receber(leitura(1, 4.0));
        controller.receber(leitura(4, 4.0));
        Map<String, Object> totais = (Map<String, Object>) metricas.snapshot().get("totais");
        assertEquals(5L, totais.get("esperadosSequencia"));
        assertEquals(2L, totais.get("perdidosSequencia"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void leituraAtrasadaQuePreencheLacunaNaoContaComoPerda() {
        controller.receber(leitura(0, 4.0));
        controller.receber(leitura(2, 4.0));
        controller.receber(leitura(1, 4.0));
        Map<String, Object> totais = (Map<String, Object>) metricas.snapshot().get("totais");
        assertEquals(0L, totais.get("perdidosSequencia"));
        assertEquals(1L, ingestao.estado().get("foraDeOrdem"));
    }

    @Test
    void publicaAlertaSoNaTransicaoParaForaDaFaixa() {
        controller.receber(leitura(0, 4.0));
        controller.receber(leitura(1, 9.0));
        controller.receber(leitura(2, 9.5));
        controller.receber(leitura(3, 4.0));
        controller.receber(leitura(4, 8.0));
        assertEquals(2, eventos.size(), "um alerta por saída da faixa, não um por leitura");
        assertInstanceOf(AlertaTemperatura.class, eventos.get(0));
    }
}
