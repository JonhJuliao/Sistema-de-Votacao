package com.example.demo;

import com.example.demo.model.CPF;
import com.example.demo.model.Candidato;
import com.example.demo.repository.CandidatoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CandidatoServiceTest {

    @Mock
    private CandidatoRepository candidatoRepository;

    @InjectMocks
    private CandidatoService candidatoService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // --------------------------------------------------------------------
    // registrarCandidato
    // --------------------------------------------------------------------

    @Test
    void deveRegistrarCandidatoComSucesso() {
        // arrange
        Candidato candidato = new Candidato();
        candidato.setCpf(new CPF("96196727044"));
        candidato.setNome("Fulano da Silva");
        candidato.setIdade(35);
        candidato.setNumero(1234);
        candidato.setPartido("ABC");

        when(candidatoRepository.existsByCpf(new CPF("96196727044"))).thenReturn(false);
        when(candidatoRepository.existsByNumero(1234)).thenReturn(false);
        when(candidatoRepository.save(any(Candidato.class)))
                .thenAnswer(invocation -> {
                    Candidato c = invocation.getArgument(0);
                    c.setId(1L);
                    return c;
                });

        // act
        Candidato salvo = candidatoService.registrarCandidato(candidato);

        // assert
        assertNotNull(salvo.getId());
        assertEquals("Fulano da Silva", salvo.getNome());
        assertEquals(new CPF("96196727044"), salvo.getCpf());
        assertEquals(35, salvo.getIdade());
        assertEquals(1234, salvo.getNumero());
        assertEquals("ABC", salvo.getPartido());

        verify(candidatoRepository).existsByCpf(new CPF("96196727044"));
        verify(candidatoRepository).existsByNumero(1234);
        verify(candidatoRepository).save(any(Candidato.class));
    }

    @Test
    void naoDeveRegistrarCandidatoComCpfDuplicado() {
        // arrange
        Candidato candidato = new Candidato();
        candidato.setCpf(new CPF("96196727044"));
        candidato.setNome("Fulano da Silva");
        candidato.setIdade(40);
        candidato.setNumero(1234);
        candidato.setPartido("ABC");

        when(candidatoRepository.existsByCpf(new CPF("96196727044"))).thenReturn(true);

        // act + assert
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> candidatoService.registrarCandidato(candidato)
        );

        assertEquals("CPF já cadastrado para outro candidato", ex.getMessage());
        verify(candidatoRepository).existsByCpf(new CPF("96196727044"));
        verify(candidatoRepository, never()).existsByNumero(anyInt());
        verify(candidatoRepository, never()).save(any());
    }

    @Test
    void naoDeveRegistrarCandidatoComNumeroDuplicado() {
        // arrange
        Candidato candidato = new Candidato();
        candidato.setCpf(new CPF("96196727044"));
        candidato.setNome("Ciclano");
        candidato.setIdade(50);
        candidato.setNumero(1234);
        candidato.setPartido("ABC");

        when(candidatoRepository.existsByCpf(new CPF("96196727044"))).thenReturn(false);
        when(candidatoRepository.existsByNumero(1234)).thenReturn(true);

        // act + assert
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> candidatoService.registrarCandidato(candidato)
        );

        assertEquals("Número de candidato já está em uso", ex.getMessage());
        verify(candidatoRepository).existsByCpf(new CPF("96196727044"));
        verify(candidatoRepository).existsByNumero(1234);
        verify(candidatoRepository, never()).save(any());
    }

    @Test
    void naoDeveRegistrarCandidatoMenorDeIdade() {
        // arrange
        Candidato candidato = new Candidato();
        candidato.setCpf(new CPF("96196727044"));
        candidato.setNome("Jovem");
        candidato.setIdade(16); // menor de idade
        candidato.setNumero(5555);
        candidato.setPartido("XYZ");

        // act + assert
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> candidatoService.registrarCandidato(candidato)
        );

        assertEquals("Candidato deve ser maior de idade", ex.getMessage());
        verify(candidatoRepository, never()).existsByCpf((new CPF("96196727044")));
        verify(candidatoRepository, never()).existsByNumero(anyInt());
        verify(candidatoRepository, never()).save(any());
    }

    // --------------------------------------------------------------------
    // listarCandidatoPorCPF
    // --------------------------------------------------------------------

    @Test
    void deveListarCandidatoPorCpfQuandoExistir() {
        Candidato candidato = new Candidato();
        candidato.setId(1L);
        candidato.setCpf(new CPF("96196727044"));
        candidato.setNome("Fulano");
        candidato.setIdade(30);
        candidato.setNumero(1234);
        candidato.setPartido("ABC");

        when(candidatoRepository.findByCpf(new CPF("96196727044")))
                .thenReturn(Optional.of(candidato));

        Optional<Candidato> resultado =
                candidatoService.listarCandidatoPorCPF(new CPF("96196727044"));

        assertTrue(resultado.isPresent());
        assertEquals("Fulano", resultado.get().getNome());
        assertEquals(1234, resultado.get().getNumero());
        verify(candidatoRepository).findByCpf(new CPF("96196727044"));
    }

    @Test
    void deveRetornarVazioQuandoNaoEncontrarCandidatoPorCpf() {
        when(candidatoRepository.findByCpf(new CPF("96196727044")))
                .thenReturn(Optional.empty());

        Optional<Candidato> resultado =
                candidatoService.listarCandidatoPorCPF(new CPF("96196727044"));

        assertTrue(resultado.isEmpty());
        verify(candidatoRepository).findByCpf(new CPF("96196727044"));
    }

    // --------------------------------------------------------------------
    // listarTodosOsCandidatos
    // --------------------------------------------------------------------

    @Test
    void deveListarTodosOsCandidatos() {
        List<Candidato> candidatos = List.of(
                criarCandidato(1L, new CPF("96196727044"), "Fulano", 40, 10, "ABC"),
                criarCandidato(2L, new CPF("96196727044"), "Ciclano", 50, 20, "XYZ")
        );

        when(candidatoRepository.findAll()).thenReturn(candidatos);

        List<Candidato> resultado = candidatoService.listarTodosOsCandidatos();

        assertEquals(2, resultado.size());
        verify(candidatoRepository).findAll();
    }

    // --------------------------------------------------------------------
    // atualizarRegistroDeCandidato
    // --------------------------------------------------------------------

    @Test
    void deveAtualizarRegistroDeCandidatoPorCpf() {
        CPF cpf = new CPF("96196727044");

        Candidato existente = criarCandidato(1L, cpf, "Fulano", 40, 10, "ABC");
        Candidato novosDados = new Candidato();
        novosDados.setNome("Fulano Atualizado");
        novosDados.setIdade(45);
        novosDados.setNumero(99);
        novosDados.setPartido("DEF");

        when(candidatoRepository.findByCpf(cpf))
                .thenReturn(Optional.of(existente));
        when(candidatoRepository.existsByNumero(99)).thenReturn(false);
        when(candidatoRepository.save(any(Candidato.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Candidato atualizado =
                candidatoService.atualizarRegistroDeCandidato(cpf, novosDados);

        assertEquals("Fulano Atualizado", atualizado.getNome());
        assertEquals(45, atualizado.getIdade());
        assertEquals(99, atualizado.getNumero());
        assertEquals("DEF", atualizado.getPartido());
        verify(candidatoRepository).findByCpf(cpf);
        verify(candidatoRepository).existsByNumero(99);
        verify(candidatoRepository).save(existente);
    }

    @Test
    void deveLancarErroAoTentarAtualizarCandidatoInexistente() {
        CPF cpf = new CPF("96196727044");
        Candidato novosDados = new Candidato();
        novosDados.setNome("Novo");
        novosDados.setNumero(50);
        novosDados.setPartido("ABC");

        when(candidatoRepository.findByCpf(cpf))
                .thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> candidatoService.atualizarRegistroDeCandidato(cpf, novosDados)
        );

        assertEquals("Candidato não encontrado para o CPF informado", ex.getMessage());
        verify(candidatoRepository).findByCpf(cpf);
        verify(candidatoRepository, never()).save(any());
    }

    // --------------------------------------------------------------------
    // helper
    // --------------------------------------------------------------------

    private Candidato criarCandidato(Long id, CPF cpf, String nome,
                                     int idade, int numero, String partido) {
        Candidato c = new Candidato();
        c.setId(id);
        c.setCpf(cpf);
        c.setNome(nome);
        c.setIdade(idade);
        c.setNumero(numero);
        c.setPartido(partido);
        return c;
    }
}
