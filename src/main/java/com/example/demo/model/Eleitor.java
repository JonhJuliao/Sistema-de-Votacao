package com.example.demo.model;

import jakarta.persistence.Entity;

@Entity
public class Eleitor extends Pessoa {

    private boolean jaVotou;

    public Eleitor() {
        super();
        this.jaVotou = false;
    }

    public Eleitor(String nome, CPF cpf) {
        super(cpf, nome, 18); // usa idade mínima
        this.jaVotou = false;
    }

    public boolean isJaVotou() {
        return jaVotou;
    }

    public void setJaVotou(boolean jaVotou) {
        this.jaVotou = jaVotou;
    }
}
