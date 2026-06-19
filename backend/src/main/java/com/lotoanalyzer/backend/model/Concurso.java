package com.lotoanalyzer.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "concursos")
public class Concurso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_concurso", nullable = false, unique = true)
    private Integer numeroConcurso;

    @Column(name = "data_sorteio", nullable = false)
    private LocalDate dataSorteio;

    @Column(nullable = false)
    private Integer n1;
    @Column(nullable = false)
    private Integer n2;
    @Column(nullable = false)
    private Integer n3;
    @Column(nullable = false)
    private Integer n4;
    @Column(nullable = false)
    private Integer n5;
    @Column(nullable = false)
    private Integer n6;
    @Column(nullable = false)
    private Integer n7;
    @Column(nullable = false)
    private Integer n8;
    @Column(nullable = false)
    private Integer n9;
    @Column(nullable = false)
    private Integer n10;
    @Column(nullable = false)
    private Integer n11;
    @Column(nullable = false)
    private Integer n12;
    @Column(nullable = false)
    private Integer n13;
    @Column(nullable = false)
    private Integer n14;
    @Column(nullable = false)
    private Integer n15;

    public List<Integer> getNumeros() {
        return List.of(n1, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11, n12, n13, n14, n15);
    }

    public static Concurso of(Integer numeroConcurso, LocalDate dataSorteio, List<Integer> numeros) {
        if (numeros == null || numeros.size() != 15) {
            throw new IllegalArgumentException("Concurso deve conter exatamente 15 dezenas");
        }
        Concurso concurso = new Concurso();
        concurso.setNumeroConcurso(numeroConcurso);
        concurso.setDataSorteio(dataSorteio);
        concurso.setN1(numeros.get(0));
        concurso.setN2(numeros.get(1));
        concurso.setN3(numeros.get(2));
        concurso.setN4(numeros.get(3));
        concurso.setN5(numeros.get(4));
        concurso.setN6(numeros.get(5));
        concurso.setN7(numeros.get(6));
        concurso.setN8(numeros.get(7));
        concurso.setN9(numeros.get(8));
        concurso.setN10(numeros.get(9));
        concurso.setN11(numeros.get(10));
        concurso.setN12(numeros.get(11));
        concurso.setN13(numeros.get(12));
        concurso.setN14(numeros.get(13));
        concurso.setN15(numeros.get(14));
        return concurso;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumeroConcurso() {
        return numeroConcurso;
    }

    public void setNumeroConcurso(Integer numeroConcurso) {
        this.numeroConcurso = numeroConcurso;
    }

    public LocalDate getDataSorteio() {
        return dataSorteio;
    }

    public void setDataSorteio(LocalDate dataSorteio) {
        this.dataSorteio = dataSorteio;
    }

    public Integer getN1() {
        return n1;
    }

    public void setN1(Integer n1) {
        this.n1 = n1;
    }

    public Integer getN2() {
        return n2;
    }

    public void setN2(Integer n2) {
        this.n2 = n2;
    }

    public Integer getN3() {
        return n3;
    }

    public void setN3(Integer n3) {
        this.n3 = n3;
    }

    public Integer getN4() {
        return n4;
    }

    public void setN4(Integer n4) {
        this.n4 = n4;
    }

    public Integer getN5() {
        return n5;
    }

    public void setN5(Integer n5) {
        this.n5 = n5;
    }

    public Integer getN6() {
        return n6;
    }

    public void setN6(Integer n6) {
        this.n6 = n6;
    }

    public Integer getN7() {
        return n7;
    }

    public void setN7(Integer n7) {
        this.n7 = n7;
    }

    public Integer getN8() {
        return n8;
    }

    public void setN8(Integer n8) {
        this.n8 = n8;
    }

    public Integer getN9() {
        return n9;
    }

    public void setN9(Integer n9) {
        this.n9 = n9;
    }

    public Integer getN10() {
        return n10;
    }

    public void setN10(Integer n10) {
        this.n10 = n10;
    }

    public Integer getN11() {
        return n11;
    }

    public void setN11(Integer n11) {
        this.n11 = n11;
    }

    public Integer getN12() {
        return n12;
    }

    public void setN12(Integer n12) {
        this.n12 = n12;
    }

    public Integer getN13() {
        return n13;
    }

    public void setN13(Integer n13) {
        this.n13 = n13;
    }

    public Integer getN14() {
        return n14;
    }

    public void setN14(Integer n14) {
        this.n14 = n14;
    }

    public Integer getN15() {
        return n15;
    }

    public void setN15(Integer n15) {
        this.n15 = n15;
    }
}
