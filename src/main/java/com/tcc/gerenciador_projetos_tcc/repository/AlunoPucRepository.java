package com.tcc.gerenciador_projetos_tcc.repository;

import com.tcc.gerenciador_projetos_tcc.entity.AlunoPuc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AlunoPucRepository extends JpaRepository<AlunoPuc, Integer> {
    Optional<AlunoPuc> findByRa(Integer ra);

    //Metodo que retorna true se encontrar uma entrada na tabela com o RA fornecido
    boolean existsByRa(Integer ra);
}
