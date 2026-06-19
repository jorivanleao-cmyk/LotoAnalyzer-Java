package com.lotoanalyzer.backend.model;

import java.util.List;

public class Jogo {

    private final List<Integer> dezenas;
    private final double scoreTotal;

    public Jogo(List<Integer> dezenas, double scoreTotal) {
        this.dezenas = dezenas;
        this.scoreTotal = scoreTotal;
    }

    public List<Integer> getDezenas() {
        return dezenas;
    }

    public double getScoreTotal() {
        return scoreTotal;
    }
}
