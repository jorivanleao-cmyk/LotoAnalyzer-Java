package com.lotoanalyzer.backend.dto;

import java.util.List;
import java.util.Map;

public record MlPrevisaoResponse(
        String algoritmo,
        List<Integer> top15,
        Map<Integer, Double> scorePorNumero
) {
}
