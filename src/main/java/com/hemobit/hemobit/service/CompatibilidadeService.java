package com.hemobit.hemobit.service;

import com.hemobit.hemobit.aed.estruturas.MatrizCompatibilidade;
import com.hemobit.hemobit.domain.TipoSanguineo;
import org.springframework.stereotype.Service;

@Service
public class CompatibilidadeService {
    public boolean compativel(TipoSanguineo receptor, TipoSanguineo doador) {
        if (receptor == null || doador == null) {
            throw new IllegalArgumentException("Tipo sanguíneo não informado.");
        }
        return MatrizCompatibilidade.isCompativel(indice(receptor), indice(doador));
    }

    static int indice(TipoSanguineo tipo) {
        return switch (tipo) {
            case A_POSITIVO -> 0;
            case A_NEGATIVO -> 1;
            case B_POSITIVO -> 2;
            case B_NEGATIVO -> 3;
            case AB_POSITIVO -> 4;
            case AB_NEGATIVO -> 5;
            case O_POSITIVO -> 6;
            case O_NEGATIVO -> 7;
        };
    }
}
