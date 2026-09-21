package com.hemobit.hemobit.service;

import com.hemobit.hemobit.dto.IndicadoresDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EstatisticaServiceTest {

    private EstatisticaService estatisticaService;

    @BeforeEach
    void setUp() {
        estatisticaService = new EstatisticaService();
    }

    @Test
    @DisplayName("Deve calcular corretamente a média estatística")
    void deveCalcularMedia() {
        List<Double> valores = Arrays.asList(10.0, 20.0, 30.0);
        double media = estatisticaService.calcularMedia(valores);
        assertEquals(20.0, media);
    }

    @Test
    @DisplayName("Deve calcular corretamente a mediana para lista ímpar e par")
    void deveCalcularMediana() {
        List<Double> valoresImpar = Arrays.asList(5.0, 1.0, 9.0);
        assertEquals(5.0, estatisticaService.calcularMediana(valoresImpar));

        List<Double> valoresPar = Arrays.asList(10.0, 20.0, 30.0, 40.0);
        assertEquals(25.0, estatisticaService.calcularMediana(valoresPar));
    }

    @Test
    @DisplayName("Deve calcular a moda corretamente")
    void deveCalcularModa() {
        List<Double> valores = Arrays.asList(4.0, 5.0, 4.0, 6.0, 4.0);
        assertEquals(4.0, estatisticaService.calcularModa(valores));
    }

    @Test
    @DisplayName("Deve calcular variância e desvio padrão amostragem")
    void deveCalcularVarianciaEDesvioPadrao() {
        List<Double> valores = Arrays.asList(10.0, 12.0, 23.0, 23.0, 16.0, 23.0, 21.0, 16.0);
        assertTrue(estatisticaService.calcularVariancia(valores) > 0);
        assertTrue(estatisticaService.calcularDesvioPadrao(valores) > 0);
    }

    @Test
    @DisplayName("Deve retornar DTO completo preenchido")
    void deveGerarRelatorioDTO() {
        List<Double> valores = Arrays.asList(2.0, 4.0, 4.0, 4.0, 5.0, 5.0, 7.0, 9.0);
        IndicadoresDTO dto = estatisticaService.gerarRelatorioEstatistico("Teste", valores);

        assertNotNull(dto);
        assertEquals("Teste", dto.getNomeIndicador());
        assertEquals(5.0, dto.getMedia());
        assertEquals(4.5, dto.getMediana());
        assertEquals(4.0, dto.getModa());
    }
}
