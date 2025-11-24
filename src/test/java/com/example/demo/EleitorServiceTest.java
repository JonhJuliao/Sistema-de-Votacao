package com.example.demo;

import com.example.demo.model.CPF;
import com.example.demo.model.Eleitor;
import com.example.demo.repository.EleitorRepository;
import com.example.demo.service.EleitorService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EleitorServiceTest {

    @Test
    @DisplayName("deve cadastrar eleitor com CPF válido")
    void deveCadastrarEleitor() {

        EleitorRepository repo = mock(EleitorRepository.class);

        when(repo.findByCpfValue("12345678909"))
                .thenReturn(Optional.empty());

        EleitorService service = new EleitorService(repo);

        Eleitor eleitor = service.cadastrar("Guilherme", new CPF("123.456.789-09"));

        assertNotNull(eleitor);
        assertEquals("Guilherme", eleitor.getNome());
        assertEquals("12345678909", eleitor.getCpf().getValue());
    }

    @Test
    @DisplayName("não deve cadastrar eleitor com CPF duplicado")
    void naoDeveCadastrarDuplicado() {

        EleitorRepository repo = mock(EleitorRepository.class);

        when(repo.findByCpfValue("12345678909"))
                .thenReturn(Optional.of(new Eleitor()));

        EleitorService service = new EleitorService(repo);

        assertThrows(IllegalArgumentException.class, () -> {
            service.cadastrar("Fulano", new CPF("123.456.789-09"));
        });
    }
}
