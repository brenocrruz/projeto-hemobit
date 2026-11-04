package com.hemobit.hemobit.rede;

import com.hemobit.hemobit.telemetria.IngestaoTelemetriaService;
import com.hemobit.hemobit.telemetria.SimuladorTelemetria;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

class PainelRedeServiceTest {

    private RedeController controller(boolean controleHabilitado) {
        MetricasRedeService metricas = new MetricasRedeService();
        PainelRedeService painel = new PainelRedeService(metricas,
                new IngestaoTelemetriaService(metricas, evento -> { }),
                new SimuladorTelemetria(metricas, false, "http://localhost:8080/api/v1/telemetria/leituras", 2000),
                controleHabilitado);
        return new RedeController(painel);
    }

    @Test
    void reiniciarEProibidoQuandoControleDesabilitado() {
        assertEquals(HttpStatus.FORBIDDEN, controller(false).reiniciar().getStatusCode());
    }

    @Test
    void reiniciarFuncionaNoAmbienteLocal() {
        assertEquals(HttpStatus.NO_CONTENT, controller(true).reiniciar().getStatusCode());
    }

    @Test
    void estadoInformaSimuladorDesligadoPorPadrao() {
        var estado = controller(false).metricas().getBody();
        assertNotNull(estado);
        assertEquals(false, ((java.util.Map<?, ?>) estado.get("simulador")).get("habilitado"));
        assertEquals(false, estado.get("controleHabilitado"));
    }
}
