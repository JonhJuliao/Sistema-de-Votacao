package com.example.demo.model;

import jakarta.persistence.Entity;

@Entity
public class Eleitor extends Pessoa {

    public Eleitor() {
        super();
    }

    public Eleitor(String nome, CPF cpf) {
        super(cpf, nome, 18); // usar uma idade padrão (mínimo para votar)
    }
}
