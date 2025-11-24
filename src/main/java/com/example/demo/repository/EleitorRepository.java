package com.example.demo.repository;

import com.example.demo.model.Eleitor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EleitorRepository extends JpaRepository<Eleitor, Long> {

    Optional<Eleitor> findByCpfValor(String valor);
}
