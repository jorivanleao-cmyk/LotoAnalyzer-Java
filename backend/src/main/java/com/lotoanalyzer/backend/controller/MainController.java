package com.lotoanalyzer.backend.controller;

import com.lotoanalyzer.backend.dto.NumeroFrequencia;
import com.lotoanalyzer.backend.dto.GraficoSerieResponse;
import com.lotoanalyzer.backend.dto.MlPrevisaoResponse;
import com.lotoanalyzer.backend.dto.TendenciaResponse;
import com.lotoanalyzer.backend.model.Jogo;
import com.lotoanalyzer.backend.service.EstatisticaService;
import com.lotoanalyzer.backend.service.GraficoService;
import com.lotoanalyzer.backend.service.GeradorJogosService;
import com.lotoanalyzer.backend.service.ImportacaoService;
import com.lotoanalyzer.backend.service.ProbabilidadeService;
import com.lotoanalyzer.backend.service.TendenciaService;
import com.lotoanalyzer.backend.service.ml.MachineLearningService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

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
    private final GraficoService graficoService;
    private final MachineLearningService machineLearningService;

    public MainController(
            ImportacaoService importacaoService,
            EstatisticaService estatisticaService,
            TendenciaService tendenciaService,
            ProbabilidadeService probabilidadeService,
            GeradorJogosService geradorJogosService,
            GraficoService graficoService,
            MachineLearningService machineLearningService
    ) {
        this.importacaoService = importacaoService;
        this.estatisticaService = estatisticaService;
        this.tendenciaService = tendenciaService;
        this.probabilidadeService = probabilidadeService;
        this.geradorJogosService = geradorJogosService;
        this.graficoService = graficoService;
        this.machineLearningService = machineLearningService;
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

    @GetMapping("/ml/random-forest")
    public MlPrevisaoResponse mlRandomForest() {
        return machineLearningService.preverRandomForest();
    }

    @GetMapping("/ml/xgboost")
    public MlPrevisaoResponse mlXgboost() {
        return machineLearningService.preverXgboost();
    }

    @GetMapping("/ml/rede-neural")
    public MlPrevisaoResponse mlRedeNeural() {
        return machineLearningService.preverRedeNeural();
    }

    @GetMapping("/graficos/anual/{numero}")
    public GraficoSerieResponse graficoAnual(@PathVariable int numero) {
        return graficoService.frequenciaAnualNumero(numero);
    }

    @GetMapping("/graficos/mensal/{numero}")
    public GraficoSerieResponse graficoMensal(@PathVariable int numero) {
        return graficoService.frequenciaMensalNumero(numero);
    }

    @GetMapping("/graficos/pacote-anual-top15")
    public Map<Integer, GraficoSerieResponse> pacoteGraficoAnualTop15() {
        List<Integer> top15 = machineLearningService.preverRandomForest().top15();
        return graficoService.pacoteAnualTop15(top15);
    }

    public record ImportacaoRequest(String csvUrl) {
    }
}
