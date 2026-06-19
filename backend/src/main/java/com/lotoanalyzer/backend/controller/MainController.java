package com.lotoanalyzer.backend.controller;

import com.lotoanalyzer.backend.dto.NumeroFrequencia;
import com.lotoanalyzer.backend.dto.TendenciaResponse;
import com.lotoanalyzer.backend.model.Jogo;
import com.lotoanalyzer.backend.service.EstatisticaService;
import com.lotoanalyzer.backend.service.GeradorJogosService;
import com.lotoanalyzer.backend.service.ImportacaoService;
import com.lotoanalyzer.backend.service.ProbabilidadeService;
import com.lotoanalyzer.backend.service.TendenciaService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/loto")
@CrossOrigin(origins = "*")
@Validated
public class MainController {

    private final ImportacaoService importacaoService;
    private final EstatisticaService estatisticaService;
    private final TendenciaService tendenciaService;
    private final ProbabilidadeService probabilidadeService;
    private final GeradorJogosService geradorJogosService;

    public MainController(
            ImportacaoService importacaoService,
            EstatisticaService estatisticaService,
            TendenciaService tendenciaService,
            ProbabilidadeService probabilidadeService,
            GeradorJogosService geradorJogosService
    ) {
        this.importacaoService = importacaoService;
        this.estatisticaService = estatisticaService;
        this.tendenciaService = tendenciaService;
        this.probabilidadeService = probabilidadeService;
        this.geradorJogosService = geradorJogosService;
    }

    @PostMapping("/importar")
    public ResponseEntity<Map<String, Object>> importar(@RequestBody(required = false) ImportacaoRequest body) {
        String url = body == null ? null : body.csvUrl();
        int inseridos = importacaoService.importarHistorico(url);
        return ResponseEntity.ok(Map.of(
                "status", "ok",
                "concursosImportados", inseridos
        ));
    }

    @GetMapping("/estatisticas/mais-sorteados")
    public List<NumeroFrequencia> maisSorteados() {
        return estatisticaService.numerosMaisSorteados();
    }

    @GetMapping("/estatisticas/menos-sorteados")
    public List<NumeroFrequencia> menosSorteados() {
        return estatisticaService.numerosMenosSorteados();
    }

    @GetMapping("/estatisticas/frequencia-ano")
    public Map<Integer, List<NumeroFrequencia>> frequenciaAno() {
        return estatisticaService.frequenciaPorAno();
    }

    @GetMapping("/estatisticas/frequencia-mes")
    public Map<String, List<NumeroFrequencia>> frequenciaMes() {
        return estatisticaService.frequenciaPorMes();
    }

    @GetMapping("/estatisticas/frequencia-concurso")
    public Map<Integer, List<Integer>> frequenciaConcurso() {
        return estatisticaService.frequenciaPorConcurso();
    }

    @GetMapping("/tendencias")
    public TendenciaResponse tendencias() {
        return tendenciaService.calcularTendencias();
    }

    @GetMapping("/score")
    public Map<Integer, Double> score() {
        return probabilidadeService.calcularScorePorNumero();
    }

    @GetMapping("/jogos")
    public Map<String, List<Jogo>> jogos() {
        return geradorJogosService.gerarJogos();
    }

    public record ImportacaoRequest(String csvUrl) {
    }
}
