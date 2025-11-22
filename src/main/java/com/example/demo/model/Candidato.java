package com.example.demo.model;

import jakarta.persistence.Entity;

@Entity
public class Candidato extends Pessoa {

    private Integer numero;

    private String partido;

    public Candidato(CPF cpf, String nome, int idade) {
        super(cpf, nome, idade);
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public String getPartido() {
        return partido;
    }

    public void setPartido(String partido) {
        this.partido = partido;
    }
}
