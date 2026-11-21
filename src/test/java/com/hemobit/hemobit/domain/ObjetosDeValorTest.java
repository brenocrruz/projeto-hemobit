package com.hemobit.hemobit.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ObjetosDeValorTest {
    private static final LocalDate PRODUCAO = LocalDate.of(2026, 11, 1);
    private static final LocalDate VALIDADE = LocalDate.of(2026, 12, 6);

    @Test
    void codigoDaBolsaEhNormalizadoEComparadoPeloValor() {
        assertEquals("ABC-1", CodigoBolsa.de("  abc-1 ").valor());
        assertEquals(CodigoBolsa.de("abc-1"), CodigoBolsa.de("ABC-1"));
        assertEquals("ABC-1", CodigoBolsa.de("abc-1").toString());
    }

    @Test
    void codigoDaBolsaRejeitaTextoVazioOuNulo() {
        assertEquals("Informe o código da bolsa.",
                assertThrows(IllegalArgumentException.class, () -> CodigoBolsa.de("  ")).getMessage());
        assertEquals("Informe o código da bolsa.",
                assertThrows(IllegalArgumentException.class, () -> CodigoBolsa.de(null)).getMessage());
    }

    @Test
    void quantidadeAceitaDeUmAteCinquentaBolsasInteiras() {
        assertEquals(1, QuantidadeDeBolsas.de(1).valor());
        assertEquals(50, QuantidadeDeBolsas.de(50.0).valor());
        assertEquals(new QuantidadeDeBolsas(3), QuantidadeDeBolsas.de(3));
    }

    @Test
    void quantidadeRejeitaValoresForaDaRegra() {
        assertEquals("A quantidade solicitada deve ser de no mínimo 1 bolsa.",
                assertThrows(IllegalArgumentException.class, () -> QuantidadeDeBolsas.de(0)).getMessage());
        assertEquals("A quantidade máxima por solicitação é 50.",
                assertThrows(IllegalArgumentException.class, () -> QuantidadeDeBolsas.de(51)).getMessage());
        assertEquals("A quantidade solicitada deve ser um número inteiro de bolsas.",
                assertThrows(IllegalArgumentException.class, () -> QuantidadeDeBolsas.de(2.5)).getMessage());
        assertThrows(IllegalArgumentException.class, () -> new QuantidadeDeBolsas(51));
    }

    @Test
    void periodoDeValidadeExigeDatasCoerentes() {
        assertEquals("Informe as datas de produção e de validade.",
                assertThrows(IllegalArgumentException.class, () -> PeriodoDeValidade.de(null, VALIDADE)).getMessage());
        assertEquals("Data de validade não pode ser anterior à data de produção.",
                assertThrows(IllegalArgumentException.class, () -> PeriodoDeValidade.de(VALIDADE, PRODUCAO)).getMessage());
    }

    @Test
    void periodoDeValidadeSabeSeEstaVencido() {
        PeriodoDeValidade periodo = PeriodoDeValidade.de(PRODUCAO, VALIDADE);

        assertFalse(periodo.estaVencidoEm(VALIDADE));
        assertTrue(periodo.estaVencidoEm(VALIDADE.plusDays(1)));
        assertEquals(PRODUCAO, periodo.producao());
        assertEquals(VALIDADE, periodo.validade());
    }
}
