package com.hemobit.hemobit.domain;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("UNIDADE_SAUDE")
public class UnidadeSaude extends Localidade {
    @Column(nullable = false)
    private String cnpj;

    public UnidadeSaude() {
        super();
    }

    public UnidadeSaude(
            String nome,
            String endereco,
            String cidade,
            String telefone) {

        super(nome, endereco, cidade, telefone);
    }

    //sobrecarga de constructor
    public UnidadeSaude(
            String nome,
            String endereco,
            String cidade,
            String telefone,
            String cnpj) {

        super(nome, endereco, cidade, telefone);
        this.cnpj = cnpj;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }
}
