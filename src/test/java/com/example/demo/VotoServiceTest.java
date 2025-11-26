package com.example.demo;

import com.example.demo.model.CPF;
import com.example.demo.model.Candidato;
import com.example.demo.model.Voto;
import com.example.demo.repository.CandidatoRepository;
import com.example.demo.repository.VotoRepository;

import com.example.demo.service.VotoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VotoServiceTest {

    @Mock
    private VotoRepository votoRepository;

    @Mock
    private CandidatoRepository candidatoRepository;

    @InjectMocks
    private VotoService votoService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }


    // ---------------------------------------------
    // Eleitor não pode votar duas vezes
    // ---------------------------------------------
    @Test
    void deveImpedirVotoDuplicado() {
        CPF cpf = new CPF("96196727044");

        when(votoRepository.existsByCpfEleitor(cpf))
                .thenReturn(true);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> votoService.registrarVoto(cpf, 10)
        );

        assertEquals("Eleitor já votou", ex.getMessage());
        verify(votoRepository).existsByCpfEleitor(cpf);
        verify(votoRepository, never()).save(any());
    }


    // ---------------------------------------------
    // Deve registrar voto corretamente
    // ---------------------------------------------
    @Test
    void deveRegistrarVotoComSucesso() {
        CPF cpf = new CPF("11144477735");

        Candidato candidato = new Candidato();
        candidato.setId(1L);
        candidato.setNumero(55);
        candidato.setNome("Fulano");

        when(votoRepository.existsByCpfEleitor(cpf))
                .thenReturn(false);

        when(candidatoRepository.findByNumero(55))
                .thenReturn(Optional.of(candidato));

        when(votoRepository.save(any(Voto.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Voto voto = votoService.registrarVoto(cpf, 55);

        assertEquals(cpf, voto.getCpfEleitor());
        assertEquals(candidato, voto.getCandidato());
        verify(votoRepository).save(any(Voto.class));
    }


    // ---------------------------------------------
    // Falha ao votar em candidato inexistente
    // ---------------------------------------------
    @Test
    void deveFalharSeCandidatoNaoExiste() {
        CPF cpf = new CPF("11144477735");

        when(votoRepository.existsByCpfEleitor(cpf))
                .thenReturn(false);

        when(candidatoRepository.findByNumero(9999))
                .thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> votoService.registrarVoto(cpf, 9999)
        );

        assertEquals("Candidato não encontrado", ex.getMessage());

        verify(votoRepository, never()).save(any());
    }
}
