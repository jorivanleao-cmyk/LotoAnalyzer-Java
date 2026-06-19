package com.lotoanalyzer.backend.service;

import com.lotoanalyzer.backend.model.Concurso;
import com.lotoanalyzer.backend.repository.ConcursoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
public class ImportacaoService {

    private static final DateTimeFormatter[] FORMATADORES = {
            DateTimeFormatter.ofPattern("d/M/yyyy", Locale.of("pt", "BR")),
            DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.of("pt", "BR")),
            DateTimeFormatter.ISO_LOCAL_DATE
    };

    private final ConcursoRepository concursoRepository;
    private final RestTemplate restTemplate;

    @Value("${lotoanalyzer.lotofacil.csv-url:https://servicebus2.caixa.gov.br/portaldeloterias/api/resultados/download?modalidade=Lotofacil}")
    private String csvUrlPadrao;

    public ImportacaoService(ConcursoRepository concursoRepository) {
        this.concursoRepository = concursoRepository;
        this.restTemplate = new RestTemplate();
    }

    public int importarHistorico(String csvUrl) {
        String urlPadrao = Objects.requireNonNullElse(
            csvUrlPadrao,
            "https://servicebus2.caixa.gov.br/portaldeloterias/api/resultados/download?modalidade=Lotofacil"
        );
        String origem = (csvUrl == null || csvUrl.isBlank()) ? urlPadrao : csvUrl;
        ResponseEntity<String> response = restTemplate.getForEntity(Objects.requireNonNull(origem), String.class);
        String body = response.getBody();
        if (body == null || body.isBlank()) {
            return 0;
        }

        List<Concurso> parsed = parseCsv(body);
        int inseridos = 0;
        for (Concurso concurso : parsed) {
            if (concursoRepository.findByNumeroConcurso(concurso.getNumeroConcurso()).isEmpty()) {
                concursoRepository.save(concurso);
                inseridos++;
            }
        }
        return inseridos;
    }

    private List<Concurso> parseCsv(String csvContent) {
        List<Concurso> concursos = new ArrayList<>();
        String[] linhas = csvContent.split("\\R");

        for (String linha : linhas) {
            if (linha == null || linha.isBlank()) {
                continue;
            }
            String normalizada = linha.replace(";", ",").replace("\"", "").trim();
            if (!Character.isDigit(normalizada.charAt(0))) {
                continue;
            }

            String[] colunas = normalizada.split(",");
            if (colunas.length < 17) {
                continue;
            }

            try {
                Integer numeroConcurso = Integer.parseInt(colunas[0].trim());
                LocalDate data = parseData(colunas[1].trim());

                List<Integer> dezenas = new ArrayList<>();
                for (int i = 2; i <= 16; i++) {
                    dezenas.add(Integer.parseInt(colunas[i].trim()));
                }
                dezenas.sort(Comparator.naturalOrder());
                concursos.add(Concurso.of(numeroConcurso, data, dezenas));
            } catch (Exception ignored) {
                // Ignora linhas fora do layout esperado e continua a importacao.
            }
        }
        return concursos;
    }

    private LocalDate parseData(String valor) {
        for (DateTimeFormatter formatador : FORMATADORES) {
            try {
                return LocalDate.parse(valor, formatador);
            } catch (DateTimeParseException ignored) {
                // Tenta proximo formatador.
            }
        }
        throw new IllegalArgumentException("Data invalida: " + valor);
    }
}
