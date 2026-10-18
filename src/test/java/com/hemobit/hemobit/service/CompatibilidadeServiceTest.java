package com.hemobit.hemobit.service;

import com.hemobit.hemobit.domain.TipoSanguineo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompatibilidadeServiceTest {
    private final CompatibilidadeService servico = new CompatibilidadeService();

    @Test
    void deveBaterComARegraABORhNosSessentaEQuatroPares() {
        for (TipoSanguineo receptor : TipoSanguineo.values()) {
            for (TipoSanguineo doador : TipoSanguineo.values()) {
                assertEquals(regra(receptor, doador), servico.compativel(receptor, doador),
                        "receptor " + receptor + " <- doador " + doador);
            }
        }
    }

    @Test
    void oNegativoDoaParaTodosEOPositivoSoRecebeOs() {
        for (TipoSanguineo receptor : TipoSanguineo.values()) {
            assertTrue(servico.compativel(receptor, TipoSanguineo.O_NEGATIVO));
        }
        assertTrue(servico.compativel(TipoSanguineo.O_NEGATIVO, TipoSanguineo.O_NEGATIVO));
        assertFalse(servico.compativel(TipoSanguineo.O_NEGATIVO, TipoSanguineo.O_POSITIVO));
        assertFalse(servico.compativel(TipoSanguineo.O_NEGATIVO, TipoSanguineo.A_NEGATIVO));
    }

    @Test
    void abPositivoRecebeDeTodos() {
        for (TipoSanguineo doador : TipoSanguineo.values()) {
            assertTrue(servico.compativel(TipoSanguineo.AB_POSITIVO, doador));
        }
    }

    @Test
    void deveRejeitarTipoNulo() {
        assertThrows(IllegalArgumentException.class, () -> servico.compativel(null, TipoSanguineo.A_POSITIVO));
        assertThrows(IllegalArgumentException.class, () -> servico.compativel(TipoSanguineo.A_POSITIVO, null));
    }

    private boolean regra(TipoSanguineo receptor, TipoSanguineo doador) {
        boolean doadorTemA = doador.name().startsWith("A");
        boolean doadorTemB = doador.name().startsWith("B") || doador.name().startsWith("AB");
        boolean doadorPositivo = doador.name().endsWith("_POSITIVO");
        boolean receptorTemA = receptor.name().startsWith("A");
        boolean receptorTemB = receptor.name().startsWith("B") || receptor.name().startsWith("AB");
        boolean receptorPositivo = receptor.name().endsWith("_POSITIVO");
        return (!doadorTemA || receptorTemA) && (!doadorTemB || receptorTemB)
                && (!doadorPositivo || receptorPositivo);
    }
}
