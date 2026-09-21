package com.hemobit.hemobit.controller;

import com.hemobit.hemobit.dto.IndicadoresDTO;
import com.hemobit.hemobit.service.EstatisticaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/indicadores")
public class IndicadoresController {

    private final EstatisticaService estatisticaService;

    public IndicadoresController(EstatisticaService estatisticaService) {
        this.estatisticaService = estatisticaService;
    }

    @GetMapping("/tempos-atendimento")
    public ResponseEntity<IndicadoresDTO> getIndicadoresTempoAtendimento() {
        List<Double> temposAtendimento = Arrays.asList(45.0, 52.0, 38.0, 60.0, 48.0, 52.0, 30.0, 45.0, 50.0);
        IndicadoresDTO dto = estatisticaService.gerarRelatorioEstatistico("Tempo de Atendimento (min)", temposAtendimento);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/telemetria-temperatura")
    public ResponseEntity<IndicadoresDTO> getIndicadoresTemperatura() {
        List<Double> temperaturas = Arrays.asList(4.5, 5.0, 4.8, 5.2, 4.2, 4.8, 6.1, 3.9, 4.8);
        IndicadoresDTO dto = estatisticaService.gerarRelatorioEstatistico("Temperatura da Cadeia Fria (°C)", temperaturas);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/validade-estoque")
    public ResponseEntity<IndicadoresDTO> getIndicadoresValidadeEstoque() {
        List<Double> diasValidade = Arrays.asList(12.0, 5.0, 20.0, 35.0, 7.0, 14.0, 21.0, 5.0, 10.0);
        IndicadoresDTO dto = estatisticaService.gerarRelatorioEstatistico("Validade Restante (dias)", diasValidade);
        return ResponseEntity.ok(dto);
    }
}
