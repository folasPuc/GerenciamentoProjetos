package com.tcc.gerenciador_projetos_tcc.repository;

import com.tcc.gerenciador_projetos_tcc.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Integer> {

    List<Task> findByGroupId(Integer groupId);


    @Modifying
    @Transactional
    void deleteAllByGroupId(Integer groupId);
}
