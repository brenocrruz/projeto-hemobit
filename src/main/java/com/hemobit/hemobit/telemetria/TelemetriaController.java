package com.hemobit.hemobit.telemetria;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/telemetria")
public class TelemetriaController {

    private final IngestaoTelemetriaService ingestao;
    private final SimuladorTelemetria simulador;

    public TelemetriaController(IngestaoTelemetriaService ingestao, SimuladorTelemetria simulador) {
        this.ingestao = ingestao;
        this.simulador = simulador;
    }

    @PostMapping("/leituras")
    public ResponseEntity<Map<String, Object>> receber(@RequestBody LeituraTelemetria leitura) {
        long recebidoEm = System.currentTimeMillis();
        List<String> erros = ingestao.validar(leitura);
        if (!erros.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("erros", erros));
        }
        Map<String, Object> registrada = ingestao.registrar(leitura, recebidoEm);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of(
                "sequencia", registrada.get("sequencia"),
                "recebidoEm", recebidoEm,
                "temperaturaConforme", registrada.get("temperaturaConforme")));
    }

    @GetMapping("/veiculos")
    public Map<String, Object> veiculos() {
        return ingestao.estado();
    }

    @GetMapping("/simulador")
    public Map<String, Object> simulador() {
        return simulador.estado();
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> corpoInvalido(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest().body(Map.of("erros", List.of("JSON inválido")));
    }
}
