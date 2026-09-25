package com.hemobit.hemobit.controller;

import com.hemobit.hemobit.dto.IndicadoresDTO;
import com.hemobit.hemobit.service.EstatisticaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/indicadores")
@CrossOrigin(origins = "*")
public class IndicadoresController {

    private final EstatisticaService estatisticaService;

    public IndicadoresController(EstatisticaService estatisticaService) {
        this.estatisticaService = estatisticaService;
    }

    @GetMapping("/estoque-tipo")
    public ResponseEntity<Map<String, Object>> getEstoquePorTipo() {
        Map<String, Object> resposta = new LinkedHashMap<>();
        Map<String, Long> estoque = new LinkedHashMap<>();
        estoque.put("O-", 150L);
        estoque.put("O+", 210L);
        estoque.put("A+", 180L);
        estoque.put("A-", 45L);
        estoque.put("B+", 90L);
        estoque.put("B-", 30L);
        estoque.put("AB+", 25L);
        estoque.put("AB-", 10L);

        List<Double> valores = Arrays.asList(150.0, 210.0, 180.0, 45.0, 90.0, 30.0, 25.0, 10.0);
        IndicadoresDTO estatisticas = estatisticaService.gerarRelatorioIndicador("Estoque por Tipo Sanguineo", valores);

        resposta.put("dados", estoque);
        resposta.put("estatisticas", estatisticas);
        return ResponseEntity.ok(resposta);
    }

    @GetMapping("/demanda-hospital")
    public ResponseEntity<Map<String, Object>> getDemandaPorHospital() {
        Map<String, Object> resposta = new LinkedHashMap<>();
        Map<String, Long> demanda = new LinkedHashMap<>();
        demanda.put("Hospital 01", 120L);
        demanda.put("Hospital 02", 85L);
        demanda.put("Hospital 03", 140L);
        demanda.put("Hospital 04", 40L);
        demanda.put("Hospital 05", 95L);
        demanda.put("Hospital 06", 110L);

        List<Double> valores = Arrays.asList(120.0, 85.0, 140.0, 40.0, 95.0, 110.0);
        IndicadoresDTO estatisticas = estatisticaService.gerarRelatorioIndicador("Demanda por Hospital", valores);

        resposta.put("dados", demanda);
        resposta.put("estatisticas", estatisticas);
        return ResponseEntity.ok(resposta);
    }

    @GetMapping("/tempos-atendimento")
    public ResponseEntity<IndicadoresDTO> getTemposAtendimento() {
        List<Double> tempos = Arrays.asList(7.5, 22.5, 13.5, 33.0, 4.5, 27.0);
        return ResponseEntity.ok(estatisticaService.gerarRelatorioIndicador("Tempos de Atendimento (min)", tempos));
    }
}
