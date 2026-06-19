package com.lotoanalyzer.backend.service;

import com.lotoanalyzer.backend.dto.NumeroFrequencia;
import com.lotoanalyzer.backend.model.Concurso;
import com.lotoanalyzer.backend.repository.ConcursoRepository;
import org.springframework.stereotype.Service;

import java.time.Month;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class EstatisticaService {

    private final ConcursoRepository concursoRepository;

    public EstatisticaService(ConcursoRepository concursoRepository) {
        this.concursoRepository = concursoRepository;
    }

    public List<NumeroFrequencia> numerosMaisSorteados() {
        return calcularFrequenciaGlobal().entrySet()
                .stream()
                .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed())
                .map(e -> new NumeroFrequencia(e.getKey(), e.getValue()))
                .toList();
    }

    public List<NumeroFrequencia> numerosMenosSorteados() {
        return calcularFrequenciaGlobal().entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .map(e -> new NumeroFrequencia(e.getKey(), e.getValue()))
                .toList();
    }

    public Map<Integer, List<NumeroFrequencia>> frequenciaPorAno() {
        Map<Integer, Map<Integer, Long>> porAno = new TreeMap<>();
        for (Concurso concurso : concursoRepository.findAllByOrderByNumeroConcursoAsc()) {
            int ano = concurso.getDataSorteio().getYear();
            porAno.putIfAbsent(ano, inicializarMapa());
            atualizarFrequencia(porAno.get(ano), concurso.getNumeros());
        }
        Map<Integer, List<NumeroFrequencia>> response = new LinkedHashMap<>();
        porAno.forEach((ano, freq) -> response.put(ano, ordenarFrequencia(freq)));
        return response;
    }

    public Map<String, List<NumeroFrequencia>> frequenciaPorMes() {
        Map<String, Map<Integer, Long>> porMes = new TreeMap<>();
        for (Concurso concurso : concursoRepository.findAllByOrderByNumeroConcursoAsc()) {
            Month mes = concurso.getDataSorteio().getMonth();
            String chave = "%02d-%s".formatted(mes.getValue(), mes.name());
            porMes.putIfAbsent(chave, inicializarMapa());
            atualizarFrequencia(porMes.get(chave), concurso.getNumeros());
        }
        Map<String, List<NumeroFrequencia>> response = new LinkedHashMap<>();
        porMes.forEach((mes, freq) -> response.put(mes, ordenarFrequencia(freq)));
        return response;
    }

    public Map<Integer, List<Integer>> frequenciaPorConcurso() {
        Map<Integer, List<Integer>> response = new LinkedHashMap<>();
        for (Concurso concurso : concursoRepository.findAllByOrderByNumeroConcursoAsc()) {
            List<Integer> numeros = new ArrayList<>(concurso.getNumeros());
            numeros.sort(Comparator.naturalOrder());
            response.put(concurso.getNumeroConcurso(), numeros);
        }
        return response;
    }

    private Map<Integer, Long> calcularFrequenciaGlobal() {
        Map<Integer, Long> frequencia = inicializarMapa();
        for (Concurso concurso : concursoRepository.findAll()) {
            atualizarFrequencia(frequencia, concurso.getNumeros());
        }
        return frequencia;
    }

    private Map<Integer, Long> inicializarMapa() {
        Map<Integer, Long> mapa = new HashMap<>();
        for (int i = 1; i <= 25; i++) {
            mapa.put(i, 0L);
        }
        return mapa;
    }

    private void atualizarFrequencia(Map<Integer, Long> frequencia, List<Integer> numeros) {
        for (Integer numero : numeros) {
            frequencia.computeIfPresent(numero, (k, v) -> v + 1L);
        }
    }

    private List<NumeroFrequencia> ordenarFrequencia(Map<Integer, Long> frequencia) {
        return frequencia.entrySet()
                .stream()
                .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed())
                .map(e -> new NumeroFrequencia(e.getKey(), e.getValue()))
                .toList();
    }
}
