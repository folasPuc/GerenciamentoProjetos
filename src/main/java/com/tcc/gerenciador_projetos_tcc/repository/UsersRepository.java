package com.tcc.gerenciador_projetos_tcc.repository;

import com.tcc.gerenciador_projetos_tcc.entity.Alunos;
import com.tcc.gerenciador_projetos_tcc.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Integer> {


    Optional<Users> findByRaAndFaculdade(Integer ra, String faculdade);

}
