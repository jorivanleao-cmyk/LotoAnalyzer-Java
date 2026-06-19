package com.lotoanalyzer.backend.service;

import com.lotoanalyzer.backend.model.Concurso;
import com.lotoanalyzer.backend.repository.ConcursoRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProbabilidadeService {

    private final ConcursoRepository concursoRepository;

    public ProbabilidadeService(ConcursoRepository concursoRepository) {
        this.concursoRepository = concursoRepository;
    }

    public Map<Integer, Double> calcularScorePorNumero() {
        List<Concurso> concursos = concursoRepository.findAllByOrderByNumeroConcursoAsc();
        List<Concurso> ultimos100 = concursos.stream()
                .skip(Math.max(0, concursos.size() - 100L))
                .toList();

        Map<Integer, Integer> frequenciaHistorica = inicializarInteiros();
        Map<Integer, Integer> frequenciaUltimos100 = inicializarInteiros();
        Map<Integer, Integer> atraso = calcularAtraso(concursos);
        Map<Integer, Integer> repeticaoPadrao = inicializarInteiros();

        for (Concurso concurso : concursos) {
            concurso.getNumeros().forEach(n -> frequenciaHistorica.computeIfPresent(n, (k, v) -> v + 1));
        }
        for (Concurso concurso : ultimos100) {
            concurso.getNumeros().forEach(n -> frequenciaUltimos100.computeIfPresent(n, (k, v) -> v + 1));
        }

        for (int i = 1; i < concursos.size(); i++) {
            List<Integer> atual = concursos.get(i).getNumeros();
            List<Integer> anterior = concursos.get(i - 1).getNumeros();
            atual.stream().filter(anterior::contains)
                    .forEach(n -> repeticaoPadrao.computeIfPresent(n, (k, v) -> v + 1));
        }

        double maxHistorica = Math.max(1, frequenciaHistorica.values().stream().mapToInt(Integer::intValue).max().orElse(1));
        double maxUltimos100 = Math.max(1, frequenciaUltimos100.values().stream().mapToInt(Integer::intValue).max().orElse(1));
        double maxAtraso = Math.max(1, atraso.values().stream().mapToInt(Integer::intValue).max().orElse(1));
        double maxRepeticao = Math.max(1, repeticaoPadrao.values().stream().mapToInt(Integer::intValue).max().orElse(1));

        Map<Integer, Double> score = new HashMap<>();
        for (int n = 1; n <= 25; n++) {
            double scoreNumero =
                    (frequenciaHistorica.get(n) / maxHistorica) * 0.40 +
                    (frequenciaUltimos100.get(n) / maxUltimos100) * 0.30 +
                    (atraso.get(n) / maxAtraso) * 0.20 +
                    (repeticaoPadrao.get(n) / maxRepeticao) * 0.10;
            score.put(n, scoreNumero);
        }
        return score;
    }

    private Map<Integer, Integer> calcularAtraso(List<Concurso> concursos) {
        Map<Integer, Integer> atraso = inicializarInteiros();
        int total = concursos.size();
        for (int n = 1; n <= 25; n++) {
            int ultimoIndice = -1;
            for (int i = total - 1; i >= 0; i--) {
                if (concursos.get(i).getNumeros().contains(n)) {
                    ultimoIndice = i;
                    break;
                }
            }
            atraso.put(n, ultimoIndice == -1 ? total : total - 1 - ultimoIndice);
        }
        return atraso;
    }

    private Map<Integer, Integer> inicializarInteiros() {
        Map<Integer, Integer> mapa = new HashMap<>();
        for (int i = 1; i <= 25; i++) {
            mapa.put(i, 0);
        }
        return mapa;
    }
}
