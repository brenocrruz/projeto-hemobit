package com.hemobit.hemobit.service;

import com.hemobit.hemobit.dto.IndicadoresDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

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
        assertNotNull(dto.getDistribuicaoFrequencia());
    }
}
