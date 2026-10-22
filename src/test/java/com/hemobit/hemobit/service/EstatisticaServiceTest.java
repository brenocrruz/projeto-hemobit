package com.hemobit.hemobit.service;

import com.hemobit.hemobit.dto.IndicadoresDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class EstatisticaServiceTest {

    private EstatisticaService service;
    private List<Double> dadosExemplo;

    @BeforeEach
    void setUp() {
        service = new EstatisticaService();
        dadosExemplo = Arrays.asList(10.0, 20.0, 20.0, 30.0);
    }

    @Test
    void deveCalcularMedia() {
        assertEquals(20.0, service.calcularMedia(dadosExemplo));
    }

    @Test
    void deveCalcularMediana() {
        assertEquals(20.0, service.calcularMediana(dadosExemplo));
    }

    @Test
    void deveCalcularModa() {
        assertEquals(20.0, service.calcularModa(dadosExemplo));
    }

    @Test
    void deveCalcularDesvioPadraoEVariancia() {
        assertTrue(service.calcularVariancia(dadosExemplo) > 0);
        assertTrue(service.calcularDesvioPadrao(dadosExemplo) > 0);
    }

    @Test
    void deveGerarDtoCompleto() {
        IndicadoresDTO dto = service.gerarRelatorioIndicador("Teste Indicadores", dadosExemplo);
        assertNotNull(dto);
        assertEquals("Teste Indicadores", dto.getNomeIndicador());
        assertEquals(20.0, dto.getMedia());
        assertEquals(Map.of(10.0, 1L, 20.0, 2L, 30.0, 1L), dto.getDistribuicaoFrequencia());
        assertEquals(20.0, dto.getMediana());
        assertEquals(20.0, dto.getModa());
        assertEquals(200.0 / 3, dto.getVariancia(), 0.000001);
        assertEquals(Math.sqrt(200.0 / 3), dto.getDesvioPadrao(), 0.000001);
    }

    @Test
    void deveRetornarDistribuicaoVaziaSemDados() {
        assertEquals(Map.of(), service.gerarRelatorioIndicador("Vazio", List.of()).getDistribuicaoFrequencia());
        assertEquals(Map.of(), service.gerarRelatorioIndicador("Nulo", null).getDistribuicaoFrequencia());
    }

    @Test
    void devePreservarConstrutorExistente() {
        IndicadoresDTO dto = new IndicadoresDTO("Indicador", 1.0, 2.0, 3.0, 4.0, 5.0);
        assertEquals(Map.of(), dto.getDistribuicaoFrequencia());
        assertEquals(1.0, dto.getMedia());
    }
}
