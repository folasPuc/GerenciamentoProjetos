package com.tcc.gerenciador_projetos_tcc.repository;

import com.tcc.gerenciador_projetos_tcc.entity.UsersPuc;
import com.tcc.gerenciador_projetos_tcc.entity.UsersUnicamp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UsersUnicampRepository extends JpaRepository<UsersUnicamp, Integer> {

    Optional<UsersUnicamp> findByRa(Integer ra);

    Optional<UsersUnicamp> findByEmail(String email);


}
