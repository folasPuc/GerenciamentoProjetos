package com.tcc.gerenciador_projetos_tcc.repository;

import com.tcc.gerenciador_projetos_tcc.entity.Alunos;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AlunoRepository extends JpaRepository<Alunos, Integer> {

    boolean existsByRaAndFaculdade(Integer ra, String faculdade);


}
