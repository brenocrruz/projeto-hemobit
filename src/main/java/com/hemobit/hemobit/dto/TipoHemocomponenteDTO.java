package com.hemobit.hemobit.dto;

import com.hemobit.hemobit.domain.TipoHemocomponente;

public record TipoHemocomponenteDTO(TipoHemocomponente codigo, String nome, int validadeReferenciaDias) {
    public TipoHemocomponenteDTO(TipoHemocomponente tipo) {
        this(tipo, tipo.getRotulo(), tipo.getValidadeReferenciaDias());
    }
}
