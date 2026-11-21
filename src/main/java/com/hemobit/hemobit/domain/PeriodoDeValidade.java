package com.hemobit.hemobit.domain;

import java.time.LocalDate;

public record PeriodoDeValidade(LocalDate producao, LocalDate validade) {
    public PeriodoDeValidade {
        if (producao == null || validade == null) {
            throw new IllegalArgumentException("Informe as datas de produção e de validade.");
        }
        if (validade.isBefore(producao)) {
            throw new IllegalArgumentException("Data de validade não pode ser anterior à data de produção.");
        }
    }

    public static PeriodoDeValidade de(LocalDate producao, LocalDate validade) {
        return new PeriodoDeValidade(producao, validade);
    }

    public boolean estaVencidoEm(LocalDate hoje) {
        return validade.isBefore(hoje);
    }
}
