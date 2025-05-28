package com.tcc.gerenciador_projetos_tcc.repository;

import java.util.List;

import com.tcc.gerenciador_projetos_tcc.entity.TaskFiles;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskFilesRepository extends JpaRepository<TaskFiles, Long> {

    List<TaskFiles> findByTaskId(Long taskId);

}
