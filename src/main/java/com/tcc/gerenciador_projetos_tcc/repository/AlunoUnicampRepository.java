package com.tcc.gerenciador_projetos_tcc.repository;

import com.tcc.gerenciador_projetos_tcc.entity.AlunoPuc;
import com.tcc.gerenciador_projetos_tcc.entity.AlunoUnicamp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AlunoUnicampRepository extends JpaRepository<AlunoUnicamp, Integer> {
    Optional<AlunoUnicamp> findByRa(Integer ra);

    //Metodo que retorna true se encontrar uma entrada na tabela com o RA fornecido
    boolean existsByRa(Integer ra);
}
