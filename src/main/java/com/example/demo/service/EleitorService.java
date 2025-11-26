package com.example.demo.service;

import com.example.demo.model.CPF;
import com.example.demo.model.Eleitor;
import com.example.demo.repository.EleitorRepository;
import org.springframework.stereotype.Service;

@Service
public class EleitorService {

    private final EleitorRepository repo;

    public EleitorService(EleitorRepository repo) {
        this.repo = repo;
    }

    public Eleitor cadastrar(String nome, CPF cpf) {

        // verifica duplicidade usando o método correto
        if (repo.findByCpfValor(cpf.getValor()).isPresent()) {
            throw new IllegalArgumentException("CPF já cadastrado");
        }

        // cria e retorna o eleitor (não salva)
        return new Eleitor(nome, cpf);
    }
}
