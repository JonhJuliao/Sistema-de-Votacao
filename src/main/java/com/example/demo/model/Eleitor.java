package com.example.demo.model;

import jakarta.persistence.Entity;

@Entity
public class Eleitor extends Pessoa {

    // 1. Cria a constante (Refactor)
    private static final int IDADE_PADRAO_ELEITOR = 18;
    
    private boolean jaVotou;

    public Eleitor() {
        super();
        this.jaVotou = false;
    }

    public Eleitor(String nome, CPF cpf) {
        // 2. Usa a constante aqui
        super(cpf, nome, IDADE_PADRAO_ELEITOR); 
        this.jaVotou = false;
    }

    public boolean isJaVotou() {
        return jaVotou;
    }

    public void setJaVotou(boolean jaVotou) {
        this.jaVotou = jaVotou;
    }
}