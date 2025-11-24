package com.example.demo.repository;

import com.example.demo.model.CPF;
import com.example.demo.model.Voto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VotoRepository extends JpaRepository<Voto, Long> {

    boolean existsByCpfEleitor(CPF cpf);
}
