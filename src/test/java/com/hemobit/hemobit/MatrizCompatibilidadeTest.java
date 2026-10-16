package com.hemobit.hemobit;

import com.hemobit.hemobit.aed.estruturas.MatrizCompatibilidade;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MatrizCompatibilidadeTest {

    @Test
    void devePermitirO_NegativoDoarParaTodosCenarioPositivo() {
        assertTrue(MatrizCompatibilidade.isCompativel(0, 7)); 
        assertTrue(MatrizCompatibilidade.isCompativel(4, 7)); 
    }

    @Test
    void naoDevePermitirO_NegativoReceberDeOutrosCenarioNegativo() {
        assertFalse(MatrizCompatibilidade.isCompativel(7, 0)); 
        assertFalse(MatrizCompatibilidade.isCompativel(7, 6)); 
    }

    @Test
    void devePermitirAB_PositivoReceberDeTodosCenarioPositivo() {
        assertTrue(MatrizCompatibilidade.isCompativel(4, 3));
    }
}