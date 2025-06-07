package com.tcc.gerenciador_projetos_tcc.repository;

import java.util.List;

import com.tcc.gerenciador_projetos_tcc.entity.TaskFiles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

public interface TaskFilesRepository extends JpaRepository<TaskFiles, Long> {

    List<TaskFiles> findByTaskId(Long taskId);


    @Modifying
    @Transactional
    void deleteByTaskId(Long taskId);

}
