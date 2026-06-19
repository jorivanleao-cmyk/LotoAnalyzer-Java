package com.lotoanalyzer.backend.dto;

import java.util.List;

public record GraficoSerieResponse(String titulo, List<SeriePonto> pontos) {
}
