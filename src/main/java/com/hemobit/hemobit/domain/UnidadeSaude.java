package com.hemobit.hemobit.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("UNIDADE_SAUDE")
public class UnidadeSaude extends Localidade {

    public UnidadeSaude() {
        super();
    }

    public UnidadeSaude(String nome, String endereco, String cidade, String telefone) {
        super(nome, endereco, cidade, telefone);
    }
}