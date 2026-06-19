package com.lotoanalyzer.backend.service;

import com.lotoanalyzer.backend.model.Jogo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

@Service
public class GeradorJogosService {

    private final ProbabilidadeService probabilidadeService;
    private final Random random = new Random();

    public GeradorJogosService(ProbabilidadeService probabilidadeService) {
        this.probabilidadeService = probabilidadeService;
    }

    public Map<String, List<Jogo>> gerarJogos() {
        Map<Integer, Double> score = probabilidadeService.calcularScorePorNumero();
        List<Jogo> recomendados = gerarLista(score, 5, false);
        List<Jogo> alternativos = gerarLista(score, 10, true);
        return Map.of(
                "recomendados", recomendados,
                "alternativos", alternativos
        );
    }

    private List<Jogo> gerarLista(Map<Integer, Double> score, int quantidade, boolean alternativo) {
        List<Integer> ordenados = score.entrySet().stream()
                .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .toList();

        List<Jogo> jogos = new ArrayList<>();
        Set<String> assinaturas = new LinkedHashSet<>();

        int tentativas = 0;
        while (jogos.size() < quantidade && tentativas < 2000) {
            tentativas++;
            List<Integer> jogo = new ArrayList<>();
            int faixa = alternativo ? 20 : 17;

            while (jogo.size() < 15) {
                int indice = random.nextInt(Math.min(faixa, ordenados.size()));
                int numero = ordenados.get(indice);
                if (!jogo.contains(numero)) {
                    jogo.add(numero);
                }
            }

            jogo.sort(Comparator.naturalOrder());
            String assinatura = jogo.toString();
            if (assinaturas.add(assinatura)) {
                double scoreTotal = jogo.stream().mapToDouble(score::get).sum();
                jogos.add(new Jogo(jogo, scoreTotal));
            }
        }

        return jogos.stream()
                .sorted(Comparator.comparingDouble(Jogo::getScoreTotal).reversed())
                .limit(quantidade)
                .toList();
    }
}
