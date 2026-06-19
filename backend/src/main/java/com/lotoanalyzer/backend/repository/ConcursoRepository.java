package com.lotoanalyzer.backend.repository;

import com.lotoanalyzer.backend.model.Concurso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConcursoRepository extends JpaRepository<Concurso, Long> {

    Optional<Concurso> findByNumeroConcurso(Integer numeroConcurso);

    List<Concurso> findAllByOrderByNumeroConcursoAsc();

    List<Concurso> findTop100ByOrderByNumeroConcursoDesc();

    Optional<Concurso> findTopByOrderByNumeroConcursoDesc();
}
