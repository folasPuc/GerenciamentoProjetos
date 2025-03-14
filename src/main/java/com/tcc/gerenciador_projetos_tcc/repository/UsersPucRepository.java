package com.tcc.gerenciador_projetos_tcc.repository;

import com.tcc.gerenciador_projetos_tcc.entity.UsersPuc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UsersPucRepository extends JpaRepository<UsersPuc, Integer> {

    Optional<UsersPuc> findByRa(Integer ra);

    Optional<UsersPuc> findByEmail(String email);


}
