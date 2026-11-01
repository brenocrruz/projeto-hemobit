package com.hemobit.hemobit.rede;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

class MetricasRedeServiceTest {

    private final AtomicLong agora = new AtomicLong(1_000_000);
    private final MetricasRedeService metricas = new MetricasRedeService(agora::get);

    @Test
    void percentilUsaNearestRank() {
        double[] valores = {10, 20, 30, 40, 50, 60, 70, 80, 90, 100};
        assertEquals(50, MetricasRedeService.percentil(valores, 50));
        assertEquals(100, MetricasRedeService.percentil(valores, 95));
        assertEquals(10, MetricasRedeService.percentil(valores, 1));
    }

    @Test
    void jitterEZeroParaLatenciaConstanteECresceComVariacao() {
        assertEquals(0, MetricasRedeService.jitter(new double[]{5, 5, 5, 5}));
        double j = MetricasRedeService.jitter(new double[]{5, 21, 5, 21});
        assertTrue(j > 0 && j < 16, "jitter suavizado deve ficar entre 0 e a variação máxima");
    }

    @Test
    void jitterECalculadoPorVeiculoSemMisturarFluxos() {
        // Dois veículos estáveis, mas com latências diferentes: intercalados dariam jitter alto.
        for (int i = 0; i < 20; i++) {
            metricas.registrarEnvio("VTR-01", 5, 202, 100);
            metricas.registrarEnvio("VTR-02", 50, 202, 100);
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> t = (Map<String, Object>) metricas.snapshot().get("telemetria");
        assertEquals(0.0, t.get("jitterMs"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void calculaPerdaVazaoEErrosDaTelemetria() {
        for (int i = 0; i < 9; i++) {
            agora.addAndGet(1000);
            metricas.registrarEnvio("VTR-01", 10, 202, 250);
        }
        metricas.registrarEnvio("VTR-01", 2000, 0, 0);
        metricas.registrarEnvio("VTR-01", 12, 400, 100);

        Map<String, Object> t = (Map<String, Object>) metricas.snapshot().get("telemetria");
        assertEquals(11, t.get("amostras"));
        assertEquals(9.09, t.get("perdaPercentual"));
        assertEquals(1L, t.get("erros4xx"));
        assertEquals(10.0, ((Map<String, Object>) t.get("latenciaMs")).get("p95"));
        assertTrue((double) t.get("vazaoMsgPorSegundo") > 0);
    }

    @Test
    @SuppressWarnings("unchecked")
    void descartaAmostrasForaDaJanela() {
        metricas.registrarEnvio("VTR-01", 10, 202, 100);
        agora.addAndGet(MetricasRedeService.JANELA_MS + 1);
        Map<String, Object> t = (Map<String, Object>) metricas.snapshot().get("telemetria");
        assertEquals(0, t.get("amostras"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void agrupaErrosDaApiPorRota() {
        metricas.registrarRequisicao("GET /api/x", 200, 3, 0);
        metricas.registrarRequisicao("GET /api/x", 404, 2, 0);
        metricas.registrarRequisicao("POST /api/y", 500, 9, 0);

        Map<String, Object> api = (Map<String, Object>) metricas.snapshot().get("api");
        assertEquals(1L, api.get("erros4xx"));
        assertEquals(1L, api.get("erros5xx"));
        List<Map<String, Object>> rotas = (List<Map<String, Object>>) api.get("rotas");
        assertEquals("GET /api/x", rotas.get(0).get("rota"));
        assertEquals(2, rotas.get(0).get("requisicoes"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void serieTemSessentaPontosDeUmSegundo() {
        metricas.registrarEnvio("VTR-01", 10, 202, 100);
        List<Map<String, Object>> serie = (List<Map<String, Object>>) metricas.snapshot().get("serie");
        assertEquals(60, serie.size());
        assertEquals(1, serie.get(59).get("envios"));
    }
}
