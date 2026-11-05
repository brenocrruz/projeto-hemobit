package com.hemobit.hemobit.rede;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final TelemetriaWebSocketHandler handler;

    public WebSocketConfig(TelemetriaWebSocketHandler handler) {
        this.handler = handler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // Sem setAllowedOrigins: só aceita conexões da mesma origem do painel.
        registry.addHandler(handler, "/ws/telemetria");
    }
}
