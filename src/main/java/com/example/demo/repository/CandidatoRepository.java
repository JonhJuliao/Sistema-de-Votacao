package com.example.demo.repository;

import com.example.demo.model.CPF;
import com.example.demo.model.Candidato;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CandidatoRepository extends JpaRepository<Candidato, Long> {

    boolean existsByCpf(CPF cpf);

    boolean existsByNumero(Integer numero);

    Optional<Candidato> findByCpf(CPF cpf);
}
