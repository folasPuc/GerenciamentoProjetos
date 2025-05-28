package com.tcc.gerenciador_projetos_tcc.service;

import java.util.List;

import com.tcc.gerenciador_projetos_tcc.entity.TaskFiles;
import com.tcc.gerenciador_projetos_tcc.repository.TaskFilesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskFilesService {

    private final TaskFilesRepository taskFileRepository;

    public TaskFilesService(TaskFilesRepository taskFileRepository) {
        this.taskFileRepository = taskFileRepository;
    }

    public void saveToDatabase(Long taskId, String filename, byte[] file) {
        TaskFiles taskFile = new TaskFiles(taskId, filename, file);
        taskFileRepository.save(taskFile);
    }

    public List<TaskFiles> getFilesByTaskId(Long taskId) {
        return taskFileRepository.findByTaskId(taskId);
    }

}
