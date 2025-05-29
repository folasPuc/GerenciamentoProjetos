package com.tcc.gerenciador_projetos_tcc.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
public class Task {

    public enum TaskStatus {
        TODO("A Fazer"),
        IN_PROGRESS("Em Progresso"),
        DONE("Concluída");

        private final String displayName;

        TaskStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String title;
    private String description;
    private String assignee;
    private int fileCount = 0;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Long groupId;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<TaskHistoryEntry> history = new ArrayList<>();

    public Task() {
        // Construtor padrão para JPA
    }

    public Task(String title, String description, String assignee, TaskStatus status, Long groupId, String creator) {
        this.title = title;
        this.description = description;
        this.assignee = assignee;
        this.status = status;
        this.groupId = groupId;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

        TaskHistoryEntry creationEntry = new TaskHistoryEntry("Criação da tarefa", creator, null, this);
        this.history.add(creationEntry);
    }

    public void updateStatus(TaskStatus newStatus, String user) {
        TaskStatus oldStatus = this.status;
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();

        String details = "De: " + oldStatus.getDisplayName() + " → Para: " + newStatus.getDisplayName();
        this.history.add(new TaskHistoryEntry("Alteração de status", user, details, this));
    }

    public void updateDetails(String newTitle, String newDescription, String newAssignee, String user) {
        StringBuilder changes = new StringBuilder();

        if (!this.title.equals(newTitle)) {
            changes.append("Título: '").append(this.title).append("' → '").append(newTitle).append("'\n");
            this.title = newTitle;
        }

        if (!this.description.equals(newDescription)) {
            changes.append("Descrição alterada\n");
            this.description = newDescription;
        }

        if (!this.assignee.equals(newAssignee)) {
            changes.append("Responsável: '").append(this.assignee).append("' → '").append(newAssignee).append("'");
            this.assignee = newAssignee;
        }

        this.updatedAt = LocalDateTime.now();

        if (changes.length() > 0) {
            this.history.add(new TaskHistoryEntry("Edição de tarefa", user, changes.toString(), this));
        }
    }

    public void addComment(String comment, String user) {
        this.history.add(new TaskHistoryEntry("Comentário", user, comment, this));
        this.updatedAt = LocalDateTime.now();
    }

    public void addFile(String fileName, String user) {
        this.history.add(new TaskHistoryEntry("Envio de arquivo", user, fileName, this));
        this.updatedAt = LocalDateTime.now();
    }

    public void removeFile(String fileName, String user) {
        this.history.add(new TaskHistoryEntry("Removeu um arquivo", user, fileName, this));
        this.updatedAt = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public List<TaskHistoryEntry> getHistory() {
        return history;
    }

    public void setHistory(List<TaskHistoryEntry> history) {
        this.history = history;
    }

    public void incrementFileCount() {
        this.fileCount++;
    }

    public void decrementFileCount() {
        this.fileCount--;
    }

    public void setFileCount(int count) {
        this.fileCount = count;
    }

    public int getFileCount() {
        return fileCount;
    }
}
