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

import java.util.List;
import java.util.Map;
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

    @Test
    void deveApurarTotalDeVotosPorCandidato() {
        // arrange
        Candidato candidato1 = new Candidato();
        candidato1.setId(1L);
        candidato1.setNumero(10);

        Candidato candidato2 = new Candidato();
        candidato2.setId(2L);
        candidato2.setNumero(20);

        Voto voto1 = new Voto(new CPF("63043446003"), candidato1);
        Voto voto2 = new Voto(new CPF("89127929035"), candidato1);
        Voto voto3 = new Voto(new CPF("64829424079"), candidato2);

        when(votoRepository.findAll()).thenReturn(List.of(voto1, voto2, voto3));

        // act
        Map<Candidato, Long> resultado = votoService.apurarVotos();

        // assert
        assertEquals(2L, resultado.get(candidato1));
        assertEquals(1L, resultado.get(candidato2));
        assertEquals(2, resultado.size()); // dois candidatos distintos
    }

    @Test
    void deveRetornarMapaVazioQuandoNaoExistemVotos() {
        when(votoRepository.findAll()).thenReturn(List.of());

        Map<Candidato, Long> resultado = votoService.apurarVotos();

        assertTrue(resultado.isEmpty());
    }

    @Test
    void deveApurarCorretamenteQuandoTodosVotosSaoDeUmCandidato() {
        Candidato candidato = new Candidato();
        candidato.setId(1L);
        candidato.setNumero(10);

        Voto v1 = new Voto(new CPF("63043446003"), candidato);
        Voto v2 = new Voto(new CPF("89127929035"), candidato);
        Voto v3 = new Voto(new CPF("64829424079"), candidato);

        when(votoRepository.findAll()).thenReturn(List.of(v1, v2, v3));

        Map<Candidato, Long> resultado = votoService.apurarVotos();

        assertEquals(3L, resultado.get(candidato));
        assertEquals(1, resultado.size());
    }

}
