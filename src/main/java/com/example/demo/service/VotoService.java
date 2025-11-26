package com.example.demo.service;

import com.example.demo.model.CPF;
import com.example.demo.model.Candidato;
import com.example.demo.model.Voto;
import com.example.demo.repository.CandidatoRepository;
import com.example.demo.repository.VotoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class VotoService {

    private final VotoRepository votoRepository;
    private final CandidatoRepository candidatoRepository;

    public VotoService(VotoRepository votoRepository, CandidatoRepository candidatoRepository) {
        this.votoRepository = votoRepository;
        this.candidatoRepository = candidatoRepository;
    }

    public Voto registrarVoto(CPF cpf, Integer numeroCandidato) {

        validaSeVotou(cpf);

        Candidato candidato = buscarCandidato(numeroCandidato);

        Voto voto = new Voto(cpf, candidato);
        return votoRepository.save(voto);
    }
    
    private void validaSeVotou(CPF cpf) {
        if (votoRepository.existsByCpfEleitor(cpf)) {
            throw new IllegalArgumentException("Eleitor já votou");
        }
    }

    private Candidato buscarCandidato(Integer numero) {
        return candidatoRepository.findByNumero(numero)
                .orElseThrow(() -> new IllegalArgumentException("Candidato não encontrado"));
    }

    public Map<Candidato, Long> apurarVotos() {

        List<Voto> votos = votoRepository.findAll();

        return votos.stream()
                .collect(Collectors.groupingBy(
                        Voto::getCandidato,
                        Collectors.counting()
                ));
    }

}
