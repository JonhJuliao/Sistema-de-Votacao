package com.example.demo.model;

import jakarta.persistence.*;

@Entity
public class Voto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private CPF cpfEleitor;

    @ManyToOne
    private Candidato candidato;

    public Long getId() {
        return id;
    }

    public CPF getCpfEleitor() {
        return cpfEleitor;
    }

    public Candidato getCandidato() {
        return candidato;
    }
    
    protected Voto() {	
    }

    public Voto(CPF cpf, Candidato candidato) {
        this.cpfEleitor = cpf;
        this.candidato = candidato;
    }

}
