package com.example.demo;

import com.example.demo.model.CPF;
import com.example.demo.model.Candidato;
import com.example.demo.repository.CandidatoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CandidatoService {

    private final CandidatoRepository candidatoRepository;

    public CandidatoService(CandidatoRepository candidatoRepository) {
        this.candidatoRepository = candidatoRepository;
    }

    // ---------------------------------------------------------------------
    // registrarCandidato
    // ---------------------------------------------------------------------
    public Candidato registrarCandidato(Candidato candidato) {

        if (candidato.getIdade() < 18) {
            throw new IllegalArgumentException("Candidato deve ser maior de idade");
        }

        CPF cpf = candidato.getCpf();

        if (candidatoRepository.existsByCpf(cpf)) {
            throw new IllegalArgumentException("CPF já cadastrado para outro candidato");
        }

        if (candidatoRepository.existsByNumero(candidato.getNumero())) {
            throw new IllegalArgumentException("Número de candidato já está em uso");
        }

        return candidatoRepository.save(candidato);
    }

    // ---------------------------------------------------------------------
    // listarCandidatoPorCPF
    // ---------------------------------------------------------------------
    public Optional<Candidato> listarCandidatoPorCPF(CPF cpf) {
        return candidatoRepository.findByCpf(cpf);
    }

    // ---------------------------------------------------------------------
    // listarTodosOsCandidatos
    // ---------------------------------------------------------------------
    public List<Candidato> listarTodosOsCandidatos() {
        return candidatoRepository.findAll();
    }

    // ---------------------------------------------------------------------
    // atualizarRegistroDeCandidato
    // ---------------------------------------------------------------------
    public Candidato atualizarRegistroDeCandidato(CPF cpf, Candidato novosDados) {

        Candidato existente = candidatoRepository.findByCpf(cpf)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Candidato não encontrado para o CPF informado"
                ));

        // Atualiza campos
        existente.setNome(novosDados.getNome());
        existente.setIdade(novosDados.getIdade());
        existente.setPartido(novosDados.getPartido());

        // Se o número mudou, validar antes de atualizar
        if (!existente.getNumero().equals(novosDados.getNumero())) {

            if (candidatoRepository.existsByNumero(novosDados.getNumero())) {
                throw new IllegalArgumentException("Número de candidato já está em uso");
            }

            existente.setNumero(novosDados.getNumero());
        }

        return candidatoRepository.save(existente);
    }
}
