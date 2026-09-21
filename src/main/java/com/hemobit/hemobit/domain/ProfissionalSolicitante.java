package com.hemobit.hemobit.domain;

import jakarta.persistence.*;

@Entity
@Table(name ="profissional_solicitante")
public class ProfissionalSolicitante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String crmMatricula;

    @Column(nullable = false)
    private String email;

    @ManyToOne
    @JoinColumn(name = "unidade_saude_id", nullable = false)
    private UnidadeSaude unidadeSaude;

    public ProfissionalSolicitante() {
    }

    public ProfissionalSolicitante(
            String nome,
            String crmMatricula,
            String email,
            UnidadeSaude unidadeSaude) {
        this.nome = nome;
        this.crmMatricula = crmMatricula;
        this.email = email;
        this.unidadeSaude = unidadeSaude;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCrmMatricula() {
        return crmMatricula;
    }

    public void setCrmMatricula(String crmMatricula) {
        this.crmMatricula = crmMatricula;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UnidadeSaude getUnidadeSaude() {
        return unidadeSaude;
    }

    public void setUnidadeSaude(UnidadeSaude unidadeSaude) {
        this.unidadeSaude = unidadeSaude;
    }
}

