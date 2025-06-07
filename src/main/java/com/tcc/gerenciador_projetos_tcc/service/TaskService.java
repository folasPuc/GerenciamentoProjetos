package com.tcc.gerenciador_projetos_tcc.service;

import com.tcc.gerenciador_projetos_tcc.entity.Task;
import com.tcc.gerenciador_projetos_tcc.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    public Task createTask(String title, String description, String assignee, Task.TaskStatus status, Long groupId, String creator) {

        Task task = new Task(title, description, assignee, status, groupId,creator);
        return taskRepository.save(task);
    }

    public Task updateTaskStatus(Integer taskId, Task.TaskStatus newStatus, String user) {
        Task task = getTaskById(taskId);
        task.updateStatus(newStatus, user);
        return taskRepository.save(task);
    }

    public Task updateTaskDetails(Integer taskId, String newTitle, String newDescription, String newAssignee, String user) {
        Task task = getTaskById(taskId);
        task.updateDetails(newTitle, newDescription, newAssignee, user);
        return taskRepository.save(task);
    }

    public Task addCommentToTask(Integer taskId, String comment, String user) {
        Task task = getTaskById(taskId);
        task.addComment(comment, user);
        return taskRepository.save(task);
    }

    public Task getTaskById(Integer taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada"));
    }

    public List<Task> getTasksByGroup(Integer groupId) {
        return taskRepository.findByGroupId(groupId);
    }

    public void deleteTask(Integer taskId) {
         taskRepository.deleteById(taskId);
    }

    public void deleteAllTasksByGroupId(Integer groupId) {
        taskRepository.deleteAllByGroupId(groupId);

    }

    public Task saveTask(Task task) {
       return taskRepository.save(task);
    }

}
