package com.lotoanalyzer.backend.service;

import com.lotoanalyzer.backend.dto.GraficoSerieResponse;
import com.lotoanalyzer.backend.dto.SeriePonto;
import com.lotoanalyzer.backend.model.Concurso;
import com.lotoanalyzer.backend.repository.ConcursoRepository;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class GraficoService {

    private static final DateTimeFormatter MONTH_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM");

    private final ConcursoRepository concursoRepository;

    public GraficoService(ConcursoRepository concursoRepository) {
        this.concursoRepository = concursoRepository;
    }

    public GraficoSerieResponse frequenciaAnualNumero(int numero) {
        validarNumero(numero);
        Map<Integer, Long> agregados = new TreeMap<>();

        for (Concurso concurso : concursoRepository.findAllByOrderByNumeroConcursoAsc()) {
            int ano = concurso.getDataSorteio().getYear();
            if (concurso.getNumeros().contains(numero)) {
                agregados.put(ano, agregados.getOrDefault(ano, 0L) + 1L);
            } else {
                agregados.putIfAbsent(ano, 0L);
            }
        }

        List<SeriePonto> pontos = agregados.entrySet().stream()
                .map(e -> new SeriePonto(String.valueOf(e.getKey()), e.getValue()))
                .toList();

        return new GraficoSerieResponse("Frequencia anual - numero " + numero, pontos);
    }

    public GraficoSerieResponse frequenciaMensalNumero(int numero) {
        validarNumero(numero);
        Map<YearMonth, Long> agregados = new TreeMap<>();

        for (Concurso concurso : concursoRepository.findAllByOrderByNumeroConcursoAsc()) {
            YearMonth mes = YearMonth.from(concurso.getDataSorteio());
            if (concurso.getNumeros().contains(numero)) {
                agregados.put(mes, agregados.getOrDefault(mes, 0L) + 1L);
            } else {
                agregados.putIfAbsent(mes, 0L);
            }
        }

        List<SeriePonto> pontos = agregados.entrySet().stream()
                .map(e -> new SeriePonto(e.getKey().format(MONTH_FORMAT), e.getValue()))
                .toList();

        return new GraficoSerieResponse("Frequencia mensal - numero " + numero, pontos);
    }

    public Map<Integer, GraficoSerieResponse> pacoteAnualTop15(List<Integer> top15) {
        Map<Integer, GraficoSerieResponse> resposta = new LinkedHashMap<>();
        for (Integer numero : new ArrayList<>(top15)) {
            resposta.put(numero, frequenciaAnualNumero(numero));
        }
        return resposta;
    }

    private void validarNumero(int numero) {
        if (numero < 1 || numero > 25) {
            throw new IllegalArgumentException("Numero deve estar entre 1 e 25");
        }
    }
}
