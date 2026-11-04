package com.hemobit.hemobit.rede;

import com.hemobit.hemobit.telemetria.AlertaTemperatura;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Canal /ws/telemetria (contratos-api.md, seção 2): empurra o estado do painel a cada segundo
 * (evento METRICAS_REDE) e os alertas de cadeia fria assim que acontecem (TEMPERATURA_FORA_DA_FAIXA).
 * A ingestão continua por HTTP POST; o WebSocket é só o caminho servidor → painel.
 */
@Component
public class TelemetriaWebSocketHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(TelemetriaWebSocketHandler.class);
    private static final int LIMITE_ENVIO_MS = 2000;
    private static final int LIMITE_BUFFER_BYTES = 512 * 1024;

    private final PainelRedeService painel;
    private final ObjectMapper json;
    private final Map<String, WebSocketSession> sessoes = new ConcurrentHashMap<>();

    public TelemetriaWebSocketHandler(PainelRedeService painel, ObjectMapper json) {
        this.painel = painel;
        this.json = json;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession sessao) {
        // O decorator serializa envios concorrentes (agendador + alertas) e descarta clientes lentos.
        sessoes.put(sessao.getId(), new ConcurrentWebSocketSessionDecorator(sessao, LIMITE_ENVIO_MS, LIMITE_BUFFER_BYTES));
        transmitirMetricas();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession sessao, CloseStatus status) {
        sessoes.remove(sessao.getId());
    }

    @Scheduled(fixedRate = 1000)
    public void transmitirMetricas() {
        if (sessoes.isEmpty()) return;
        Map<String, Object> mensagem = new LinkedHashMap<>();
        mensagem.put("evento", "METRICAS_REDE");
        mensagem.putAll(painel.estado());
        transmitir(mensagem);
    }

    @EventListener
    public void alertaTemperatura(AlertaTemperatura alerta) {
        Map<String, Object> mensagem = new LinkedHashMap<>();
        mensagem.put("evento", "TEMPERATURA_FORA_DA_FAIXA");
        mensagem.putAll(alerta.leitura());
        mensagem.put("timestamp", System.currentTimeMillis());
        transmitir(mensagem);
    }

    public int conexoesAtivas() {
        return sessoes.size();
    }

    private void transmitir(Map<String, Object> mensagem) {
        if (sessoes.isEmpty()) return;
        TextMessage texto = new TextMessage(json.writeValueAsString(mensagem));
        sessoes.values().forEach(sessao -> {
            try {
                if (sessao.isOpen()) sessao.sendMessage(texto);
            } catch (IOException | IllegalStateException e) {
                log.debug("Falha ao enviar para a sessão WebSocket {}: {}", sessao.getId(), e.getMessage());
                sessoes.remove(sessao.getId());
            }
        });
    }
}
