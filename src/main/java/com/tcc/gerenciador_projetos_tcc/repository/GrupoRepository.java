package com.tcc.gerenciador_projetos_tcc.repository;

import com.tcc.gerenciador_projetos_tcc.entity.Grupo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GrupoRepository extends JpaRepository<Grupo, Long> {

    List<Grupo> findByUsuarios_Id(Integer userId);
}
