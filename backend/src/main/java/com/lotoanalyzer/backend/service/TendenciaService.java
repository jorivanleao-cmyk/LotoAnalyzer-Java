package com.lotoanalyzer.backend.service;

import com.lotoanalyzer.backend.dto.NumeroFrequencia;
import com.lotoanalyzer.backend.dto.TendenciaResponse;
import com.lotoanalyzer.backend.model.Concurso;
import com.lotoanalyzer.backend.repository.ConcursoRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TendenciaService {

    private final ConcursoRepository concursoRepository;

    public TendenciaService(ConcursoRepository concursoRepository) {
        this.concursoRepository = concursoRepository;
    }

    public TendenciaResponse calcularTendencias() {
        List<Concurso> concursos = concursoRepository.findAllByOrderByNumeroConcursoAsc();
        List<Concurso> ultimos100 = concursos.stream().skip(Math.max(0, concursos.size() - 100L)).toList();

        Map<Integer, Long> freq100 = new HashMap<>();
        for (int i = 1; i <= 25; i++) {
            freq100.put(i, 0L);
        }
        for (Concurso concurso : ultimos100) {
            concurso.getNumeros().forEach(n -> freq100.computeIfPresent(n, (k, v) -> v + 1L));
        }

        List<NumeroFrequencia> quentes = freq100.entrySet().stream()
                .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed())
                .limit(10)
                .map(e -> new NumeroFrequencia(e.getKey(), e.getValue()))
                .toList();

        List<NumeroFrequencia> frios = freq100.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .limit(10)
                .map(e -> new NumeroFrequencia(e.getKey(), e.getValue()))
                .toList();

        Map<Integer, Integer> atrasados = calcularAtrasados(concursos);
        List<Integer> repetidos = repeticaoUltimoConcurso(concursos);
        Map<String, Long> paresImpares = calcularParesImpares(concursos);
        Map<String, Integer> somaDezenas = calcularSomaDezenas(concursos);

        return new TendenciaResponse(quentes, frios, atrasados, repetidos, paresImpares, somaDezenas);
    }

    private Map<Integer, Integer> calcularAtrasados(List<Concurso> concursos) {
        Map<Integer, Integer> atraso = new LinkedHashMap<>();
        for (int n = 1; n <= 25; n++) {
            int valor = concursos.size();
            for (int i = concursos.size() - 1; i >= 0; i--) {
                if (concursos.get(i).getNumeros().contains(n)) {
                    valor = concursos.size() - 1 - i;
                    break;
                }
            }
            atraso.put(n, valor);
        }
        return atraso.entrySet().stream()
                .sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }

    private List<Integer> repeticaoUltimoConcurso(List<Concurso> concursos) {
        if (concursos.size() < 2) {
            return List.of();
        }
        List<Integer> ultimo = concursos.get(concursos.size() - 1).getNumeros();
        List<Integer> penultimo = concursos.get(concursos.size() - 2).getNumeros();

        List<Integer> repetidos = new ArrayList<>();
        for (Integer numero : ultimo) {
            if (penultimo.contains(numero)) {
                repetidos.add(numero);
            }
        }
        repetidos.sort(Comparator.naturalOrder());
        return repetidos;
    }

    private Map<String, Long> calcularParesImpares(List<Concurso> concursos) {
        long pares = 0;
        long impares = 0;
        for (Concurso concurso : concursos) {
            for (Integer numero : concurso.getNumeros()) {
                if (numero % 2 == 0) {
                    pares++;
                } else {
                    impares++;
                }
            }
        }
        Map<String, Long> response = new LinkedHashMap<>();
        response.put("pares", pares);
        response.put("impares", impares);
        return response;
    }

    private Map<String, Integer> calcularSomaDezenas(List<Concurso> concursos) {
        if (concursos.isEmpty()) {
            return Map.of("min", 0, "max", 0, "media", 0);
        }
        List<Integer> somas = concursos.stream()
                .map(c -> c.getNumeros().stream().mapToInt(Integer::intValue).sum())
                .toList();

        int min = somas.stream().min(Integer::compareTo).orElse(0);
        int max = somas.stream().max(Integer::compareTo).orElse(0);
        int media = (int) Math.round(somas.stream().mapToInt(Integer::intValue).average().orElse(0));

        Map<String, Integer> response = new LinkedHashMap<>();
        response.put("min", min);
        response.put("max", max);
        response.put("media", media);
        return response;
    }
}
