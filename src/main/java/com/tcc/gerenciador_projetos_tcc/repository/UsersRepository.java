package com.tcc.gerenciador_projetos_tcc.repository;

import com.tcc.gerenciador_projetos_tcc.entity.Alunos;
import com.tcc.gerenciador_projetos_tcc.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Integer> {


    Optional<Users> findByRaAndFaculdade(Integer ra, String faculdade);

    List<Users> findByNomeContainingIgnoreCaseAndFaculdade(String nome, String faculdade);

    @Modifying
    @Transactional
    @Query("DELETE FROM Users u WHERE u.ra = :ra")
    void deleteByRa(Integer ra);

    Users findByRa(Integer ra);

    Optional<Users> findByEmail(String email);



}
