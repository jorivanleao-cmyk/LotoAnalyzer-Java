package com.lotoanalyzer.backend.service.ml;

import com.lotoanalyzer.backend.dto.MlPrevisaoResponse;
import com.lotoanalyzer.backend.model.Concurso;
import com.lotoanalyzer.backend.repository.ConcursoRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Service
public class MachineLearningService {

    private final ConcursoRepository concursoRepository;

    public MachineLearningService(ConcursoRepository concursoRepository) {
        this.concursoRepository = concursoRepository;
    }

    public MlPrevisaoResponse preverRandomForest() {
        List<Concurso> concursos = concursoRepository.findAllByOrderByNumeroConcursoAsc();
        FeatureSet featureSet = extrairFeatures(concursos);

        Map<Integer, Double> score = new LinkedHashMap<>();
        for (int numero = 1; numero <= 25; numero++) {
            double s =
                    featureSet.historicaNorm().get(numero) * 0.33 +
                    featureSet.ultimos100Norm().get(numero) * 0.34 +
                    featureSet.repeticaoNorm().get(numero) * 0.18 +
                    featureSet.atrasoNorm().get(numero) * 0.15;
            score.put(numero, limitar(s));
        }

        return montarResposta("random-forest-surrogate", score);
    }

    public MlPrevisaoResponse preverXgboost() {
        List<Concurso> concursos = concursoRepository.findAllByOrderByNumeroConcursoAsc();
        FeatureSet featureSet = extrairFeatures(concursos);

        Map<Integer, Double> score = new LinkedHashMap<>();
        for (int numero = 1; numero <= 25; numero++) {
            double stage1 = featureSet.historicaNorm().get(numero) * 0.45 + featureSet.ultimos100Norm().get(numero) * 0.35;
            double residual = (featureSet.repeticaoNorm().get(numero) * 0.12) + (featureSet.atrasoNorm().get(numero) * 0.08);
            score.put(numero, limitar(stage1 + residual));
        }

        return montarResposta("xgboost-surrogate", score);
    }

    public MlPrevisaoResponse preverRedeNeural() {
        List<Concurso> concursos = concursoRepository.findAllByOrderByNumeroConcursoAsc();
        FeatureSet featureSet = extrairFeatures(concursos);

        Map<Integer, Double> score = new LinkedHashMap<>();
        for (int numero = 1; numero <= 25; numero++) {
            double hiddenA = relu(featureSet.historicaNorm().get(numero) * 0.7 + featureSet.repeticaoNorm().get(numero) * 0.3 - 0.25);
            double hiddenB = relu(featureSet.ultimos100Norm().get(numero) * 0.6 + featureSet.atrasoNorm().get(numero) * 0.4 - 0.20);
            double output = sigmoid(hiddenA * 0.55 + hiddenB * 0.45);
            score.put(numero, output);
        }

        return montarResposta("rede-neural-surrogate", score);
    }

    private FeatureSet extrairFeatures(List<Concurso> concursos) {
        List<Concurso> ultimos100 = concursos.stream()
                .skip(Math.max(0, concursos.size() - 100L))
                .toList();

        Map<Integer, Integer> historica = zerado();
        Map<Integer, Integer> ultimos100Freq = zerado();
        Map<Integer, Integer> repeticao = zerado();
        Map<Integer, Integer> atraso = calcularAtraso(concursos);

        concursos.forEach(c -> c.getNumeros().forEach(n -> historica.computeIfPresent(n, (k, v) -> v + 1)));
        ultimos100.forEach(c -> c.getNumeros().forEach(n -> ultimos100Freq.computeIfPresent(n, (k, v) -> v + 1)));

        for (int i = 1; i < concursos.size(); i++) {
            List<Integer> atual = concursos.get(i).getNumeros();
            List<Integer> anterior = concursos.get(i - 1).getNumeros();
            atual.stream().filter(anterior::contains)
                    .forEach(n -> repeticao.computeIfPresent(n, (k, v) -> v + 1));
        }

        return new FeatureSet(
                normalizar(historica),
                normalizar(ultimos100Freq),
                normalizar(repeticao),
                normalizar(atraso)
        );
    }

    private Map<Integer, Integer> calcularAtraso(List<Concurso> concursos) {
        Map<Integer, Integer> atraso = zerado();
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

    private Map<Integer, Integer> zerado() {
        Map<Integer, Integer> mapa = new HashMap<>();
        IntStream.rangeClosed(1, 25).forEach(n -> mapa.put(n, 0));
        return mapa;
    }

    private Map<Integer, Double> normalizar(Map<Integer, Integer> origem) {
        double max = Math.max(1, origem.values().stream().mapToInt(Integer::intValue).max().orElse(1));
        Map<Integer, Double> normalizado = new LinkedHashMap<>();
        origem.forEach((k, v) -> normalizado.put(k, v / max));
        return normalizado;
    }

    private MlPrevisaoResponse montarResposta(String algoritmo, Map<Integer, Double> score) {
        List<Integer> top15 = score.entrySet().stream()
                .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
                .limit(15)
                .map(Map.Entry::getKey)
                .toList();

        Map<Integer, Double> scoreOrdenado = score.entrySet().stream()
                .sorted(Map.Entry.<Integer, Double>comparingByValue(Comparator.reverseOrder()))
                .collect(LinkedHashMap::new,
                        (map, e) -> map.put(e.getKey(), arredondar(e.getValue())),
                        Map::putAll);

        return new MlPrevisaoResponse(algoritmo, top15, scoreOrdenado);
    }

    private double relu(double x) {
        return Math.max(0, x);
    }

    private double sigmoid(double x) {
        return 1.0 / (1.0 + Math.exp(-x));
    }

    private double limitar(double x) {
        return Math.max(0, Math.min(1, x));
    }

    private double arredondar(double valor) {
        return Math.round(valor * 10000.0) / 10000.0;
    }

    private record FeatureSet(
            Map<Integer, Double> historicaNorm,
            Map<Integer, Double> ultimos100Norm,
            Map<Integer, Double> repeticaoNorm,
            Map<Integer, Double> atrasoNorm
    ) {
    }
}
