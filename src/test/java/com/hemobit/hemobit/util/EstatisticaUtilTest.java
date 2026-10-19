package com.hemobit.hemobit.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class EstatisticaUtilTest {

    @Test
    void desabastecimentoComLambda10EEstoque8() {
        assertEquals(0.667, EstatisticaUtil.probabilidadeDesabastecimento(10, 8), 0.001);
    }

    @Test
    void semEstoqueComLambda2() {
        assertEquals(0.865, EstatisticaUtil.probabilidadeDesabastecimento(2, 0), 0.001);
    }

    @Test
    void taxaDescarte() {
        assertEquals(7.0, EstatisticaUtil.taxaDescarte(14, 200), 0.0001);
        assertEquals(0.0, EstatisticaUtil.taxaDescarte(0, 0), 0.0001);
    }

    @Test
    void lambdaDoHorizonte() {
        assertEquals(30.0, EstatisticaUtil.lambdaHorizonte(100, 10, 3), 0.0001);
    }
}