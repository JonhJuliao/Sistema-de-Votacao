package com.example.demo;

import com.example.demo.model.CPF;
import com.example.demo.model.Candidato;
import com.example.demo.model.Voto;
import com.example.demo.repository.CandidatoRepository;
import com.example.demo.repository.VotoRepository;
import org.springframework.stereotype.Service;

@Service
public class VotoService {

    private final VotoRepository votoRepository;
    private final CandidatoRepository candidatoRepository;

    public VotoService(VotoRepository votoRepository, CandidatoRepository candidatoRepository) {
        this.votoRepository = votoRepository;
        this.candidatoRepository = candidatoRepository;
    }

    public Voto registrarVoto(CPF cpf, Integer numeroCandidato) {

        // Regra 1: eleitor só vota uma vez
        if (votoRepository.existsByCpfEleitor(cpf)) {
            throw new IllegalArgumentException("Eleitor já votou");
        }

        // Regra 2: candidato deve existir
        Candidato candidato = candidatoRepository.findByNumero(numeroCandidato)
                .orElseThrow(() -> new IllegalArgumentException("Candidato não encontrado"));

        // Regra 3: criar voto corretamente
        Voto voto = new Voto();
        voto.setCpfEleitor(cpf);
        voto.setCandidato(candidato);

        return votoRepository.save(voto);
    }
}
