package com.lotoanalyzer.backend.dto;

import java.util.List;
import java.util.Map;

public record TendenciaResponse(
        List<NumeroFrequencia> numerosQuentes,
        List<NumeroFrequencia> numerosFrios,
        Map<Integer, Integer> atrasados,
        List<Integer> repetidosUltimoConcurso,
        Map<String, Long> paresImpares,
        Map<String, Integer> somaDezenas
) {
}
