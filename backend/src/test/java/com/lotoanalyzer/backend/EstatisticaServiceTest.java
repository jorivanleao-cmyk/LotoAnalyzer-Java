package com.lotoanalyzer.backend;

import com.lotoanalyzer.backend.dto.NumeroFrequencia;
import com.lotoanalyzer.backend.model.Concurso;
import com.lotoanalyzer.backend.repository.ConcursoRepository;
import com.lotoanalyzer.backend.service.EstatisticaService;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EstatisticaServiceTest {

    @Test
    void deveCalcularNumerosMaisSorteados() {
        ConcursoRepository repository = mock(ConcursoRepository.class);
        when(repository.findAll()).thenReturn(List.of(
                Concurso.of(1, LocalDate.of(2025, 1, 1), List.of(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15)),
                Concurso.of(2, LocalDate.of(2025, 1, 2), List.of(1,2,3,4,5,16,17,18,19,20,21,22,23,24,25))
        ));

        EstatisticaService service = new EstatisticaService(repository);
        List<NumeroFrequencia> resultado = service.numerosMaisSorteados();

        assertEquals(1, resultado.getFirst().numero());
        assertEquals(2L, resultado.getFirst().frequencia());
    }
}
