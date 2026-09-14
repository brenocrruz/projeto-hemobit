package com.hemobit.hemobit.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("UNIDADE_COLETA")
public class UnidadeColeta extends Localidade {

    public UnidadeColeta() {
        super();
    }

    public UnidadeColeta(String nome, String endereco, String cidade, String telefone) {
        super(nome, endereco, cidade, telefone);
    }
}